#!/bin/bash

echo ""
echo "CREATING EVENTBRIDGE RULE..."
awslocal events put-rule          \
  --name 'city-tasks-event-rule'  \
  --event-pattern "{\"source\":[\"com.hiperium.city.tasks\"],\"detail-type\":[\"TaskExecution\"]}"

echo ""
echo "CREATING LAMBDA ROLE..."
awslocal iam create-role          \
  --role-name 'lambda-role'       \
  --assume-role-policy-document '{"Version": "2012-10-17","Statement": [{ "Effect": "Allow", "Principal": {"Service": "lambda.amazonaws.com"}, "Action": "sts:AssumeRole"}]}'

echo ""
echo "ATTACHING BASIC POLICY TO LAMBDA ROLE..."
awslocal iam attach-role-policy   \
  --role-name 'lambda-role'       \
  --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole

echo ""
echo "ATTACHING CWL POLICY TO LAMBDA ROLE..."
awslocal iam attach-role-policy   \
  --role-name 'lambda-role'       \
  --policy-arn arn:aws:iam::aws:policy/CloudWatchFullAccess

echo ""
echo "CREATING LAMBDA FUNCTION..."
awslocal lambda create-function                                                 \
  --function-name 'CityTasksEvents'                                             \
  --runtime 'java17'                                                            \
  --architectures 'arm64'                                                       \
  --role 'arn:aws:iam::000000000000:role/lambda-role'                           \
  --handler 'com.hiperium.city.tasks.events.ApplicationHandler::handleRequest'  \
  --zip-file fileb:///var/lib/localstack/events.jar

echo ""
echo "CREATING FUNCTION URL..."
awslocal lambda create-function-url-config          \
  --function-name 'CityTasksEvents'                 \
  --auth-type NONE

echo ""
echo "CREATING FUNCTION LOG-GROUP..."
awslocal logs create-log-group                      \
  --log-group-name /aws/lambda/CityTasksEvents

echo ""
echo "CREATING FUNCTION LOG-STREAM..."
awslocal logs create-log-stream                     \
  --log-group-name /aws/lambda/CityTasksEvents      \
  --log-stream-name 'CityTasksEventsStream'

echo ""
echo "CREATING EVENTBRIDGE TARGET..."
lambda_arn=$(awslocal lambda get-function           \
  --function-name 'CityTasksEvents'                 \
  --query 'Configuration.FunctionArn'               \
  --output text)
awslocal events put-targets                         \
  --rule 'city-tasks-event-rule'                    \
  --targets "Id"="1","Arn"="$lambda_arn"

echo ""
echo "DONE!"
