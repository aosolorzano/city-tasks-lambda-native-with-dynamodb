## city-tasks-events

This project contains source code and supporting files for a serverless application that you can deploy with the SAM CLI. It includes the following files and folders.

- src/main                     - Code for the application's Lambda function.
- src/test                     - Unit tests for the application code. 
- src/test/resources/events    - Invocation events that you can use to invoke the function in Integration Tests.

This application reacts to EventBridge custom events, demonstrating the power of Event-Driven Development.

The application uses several AWS resources, including Lambda functions and an EventBridge Rule. These resources are defined in the `template.yaml` file in the parent project. You can update the template to add AWS resources through the same deployment process that updates your application code.

If you prefer to use an integrated development environment (IDE) to build and test your application, you can use the AWS Toolkit.  
The AWS Toolkit is an open source plug-in for popular IDEs that uses the SAM CLI to build and deploy serverless applications on AWS. The AWS Toolkit also adds a simplified step-through debugging experience for Lambda function code. See the following links to get started.

* [CLion](https://docs.aws.amazon.com/toolkit-for-jetbrains/latest/userguide/welcome.html)
* [GoLand](https://docs.aws.amazon.com/toolkit-for-jetbrains/latest/userguide/welcome.html)
* [IntelliJ](https://docs.aws.amazon.com/toolkit-for-jetbrains/latest/userguide/welcome.html)
* [WebStorm](https://docs.aws.amazon.com/toolkit-for-jetbrains/latest/userguide/welcome.html)
* [Rider](https://docs.aws.amazon.com/toolkit-for-jetbrains/latest/userguide/welcome.html)
* [PhpStorm](https://docs.aws.amazon.com/toolkit-for-jetbrains/latest/userguide/welcome.html)
* [PyCharm](https://docs.aws.amazon.com/toolkit-for-jetbrains/latest/userguide/welcome.html)
* [RubyMine](https://docs.aws.amazon.com/toolkit-for-jetbrains/latest/userguide/welcome.html)
* [DataGrip](https://docs.aws.amazon.com/toolkit-for-jetbrains/latest/userguide/welcome.html)
* [VS Code](https://docs.aws.amazon.com/toolkit-for-vscode/latest/userguide/welcome.html)
* [Visual Studio](https://docs.aws.amazon.com/toolkit-for-visual-studio/latest/user-guide/welcome.html)

### Deploy the sample application

The Serverless Application Model Command Line Interface (SAM CLI) is an extension of the AWS CLI that adds functionality for building and testing Lambda applications. It uses Docker to run your functions in an Amazon Linux environment that matches Lambda. It can also emulate your application's build environment.

To use the SAM CLI, you need the following tools.

* AWS CLI - [Install the AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html) and [configure it with your AWS credentials].
* SAM CLI - [Install the SAM CLI](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install.html)
* Java17  - [Install the Java 17](https://docs.aws.amazon.com/corretto/latest/corretto-17-ug/downloads-list.html)
* Maven   - [Install Maven](https://maven.apache.org/install.html)
* Docker  - [Install Docker community edition](https://hub.docker.com/search/?type=edition&offering=community)

The SAM CLI uses an Amazon S3 bucket to store your application's deployment artifacts. If you don't have a bucket suitable for this purpose, create one. Replace `BUCKET_NAME` in the commands in this section with a unique bucket name.
```bash
aws s3 mb s3://BUCKET_NAME
```

To prepare the application for deployment, use the `sam package` command.
```bash
sam package                               \
    --output-template-file packaged.yaml  \
    --s3-bucket BUCKET_NAME
```

The SAM CLI creates deployment packages, uploads them to the S3 bucket, and creates a new version of the template that refers to the artifacts in the bucket. 

To deploy the application, use the `sam deploy` command.
```bash
sam deploy                              \
    --template-file packaged.yaml       \
    --stack-name city-tasks-events-dev  \
    --capabilities CAPABILITY_IAM
```

### Use the SAM CLI to build and test locally
Build your application with the `sam build` command.
```bash
sam build
```

The SAM CLI installs dependencies defined in `CityTasksEventsFunction/pom.xml`, creates a deployment package, and saves it in the `.aws-sam/build` folder.

Test a single function by invoking it directly with a test eventBridgeEvent. An eventBridgeEvent is a JSON document that represents the input that the function receives from the eventBridgeEvent source. Test events are included in the `events` folder in this project.

Run functions locally and invoke them with the `sam local invoke` command.
```bash
sam local invoke CityTasksEventsFunction \
    --eventBridgeEvent src/test/resources/events/events/valid-event.json
```

The SAM CLI reads the application template to determine the EventBridge rule pattern and the functions that they invoke as a target. The `Events` property on each function's definition includes the source and detail-type of the types of events that will invoke the function.
```yaml
      Events:
        TaskExecutionTrigger:
          Type: EventBridgeRule
          Properties:
            Pattern:
              source:
                - com.hiperium.city.tasks
              detail-type:
                - TaskExecution
```

### Add a resource to your application
The application template uses AWS Serverless Application Model (AWS SAM) to define application resources. AWS SAM is an extension of AWS CloudFormation with a simpler syntax for configuring common serverless application resources such as functions, triggers, and APIs. For resources not included in [the SAM specification](https://github.com/awslabs/serverless-application-model/blob/master/versions/2016-10-31.md), you can use standard [AWS CloudFormation](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-template-resource-type-ref.html) resource types.

### Fetch, tail, and filter Lambda function logs

To simplify troubleshooting, SAM CLI has a command called `sam logs`. `sam logs` lets you fetch logs generated by your deployed Lambda function from the command line. In addition to printing the logs on the terminal, this command has several nifty features to help you quickly find the bug.

`NOTE`: This command works for all AWS Lambda functions; not just the ones you deploy using SAM.

```bash
sam logs -n CityTasksEventsFunction \
    --stack-name city-tasks-events-dev --tail
```

You can find more information and examples about filtering Lambda function logs in the [SAM CLI Documentation](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-logging.html).

### Unit tests
Tests are defined in the `city-tasks-events/src/test` folder in this project.
```bash
mvn test
```

### Cleanup
To delete the sample application and the bucket that you created, use the SAM CLI and AWS CLI.
```bash
sam delete --stack-name city-tasks-events-dev
aws s3 rb s3://BUCKET_NAME
```

### Resources
See the [AWS SAM developer guide](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/what-is-sam.html) for an introduction to SAM specification, the SAM CLI, and serverless application concepts.

Next, you can use AWS Serverless Application Repository to deploy ready to use Apps that go beyond hello world samples and learn how authors developed their applications: [AWS Serverless Application Repository main page](https://aws.amazon.com/serverless/serverlessrepo/)
