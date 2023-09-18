#!/bin/bash

echo ""
echo "CREATING LAMBDA ROLE..."
awslocal iam create-role                                          \
  --role-name 'lambda-role'                                       \
  --assume-role-policy-document '{"Version": "2012-10-17","Statement": [{ "Effect": "Allow", "Principal": {"Service": "lambda.amazonaws.com"}, "Action": "sts:AssumeRole"}]}'

echo ""
echo "ATTACHING BASIC POLICY TO LAMBDA ROLE..."
awslocal iam attach-role-policy                                   \
  --role-name 'lambda-role'                                       \
  --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole

echo ""
echo "ATTACHING CWL POLICY TO LAMBDA ROLE..."
awslocal iam attach-role-policy                                   \
  --role-name 'lambda-role'                                       \
  --policy-arn arn:aws:iam::aws:policy/CloudWatchFullAccess

echo ""
echo "ADDING DYNAMODB ACCESS TO LAMBDA ROLE..."
awslocal iam put-role-policy                                      \
    --role-name 'lambda-role'                                     \
    --policy-name DynamoDBWriteAccess                             \
    --policy-document '{"Version": "2012-10-17", "Statement": [{"Effect": "Allow", "Action": "dynamodb:PutItem", "Resource": "arn:aws:dynamodb:us-east-1:000000000000:table/Events"}]}'

echo ""
echo "CREATING LAMBDA FUNCTION..."
awslocal lambda create-function                                                             \
  --function-name 'city-tasks-events-function'                                              \
  --runtime 'provided.al2'                                                                  \
  --architectures 'arm64'                                                                   \
  --zip-file fileb:///workspace/apps/events-native-assembly.zip                             \
  --handler 'org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest' \
  --timeout 20                                                                              \
  --memory-size 512                                                                         \
  --role 'arn:aws:iam::000000000000:role/lambda-role'                                       \
  --environment 'Variables={AWS_ENDPOINT_OVERRIDE=http://host.docker.internal:4566}'

echo ""
echo "CREATING FUNCTION LOG-GROUP..."
awslocal logs create-log-group                                    \
  --log-group-name '/aws/lambda/city-tasks-events-function'

echo ""
echo "CREATING FUNCTION LOG-STREAM..."
awslocal logs create-log-stream                                   \
  --log-group-name '/aws/lambda/city-tasks-events-function'       \
  --log-stream-name 'city-tasks-events-function-log-stream'

echo ""
echo "GRANTING EVENTBRIDGE TO INVOKE LAMBDA FUNCTION..."
awslocal lambda add-permission                                    \
    --function-name 'city-tasks-events-function'                  \
    --statement-id events-lambda-permission                       \
    --action 'lambda:InvokeFunction'                              \
    --principal events.amazonaws.com                              \
    --source-arn 'arn:aws:events:us-east-1:000000000000:rule/city-tasks-events-function-rule'

echo ""
echo "DONE!"
