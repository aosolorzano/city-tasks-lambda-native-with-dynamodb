#!/bin/bash

echo ""
echo "GETTING AWS-LOCAL CLI VERSION..."
awslocal --version

echo ""
echo "CREATING DEVICES TABLE..."
awslocal dynamodb create-table                                \
  --table-name Devices                                        \
  --attribute-definitions AttributeName=id,AttributeType=S    \
  --key-schema AttributeName=id,KeyType=HASH                  \
  --billing-mode PAY_PER_REQUEST

echo ""
echo "WRITING DEVICE ITEMS..."
awslocal dynamodb batch-write-item                            \
    --request-items file:///var/lib/localstack/devices.json

echo ""
echo "CREATING LAMBDA ROLE..."
awslocal iam create-role    \
  --role-name lambda-role   \
  --assume-role-policy-document '{"Version": "2012-10-17","Statement": [{ "Effect": "Allow", "Principal": {"Service": "lambda.amazonaws.com"}, "Action": "sts:AssumeRole"}]}'

echo ""
echo "ATTACHING BASIC POLICY TO LAMBDA ROLE..."
awslocal iam attach-role-policy   \
  --role-name lambda-role         \
  --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole

echo ""
echo "ATTACHING CWL POLICY TO LAMBDA ROLE..."
awslocal iam attach-role-policy \
  --role-name lambda-role       \
  --policy-arn arn:aws:iam::aws:policy/CloudWatchFullAccess

echo ""
echo "CREATING LAMBDA FUNCTION..."
awslocal lambda create-function                         \
  --function-name 'city-tasks-events'                   \
  --runtime 'java17'                                    \
  --architectures 'arm64'                               \
  --role 'arn:aws:iam::000000000000:role/lambda-role'   \
  --handler 'com.hiperium.city.tasks.events.ApplicationHandler::handleRequest' \
  --zip-file fileb:///var/lib/localstack/city-tasks-events.jar

echo ""
echo "CREATING FUNCTION URL..."
awslocal lambda create-function-url-config  \
  --function-name city-tasks-events         \
  --auth-type NONE

echo ""
echo "CREATING FUNCTION LOG-GROUP..."
awslocal logs create-log-group \
  --log-group-name /aws/lambda/city-tasks-events

echo ""
echo "CREATING FUNCTION LOG-STREAM..."
awslocal logs create-log-stream                     \
  --log-group-name /aws/lambda/city-tasks-events    \
  --log-stream-name city-tasks-events-stream

echo ""
echo "CREATING EVENTBRIDGE RULE..."
awslocal events put-rule        \
  --name city-tasks-event-rule  \
  --event-pattern "{\"source\":[\"com.hiperium.city.tasks\"],\"detail-type\":[\"TaskExecution\"]}"

echo ""
echo "CREATING EVENTBRIDGE TARGET..."
lambda_arn=$(awslocal lambda get-function   \
  --function-name city-tasks-events         \
  --query 'Configuration.FunctionArn'       \
  --output text)
awslocal events put-targets       \
  --rule city-tasks-event-rule    \
  --targets "Id"="1","Arn"="$lambda_arn"

echo ""
echo "DONE!"
