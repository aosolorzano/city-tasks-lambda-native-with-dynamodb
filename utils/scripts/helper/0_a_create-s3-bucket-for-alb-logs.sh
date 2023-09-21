#!/bin/bash
set -e

cd "$WORKING_DIR" || {
  echo "Error moving to the application's root directory."
  exit 1
}

read -r -p 'Do you want to Create a new <S3 Bucket> to store ALB logs? [y/N] ' create_s3_bucket
if [ "$create_s3_bucket" == "Y" ] || [ "$create_s3_bucket" == "y" ] || [ "$create_s3_bucket" == "yes" ]; then
  read -r -p 'Please, enter the <Bucket> name: ' s3_bucket_name
  if [ -z "$s3_bucket_name" ]; then
    echo ""
    echo "Error: S3 Bucket name is required."
    sh "$WORKING_DIR"/utils/scripts/helper/1_revert-automated-scripts.sh
    exit 1
  fi
  echo "Wait a moment, creating the S3 Bucket..."
  aws s3api create-bucket                   \
      --bucket "$s3_bucket_name"            \
      --profile "$AWS_WORKLOADS_PROFILE"

  ### UPDATING S3 BUCKET POLICY FILE
  workloads_account_id=$(aws configure get sso_account_id --profile "$AWS_WORKLOADS_PROFILE")
  sed -i'.bak' -e "s/aws_account_id/$workloads_account_id/g; s/bucket_name/$s3_bucket_name/g" \
        "$WORKING_DIR"/utils/aws/iam/s3-alb-access-logs-policy.json
  rm -f "$WORKING_DIR"/utils/aws/iam/s3-alb-access-logs-policy.json.bak

  ### ASSIGNING S3 BUCKET POLICY
  aws s3api put-bucket-policy               \
      --bucket "$s3_bucket_name"            \
      --policy file://"$WORKING_DIR"/utils/aws/iam/s3-alb-access-logs-policy.json \
      --profile "$AWS_WORKLOADS_PROFILE"

  ### REVERTING S3 BUCKET POLICY FILE
  cat "$WORKING_DIR"/utils/templates/iam/s3-alb-access-logs-policy.json > "$WORKING_DIR"/utils/aws/iam/s3-alb-access-logs-policy.json
  echo "DONE!"
else
  read -r -p 'Enter the existing <S3 Bucket> name: [city-tasks-alb-dev]' s3_bucket_name
  if [ -z "$s3_bucket_name" ]; then
    s3_bucket_name='city-tasks-alb-dev'
  fi
fi

### UPDATING ENVIRONMENT MANIFEST FILE
sed -i'.bak' -e "s/s3_bucket_name/$s3_bucket_name/g" \
      "$WORKING_DIR"/copilot/environments/"$AWS_WORKLOADS_ENV"/manifest.yml
rm -f "$WORKING_DIR"/copilot/environments/"$AWS_WORKLOADS_ENV"/manifest.yml.bak
