#!/bin/bash

cd "$WORKING_DIR" || {
  echo "Error moving to the project's root directory."
  exit 1
}

### VERIFYING CA AND CSR CERTIFICATES FILES
CA_CERTS_DIR="$WORKING_DIR"/utils/certs
TASKS_CERTS_DIR=utils/certs/"$AWS_WORKLOADS_ENV"
if [ ! -f "$CA_CERTS_DIR"/ca-cert.pem ] || [ ! -f "$TASKS_CERTS_DIR"/server-key.pem ] || [ ! -f "$TASKS_CERTS_DIR"/server-cert-"$AWS_WORKLOADS_ENV".pem ]; then
  echo ""
  echo "Error: Not TLS certificates was found for the '$AWS_WORKLOADS_ENV' environment."
  echo "You can create <TLS Certificates> using the 'Helper Menu', option 3."
  exit 1
fi

### REVERTING CONFIGURATION FILES
sh "$WORKING_DIR"/utils/scripts/helper/1_revert-automated-scripts.sh

### READING SERVER DOMAIN NAME AND SERVER FQDN
echo ""
read -r -p 'Enter the <Domain Name> used in your <CSR> certificate: ' server_domain_name
if [ -z "$server_domain_name" ]; then
  echo "Error: The <Domain Name> is required."
  exit 1
fi
server_fqdn="$AWS_WORKLOADS_ENV.$server_domain_name"

### UPDATE API MANIFEST FILE WITH SERVER FQDN
sed -i'.bak' -e "s/server_domain_name/$server_domain_name/g; s/server_fqdn/$server_fqdn/g;"  \
      "$WORKING_DIR"/copilot/api/manifest.yml
rm -f "$WORKING_DIR"/copilot/api/manifest.yml.bak

### ASKING TO STORE ALB ACCESS-LOGS
sh "$WORKING_DIR"/utils/scripts/helper/0_a_create-s3-bucket-for-alb-logs.sh

### ASKING TO PRUNE DOCKER SYSTEM
read -r -p "Do you want to prune your docker system? [y/N] " response
case $response in
  [yY])
    sh "$WORKING_DIR"/utils/scripts/helper/2_docker-system-prune.sh
    ;;
esac

echo ""
echo "GENERATING DOCKER BUILDER IMAGE FOR LAMBDA FUNCTION..."
echo ""
docker build -t hiperium/native-image-builder -f utils/docker/sam-builder/Dockerfile .
echo ""
echo "DONE!"

echo ""
echo "GETTING INFORMATION FROM AWS. PLEASE WAIT..."

### GETTING CSR CERTIFICATE ARN
acm_arn=$(aws acm list-certificates       \
  --includes  keyTypes=EC_prime256v1      \
  --profile "$AWS_WORKLOADS_PROFILE"      \
  --output text                           \
  --query "CertificateSummaryList[?contains(DomainName, '$server_domain_name')].[CertificateArn]")
if [ -z "$acm_arn" ]; then
  echo ""
  echo "Error: Not ACM Certificate was found for domain: '$server_domain_name'."
  echo "You can import your <CSR> certificates using the 'Helper Menu', option 4."
  sh "$WORKING_DIR"/utils/scripts/helper/1_revert-automated-scripts.sh
  exit 1
fi

### GETTING COGNITO USER POOL ID
cognito_user_pool_id=$(aws cognito-idp list-user-pools --max-results 1 --output text  \
  --query "UserPools[?contains(Name, 'CityUserPool')].[Id]"                           \
  --profile "$AWS_IDP_PROFILE")
if [ -z "$cognito_user_pool_id" ]; then
  echo ""
  echo "Error: Not Cognito User Pool ID was found with name: 'CityUserPool'."
  sh "$WORKING_DIR"/utils/scripts/helper/1_revert-automated-scripts.sh
  exit 0
fi

### UPDATING API MANIFEST FILE WITH COGNITO USER POOL ID
idp_aws_region=$(aws configure get region --profile "$AWS_IDP_PROFILE")
sed -i'.bak' -e "s/idp_aws_region/$idp_aws_region/g; s/cognito_user_pool_id/$cognito_user_pool_id/g"  \
      "$WORKING_DIR"/copilot/api/manifest.yml
rm -f "$WORKING_DIR"/copilot/api/manifest.yml.bak

### UPDATING ENV MANIFEST FILE WITH ACM ARN
workloads_aws_region=$(aws configure get region --profile "$AWS_WORKLOADS_PROFILE")
workloads_aws_account_id=$(aws configure get sso_account_id --profile "$AWS_WORKLOADS_PROFILE")
acm_certificate_number=$(echo "$acm_arn" | cut -d'/' -f2)
sed -i'.bak' -e "s/workloads_aws_region/$workloads_aws_region/g; s/workloads_aws_account_id/$workloads_aws_account_id/g; s/acm_certificate_number/$acm_certificate_number/g" \
      "$WORKING_DIR"/copilot/environments/"$AWS_WORKLOADS_ENV"/manifest.yml
rm -f "$WORKING_DIR"/copilot/environments/"$AWS_WORKLOADS_ENV"/manifest.yml.bak

### UPDATING ENVOY CONFIGURATION FILE FOR AWS
cat "$WORKING_DIR"/utils/templates/docker/envoy/envoy-aws.yaml > "$WORKING_DIR"/src/city-tasks-api-proxy/envoy.yaml
echo ""
echo "DONE!"

echo ""
echo "INITIALIZING SAM PROJECT ON AWS..."
sam init                        \
  --runtime 'provided.al2'      \
  --architecture 'arm64'        \
  --dependency-manager 'maven'  \
  --no-tracing                  \
  --no-application-insights     \
  --no-beta-features

echo ""
echo "BUILDING SAM PROJECT..."
sam build --config-env "$AWS_WORKLOADS_ENV"

echo ""
echo "DEPLOYING SAM PROJECT INTO AWS..."
sam deploy                                \
  --config-env "$AWS_WORKLOADS_ENV"       \
  --disable-rollback                      \
  --profile "$AWS_WORKLOADS_PROFILE"

echo ""
echo "INITIALIZING COPILOT STACK INTO AWS..."
copilot init                              \
  --app city-tasks                        \
  --name api                              \
  --type 'Load Balanced Web Service'      \
  --port 8080                             \
  --tag '1.7.0'                           \
  --dockerfile './src/city-tasks-api/Dockerfile'
echo ""
echo "DONE!"

echo ""
echo "INITIALIZING ENVIRONMENT INTO AWS..."
copilot env init                          \
  --app city-tasks                        \
  --name "$AWS_WORKLOADS_ENV"             \
  --profile "$AWS_WORKLOADS_PROFILE"      \
  --default-config
echo ""
echo "DONE!"

echo ""
echo "DEPLOYING ENVIRONMENT NETWORKING INTO AWS..."
copilot env deploy                        \
  --app city-tasks                        \
  --name "$AWS_WORKLOADS_ENV"
echo ""
echo "DONE!"

echo ""
echo "DEPLOYING CONTAINER APPLICATION INTO AWS..."
copilot deploy                            \
  --app city-tasks                        \
  --name api                              \
  --env "$AWS_WORKLOADS_ENV"              \
  --tag '1.7.0'                           \
  --no-rollback                           \
  --resource-tags project=Hiperium,copilot-application-type=api,copilot-application-version=1.7.0
echo ""
echo "DONE!"

echo ""
echo "ASSIGNING EVENTBRIDGE IAM-POLICY TO ECS-TASK..."
sed -i'.bak' -e "s/aws_region/$workloads_aws_region/g; s/aws_account_id/$workloads_aws_account_id/g" \
      "$WORKING_DIR"/utils/aws/iam/ecs-task-eventbridge-put-policy.json
rm -f "$WORKING_DIR"/utils/aws/iam/ecs-task-eventbridge-put-policy.json.bak

ecs_task_role_name=$(aws iam list-roles --output text \
  --query "Roles[?contains(RoleName, 'city-tasks-$AWS_WORKLOADS_ENV-api-TaskRole')].[RoleName]" \
  --profile "$AWS_WORKLOADS_PROFILE")
if [ -z "$ecs_task_role_name" ]; then
  echo ""
  echo "WARNING: ECS Task Role was not found with name: 'city-tasks-$AWS_WORKLOADS_ENV-api'."
  echo "         Please, assign the following IAM-Policy to the ECS Task Role manually:"
  echo ""
  cat "$WORKING_DIR"/utils/aws/iam/ecs-task-eventbridge-put-policy.json
  echo ""
else
  aws iam put-role-policy --role-name "$ecs_task_role_name"                     \
    --policy-name city-tasks-"$AWS_WORKLOADS_ENV"-api-EventBridge-PutPolicy     \
    --policy-document file://"$WORKING_DIR"/utils/aws/iam/ecs-task-eventbridge-put-policy.json \
    --profile "$AWS_WORKLOADS_PROFILE"
fi
echo "DONE!"

### LOADING DEVICE TEST DATA INTO DYNAMODB
if [ "$AWS_WORKLOADS_ENV" == "dev" ]; then
  echo ""
  echo "WRITING DEVICE TESTING DATA INTO DYNAMODB..."
  aws dynamodb batch-write-item \
      --request-items file://"$WORKING_DIR"/utils/aws/dynamodb/devices-test-data.json \
      --profile "$AWS_WORKLOADS_PROFILE"
  echo "DONE!"
fi

echo ""
echo "GETTING ALB DOMAIN NAME..."
echo ""
alb_domain_name=$(aws cloudformation describe-stacks --stack-name city-tasks-"$AWS_WORKLOADS_ENV" \
  --query "Stacks[0].Outputs[?OutputKey=='PublicLoadBalancerDNSName'].OutputValue" \
  --output text \
  --profile "$AWS_WORKLOADS_PROFILE")
echo "Domain Name: $alb_domain_name"

### ASKING TO REGISTER ALB DOMAIN NAME ON ROUTE53
echo ""
read -r -p "Do you want to <register> the ALB domain name on Route53? [Y/n] " register_alb_domain_name
if [ -z "$register_alb_domain_name" ]; then
  read -r -p "Do you want to <Update> an existing Record Set? [Y/n] " update_record_set
  if [ -z "$update_record_set" ]; then
    export UPDATE_RECORD_SET="true"
  else
    export UPDATE_RECORD_SET="false"
  fi
  export SERVER_DOMAIN_NAME="$server_domain_name"
  export ALB_DOMAIN_NAME="$alb_domain_name"
  sh "$WORKING_DIR"/utils/scripts/helper/6_7_register-alb-domain-in-route53.sh
else
  echo ""
  echo "No problem at all. You can register the ALB domain name on Route53 later."
  echo ""
  echo "DONE!"
fi
