# sns-client

[![Build Status](https://travis-ci.org/hmrc/sns-client.svg)](https://travis-ci.org/hmrc/sns-client) 
[ ![Download](https://api.bintray.com/packages/hmrc/releases/sns-client/images/download.svg) ](https://bintray.com/hmrc/releases/sns-client/_latestVersion)


## API
[See this page for the API definition](docs/API.md)


## Running the SNS Client against the HMRC SNS Stub

    sm --start DATASTREAM -f
    sm --start AWS_SNS_STUB -f
    sm --start SNS_CLIENT  --appendArgs '{
    "SNS_CLIENT": [
    "-Daws.regionOverrideForStubbing=http://localhost:8423/aws-sns-stub/", 
    "-Daws.stubbing=true"
    ]}' -f    

#### AWS SNS Stub

[https://github.com/hmrc/aws-sns-stub](https://github.com/hmrc/aws-sns-stub)


## Running the SNS Client locally against AWS

    sm --start DATASTREAM -f
    sm --start SNS_CLIENT  --appendArgs '{
        "SNS_CLIENT": [
        "-Daws.region=region", 
        "-Daws.accessKey=aws-access-key", 
        "-Daws.secret=aws-key", 
        "-Daws.platform.gcm.applicationArn=application-arn", 
        "-Daws.platform.gcm.osName=android", 
        "-Daws.platform.gcm.apiKey=firebase-server-key", 
        "-Daws.stubbing=false"]}' -f


| *Key* | *Location* |
|--------|-----------|
| ```aws.accessKey``` | found under your account > security credentials |
| ```aws.secret```    | only shown when the key is generated, if you don't know it - generate a new key |
| ```aws.platform.gcm.applicationArn``` | generated by SNS when a Firebase project is registered (see below for how to set this up) |
| ```aws.platform.gcm.apiKey``` | This is the `Server Key` or `Legacy Server Key` associated with your project not the `Web Api Key` |
| ```aws.region``` | e.g. eu-west-1 |
| ```aws.platform.gcm.osName``` | e.g. android |

#### Create a PlatformApplication (Application Arn).

1. Log into Firebase and create a new project (you'll need to supply a name and a region)
2. Under project settings, make a not of the `Project ID` and find `Server Key` (or `Legacy Server Key`) under the Cloud Messaging tab associated with your project
3. Head over to AWS SNS Console and Applications > Create New Platform Application and use the Project ID and Web Api Key  from step 2
4. Note the ApplicationARN generated e.g. `arn:aws:sns:eu-west-1:123456789012:app/GCM/Test`


    
### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")