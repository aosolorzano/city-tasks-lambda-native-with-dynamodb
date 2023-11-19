#!/usr/bin/env bash
set -e

cd "$WORKING_DIR" || {
  echo "Error moving to the Project's root directory."
  exit 1
}

### SETTING ENVIRONMENT VARIABLES
if [ -z "$UPDATE_RECORD_SET" ]; then
  read -r -p "Do you want to <Update> an existing <Record Set>: [Y/n] " update_record_set
  if [ "$update_record_set" ]; then
    UPDATE_RECORD_SET="true"
  else
    UPDATE_RECORD_SET="false"
  fi
fi

### GETTING SERVER DOMAIN NAME
if [ "$SERVER_DOMAIN_NAME" ]; then
  server_domain_name=$SERVER_DOMAIN_NAME
else
  read -r -p 'Enter the <Domain Name> used in your <CSR> certificate: ' server_domain_name
  if [ -z "$server_domain_name" ]; then
    echo ""
    echo "Error: Domain Name is required."
    exit 1
  fi
fi
server_fqdn="$AWS_WORKLOADS_ENV.$server_domain_name"

### GETTING ROUTE53 AWS PROFILE
read -r -p 'Enter the <AWS Profile> for the registration: [default] ' aws_route53_profile
if [ -z "$aws_route53_profile" ]; then
  AWS_ROUTE53_PROFILE='default'
else
  AWS_ROUTE53_PROFILE=$aws_route53_profile
fi

echo ""
echo "GETTING INFORMATION FROM AWS. PLEASE WAIT..."

### GETTING ALB DOMAIN NAME
if [ "$ALB_DOMAIN_NAME" ]; then
  alb_domain_name=$ALB_DOMAIN_NAME
else
  alb_domain_name=$(aws cloudformation describe-stacks --stack-name city-tasks-"$AWS_WORKLOADS_ENV" \
    --query "Stacks[0].Outputs[?OutputKey=='PublicLoadBalancerDNSName'].OutputValue" \
    --output text \
    --profile "$AWS_WORKLOADS_PROFILE")
  if [ -z "$alb_domain_name" ]; then
    echo ""
    echo "Error: ALB Domain Name not found."
    exit 1
  fi
fi

### GETTING HOSTED ZONE-ID BY SERVER DOMAIN NAME
hosted_zone_id=$(aws route53 list-hosted-zones-by-name \
  --dns-name "$server_domain_name" \
  --profile "$AWS_ROUTE53_PROFILE" \
  --output text \
  --query "HostedZones[?contains(Name, '$server_domain_name')].[Id]")
if [ -z "$hosted_zone_id" ]; then
  echo ""
  echo "Error: Hosted Zone ID not found."
  exit 1
fi

### SET RECORD SET ACTION
if [ "$UPDATE_RECORD_SET" = "true" ]; then
  record_set_action="UPSERT"
else
  record_set_action="CREATE"
fi

### UPDATING RECORD-SET FILE
sed -i'.bak' -e "s/record-set-action/$record_set_action/g; s/server-name-fqdn/$server_fqdn/g; s/alb-domain-name/$alb_domain_name/g" \
      "$WORKING_DIR"/utils/aws/route53/tasks-api-save-alb-record-set.json
rm -f "$WORKING_DIR"/utils/aws/route53/tasks-api-save-alb-record-set.json.bak

### REGISTERING RECORD SET ON ROUTE53
echo ""
echo "REGISTERING RECORD SET ON ROUTE53. PLEASE WAIT..."
hosted_zone_id=$(echo "$hosted_zone_id" | cut -d'/' -f3)
aws route53 change-resource-record-sets     \
  --hosted-zone-id "$hosted_zone_id"        \
  --change-batch file://"$WORKING_DIR"/utils/aws/route53/tasks-api-save-alb-record-set.json \
  --profile "$AWS_ROUTE53_PROFILE"

### REVERTING RECORD-SET FILE
cat "$WORKING_DIR"/utils/templates/route53/tasks-api-save-alb-record-set.json > "$WORKING_DIR"/utils/aws/route53/tasks-api-save-alb-record-set.json
echo "DONE!"
