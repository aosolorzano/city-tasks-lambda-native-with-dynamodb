#!/bin/bash

cd "$WORKING_DIR" || {
  echo "Error moving to the application's root directory."
  exit 1
}
TEMPLATE_DIR="$WORKING_DIR"/utils/templates

cat "$TEMPLATE_DIR"/copilot/api/manifest.yml                      > "$WORKING_DIR"/copilot/api/manifest.yml
cat "$TEMPLATE_DIR"/copilot/env/"$AWS_WORKLOADS_ENV"/manifest.yml > "$WORKING_DIR"/copilot/environments/"$AWS_WORKLOADS_ENV"/manifest.yml
cat "$TEMPLATE_DIR"/iam/s3-alb-access-logs-policy.json            > "$WORKING_DIR"/utils/aws/iam/s3-alb-access-logs-policy.json
cat "$TEMPLATE_DIR"/iam/ecs-task-eventbridge-put-policy.json      > "$WORKING_DIR"/utils/aws/iam/ecs-task-eventbridge-put-policy.json
cat "$TEMPLATE_DIR"/route53/tasks-api-save-alb-record-set.json    > "$WORKING_DIR"/utils/aws/route53/tasks-api-save-alb-record-set.json
cat "$TEMPLATE_DIR"/route53/tasks-api-delete-alb-record-set.json  > "$WORKING_DIR"/utils/aws/route53/tasks-api-delete-alb-record-set.json
cat "$TEMPLATE_DIR"/docker/env/tasks-api-dev.env                  > "$WORKING_DIR"/utils/docker/env/tasks-api-dev.env
cat "$TEMPLATE_DIR"/docker/envoy/envoy-https-http.yaml            > "$WORKING_DIR"/src/city-tasks-proxy/envoy.yaml

echo "subjectAltName = DNS:api.example.io" > "$WORKING_DIR"/utils/certs/v3.ext
