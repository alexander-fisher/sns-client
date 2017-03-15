# sns-client

[![Build Status](https://travis-ci.org/hmrc/sns-client.svg)](https://travis-ci.org/hmrc/sns-client) 
[![Download](https://api.bintray.com/packages/hmrc/releases/sns-client/images/download.svg) ](https://bintray.com/hmrc/releases/sns-client/_latestVersion)

## Requirements
The following services are exposed from the micro-service.

Please note it is mandatory to supply an Accept HTTP header to all below services with the value ```application/vnd.hmrc.1.0+json```. 


## API

Base URL ```/sns-client```

| *Task* | *Supported Methods* | *Type* | *Description* |
|--------|---------------------|--------|---------------|
| ```/endpoints```     | ```POST``` | ```Endpoints```    | Batch publication of push notification messages, returns 200  with list of creation statuses|
| ```/notifications``` | ```POST``` | ```Notification``` | Batch creation of _PlatformApplicationEndpoints_, returns 200 with list of delivery statuses|

### Endpoints
| *Key* | *Value* |  *Description* |
|--------|----|----|
| ```endpoints``` | Array[```Endpoint```] | Sequence of Endpoint Type |

os: String, registrationToken: String

### Endpoint
| *Key* | *Value* |  *Description* |
|--------|----|----|
| ```os``` | String | The Native OS of the device, `android`, `ios` or `windows` |
| ```registrationToken``` | String | Token provided to the device when they register with the platform push notification provider e.g. `Firebase` or `APNS` |


#### Example
##### Request

    {
        "endpoints": [
            {
                "os": "thisWontWork",
                "registrationToken": "token1"
            },
            {
                "os": "android",
                "registrationToken": "token2"
            }
        ]
    }
##### Response
The response is a Map[String, Option[String]], where the key is the registration token and the value the Endpoint Arn if one was successfully created
```token1``` will be interpreted as None providing you have the requisite JsonFormats (see source for implementation)

    {
      "deliveryStatuses": {
        "token1": null,
        "my-registration-token": "default-platform-arn/stubbed/default-platform-arn/my-registration-token"
      }
    }


### Notifications
| *Key* | *Value* |  *Description* |
|--------|----|----|
| ```notifications``` | Array[```Notification```] | Sequence of Notifications |

### Notification
| *Key* | *Value* |  *Description* |
|--------|----|----|
| ```endpointArn``` | String | The SNS ARN resource string identiying the device endpoint |
| ```message``` | String | Content to be delivered to the device |
| ```id``` | String | This ID is used to map the delivery status to the request |

#### Example
##### Request

    {
      "notifications": [
          {
              "endpointArn": "endpointArn", 
              "message": "Hello World", 
              "id": "12334"
          },
          {
              "endpointArn": "endpointArn", 
              "message": "Hello World", 
              "id": "43211"
          }
      ]
    }
##### Response

    {
        "12334": "Failure",
        "43211": "Success"
    }

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")
