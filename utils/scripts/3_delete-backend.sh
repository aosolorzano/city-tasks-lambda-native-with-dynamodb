#!/bin/bash

cd "$WORKING_DIR" || {
  echo "Error moving to the Tasks Service directory."
  exit 1
}

### REMOVING EVENTBRIDGE RULE POLICY TO AVOID ERRORS WHEN DELETING THE COPILOT CLI STACK
echo ""
echo "REMOVING EVENTBRIDGE POLICY FROM ECS TASK ROLE..."
ecs_task_role_name=$(aws iam list-roles --output text \
  --query "Roles[?contains(RoleName, 'city-tasks-$AWS_WORKLOADS_ENV-api-TaskRole')].[RoleName]" \
  --profile "$AWS_WORKLOADS_PROFILE")
if [ -z "$ecs_task_role_name" ]; then
  echo ""
  echo "WARNING: ECS Task Role was not found with name: 'city-tasks-$AWS_WORKLOADS_ENV-api'."
  echo "         Please, remove the following IAM-Policy from the ECS Task Role manually:"
  echo ""
  echo "         city-tasks-$AWS_WORKLOADS_ENV-api-EventBridge-PutPolicy"
  echo ""
else
  aws iam delete-role-policy              \
      --role-name "$ecs_task_role_name"   \
      --policy-name "city-tasks-$AWS_WORKLOADS_ENV-api-EventBridge-PutPolicy" \
      --profile "$AWS_WORKLOADS_PROFILE"
  echo "DONE!"
fi

echo ""
echo "DELETING COPILOT APPLICATION FROM AWS..."
copilot app delete --yes
echo ""
echo "DONE!"

echo ""
echo "DELETING PENDING LOG-GROUPS FROM CLOUDWATCH..."
echo ""
aws logs describe-log-groups --output text \
  --query "logGroups[?contains(logGroupName, 'city-tasks-$AWS_WORKLOADS_ENV-api')].[logGroupName]" \
  --profile "$AWS_WORKLOADS_PROFILE" | while read -r log_group_name; do
  echo "Deleting log-group: $log_group_name"
  aws logs delete-log-group             \
    --log-group-name "$log_group_name"  \
    --profile "$AWS_WORKLOADS_PROFILE"
done
aws logs delete-log-group                                       \
    --log-group-name "city-tasks-$AWS_WORKLOADS_ENV-FlowLogs"   \
    --profile "$AWS_WORKLOADS_PROFILE"
echo ""
echo "DONE!"

echo ""
echo "DELETING SAM APPLICATION FROM AWS..."
sam delete                                                      \
  --stack-name city-tasks-events-function-"$AWS_WORKLOADS_ENV"  \
  --config-env "$AWS_WORKLOADS_ENV"                             \
  --no-prompts                                                  \
  --profile "$AWS_WORKLOADS_PROFILE"
rm -rf "$WORKING_DIR"/.aws-sam

### REVERTING CONFIGURATION FILES
sh "$WORKING_DIR"/utils/scripts/helper/1_revert-automated-scripts.sh
echo ""
echo "DONE!"
