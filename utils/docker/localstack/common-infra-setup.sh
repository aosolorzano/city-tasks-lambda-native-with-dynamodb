#!/bin/bash

echo ""
echo "CREATING LAMBDA ROLE..."
awslocal iam create-role          \
  --role-name 'lambda-role'       \
  --assume-role-policy-document '{"Version": "2012-10-17","Statement": [{ "Effect": "Allow", "Principal": {"Service": "lambda.amazonaws.com"}, "Action": "sts:AssumeRole"}]}'

echo ""
echo "ATTACHING BASIC POLICY TO LAMBDA ROLE..."
awslocal iam attach-role-policy                                                   \
  --role-name 'lambda-role'                                                       \
  --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole

echo ""
echo "ATTACHING CWL POLICY TO LAMBDA ROLE..."
awslocal iam attach-role-policy                                                   \
  --role-name 'lambda-role'                                                       \
  --policy-arn arn:aws:iam::aws:policy/CloudWatchFullAccess

echo ""
echo "ADDING DYNAMODB ACCESS TO LAMBDA ROLE..."
awslocal iam put-role-policy                                                      \
    --role-name 'lambda-role'                                                     \
    --policy-name DynamoDBWriteAccess                                             \
    --policy-document '{"Version": "2012-10-17", "Statement": [{"Effect": "Allow", "Action": "dynamodb:PutItem", "Resource": "arn:aws:dynamodb:us-east-1:000000000000:table/Events"}]}'

echo ""
echo "CREATING LAMBDA FUNCTION..."
awslocal lambda create-function                                                   \
  --function-name 'city-tasks-events-function'                                    \
  --runtime 'java17'                                                              \
  --architectures 'arm64'                                                         \
  --role 'arn:aws:iam::000000000000:role/lambda-role'                             \
  --handler 'com.hiperium.city.tasks.events.ApplicationHandler::handleRequest'    \
  --zip-file fileb:///var/lib/localstack/city-tasks-events.jar

echo ""
echo "CREATING FUNCTION URL..."
awslocal lambda create-function-url-config                        \
  --function-name 'city-tasks-events-function'                    \
  --auth-type NONE

echo ""
echo "CREATING FUNCTION LOG-GROUP..."
awslocal logs create-log-group                                    \
  --log-group-name '/aws/lambda/city-tasks-events-function'

echo ""
echo "CREATING FUNCTION LOG-STREAM..."
awslocal logs create-log-stream                                   \
  --log-group-name '/aws/lambda/city-tasks-events-function'       \
  --log-stream-name 'city-tasks-events-log-stream'

echo ""
echo "CREATING EVENTBRIDGE RULE..."
awslocal events put-rule                                          \
  --name 'city-tasks-events-rule'                                 \
  --event-pattern "{\"source\":[\"com.hiperium.city.tasks\"],\"detail-type\":[\"TaskExecution\"]}"

echo ""
echo "GRANTING EVENTBRIDGE TO INVOKE LAMBDA FUNCTION..."
awslocal lambda add-permission                                    \
    --function-name 'city-tasks-events-function'                  \
    --statement-id events-lambda-permission                       \
    --action 'lambda:InvokeFunction'                              \
    --principal events.amazonaws.com                              \
    --source-arn 'arn:aws:events:us-east-1:000000000000:rule/city-tasks-events-rule'

echo ""
echo "GETTING LAMBDA FUNCTION ARN..."
lambda_arn=$(awslocal lambda get-function                         \
  --function-name 'city-tasks-events-function'                    \
  --query 'Configuration.FunctionArn'                             \
  --output text)
echo "ARN: $lambda_arn"

echo ""
echo "ADDING LAMBDA FUNCTION AS EVENTBRIDGE TARGET..."
awslocal events put-targets                                       \
  --rule 'city-tasks-events-rule'                                 \
  --targets Id=1,Arn="$lambda_arn"

echo ""
echo "DONE!"
