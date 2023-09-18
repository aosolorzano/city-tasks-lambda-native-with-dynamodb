#!/bin/bash

cd "$WORKING_DIR" || {
  echo "Error moving to the application's root directory."
  exit 1
}

### VERIFY CA AND CSR CERTIFICATES FILES
CA_CERTS_DIR="$WORKING_DIR"/utils/certs
TASKS_CERTS_DIR="$WORKING_DIR"/utils/certs/"$AWS_WORKLOADS_ENV"
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

### ASKING TO PRUNE DOCKER SYSTEM
read -r -p "Do you want to prune your docker system? [y/N] " response
case $response in
  [yY])
    sh "$WORKING_DIR"/utils/scripts/helper/2_docker-system-prune.sh
    ;;
esac

### UPDATE ENVOY CONFIGURATION FILE WITH SERVER FQDN
sed -i'.bak' -e "s/server_fqdn/$server_fqdn/g;"   \
      "$WORKING_DIR"/src/city-tasks-api-proxy/envoy.yaml
rm -f "$WORKING_DIR"/src/city-tasks-api-proxy/envoy.yaml.bak

echo ""
echo "GETTING INFORMATION FROM AWS. PLEASE WAIT..."

### GETTING COGNITO USER POOL ID
cognito_user_pool_id=$(aws cognito-idp list-user-pools --max-results 1 --output text  \
  --query "UserPools[?contains(Name, 'CityUserPool')].[Id]"                           \
  --profile "$AWS_IDP_PROFILE")
if [ -z "$cognito_user_pool_id" ]; then
  echo ""
  echo "Error: Not Cognito User Pool ID was found with name: 'CityUserPool'."
  exit 0
fi

### UPDATING DOCKER COMPOSE ENVIRONMENT FILE
idp_aws_region=$(aws configure get region --profile "$AWS_IDP_PROFILE")
sed -i'.bak' -e "s/idp_aws_region/$idp_aws_region/g; s/cognito_user_pool_id/$cognito_user_pool_id/g"  \
      "$WORKING_DIR"/utils/docker/env/tasks-api-dev.env
rm -f "$WORKING_DIR"/utils/docker/env/tasks-api-dev.env.bak

### STARTING DOCKER CLUSTER
echo ""
echo "STARING DOCKER CLUSTER..."
echo ""
docker compose up --build
echo ""
echo "DONE!"
