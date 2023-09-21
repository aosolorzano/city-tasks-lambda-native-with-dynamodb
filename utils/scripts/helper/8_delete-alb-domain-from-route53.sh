#!/usr/bin/env bash
set -e

### VERIFY AWS PROFILE NAME
if [ -z "$AWS_WORKLOADS_PROFILE" ]; then
  echo ""
  echo "You must specify the AWS profile before delete the resources."
  exit 1
fi

echo ""
read -r -p 'Enter the <AWS Profile> used to delete the <Record Set>: [default] ' aws_profile
if [ -z "$aws_workloads_profile" ]; then
  AWS_ROUTE53_PROFILE='default'
else
  AWS_ROUTE53_PROFILE=$aws_profile
fi

### READ CSR DOMAIN NAME AND CSR SERVER FQDN
read -r -p 'Enter the <Domain Name> used in your <CSR> certificate: ' server_domain_name
if [ -z "$server_domain_name" ]; then
  echo "Error: The <Domain Name> is required."
  exit 1
fi
server_fqdn="$AWS_WORKLOADS_ENV.$server_domain_name"

echo ""
echo "GETTING INFORMATION FROM AWS. PLEASE WAIT..."

### GET ALB DNS NAME FROM AWS
alb_domain_name=$(aws cloudformation describe-stacks --stack-name city-tasks-"$AWS_WORKLOADS_ENV" \
  --query "Stacks[0].Outputs[?OutputKey=='PublicLoadBalancerDNSName'].OutputValue" \
  --output text \
  --profile "$AWS_WORKLOADS_PROFILE")

echo ""
echo "DELETING RECORD SET FROM ROUTE53..."
hosted_zone_id=$(aws route53 list-hosted-zones-by-name \
  --dns-name "$server_domain_name" \
  --profile "$AWS_ROUTE53_PROFILE" \
  --output text \
  --query "HostedZones[?contains(Name, '$server_domain_name')].[Id]")
if [ -z "$hosted_zone_id" ]; then
  echo ""
  echo "WARNING: Not Hosted Zone found on Route53. You must delete it manually."
else
  cat "$WORKING_DIR"/utils/templates/route53/tasks-api-delete-alb-record-set.json > "$WORKING_DIR"/utils/aws/route53/tasks-api-delete-alb-record-set.json
  sed -i'.bak' -e "s/server-name-fqdn/$server_fqdn/g; s/alb-domain-name/$alb_domain_name/g" \
        "$WORKING_DIR"/utils/aws/route53/tasks-api-delete-alb-record-set.json
  rm -f "$WORKING_DIR"/utils/aws/route53/tasks-api-delete-alb-record-set.json.bak
  hosted_zone_id=$(echo "$hosted_zone_id" | cut -d'/' -f3)
  aws route53 change-resource-record-sets \
    --hosted-zone-id "$hosted_zone_id" \
    --change-batch file://"$WORKING_DIR"/utils/aws/route53/tasks-api-delete-alb-record-set.json \
    --profile "$AWS_ROUTE53_PROFILE"
  echo "DONE!"
fi
