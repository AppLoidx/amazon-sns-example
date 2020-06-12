# Amazon SNS - Spring Boot usage example

### 1. Adding dependency
```xml
        <!-- https://mvnrepository.com/artifact/com.amazonaws/aws-java-sdk -->
        <dependency>
            <groupId>com.amazonaws</groupId>
            <artifactId>aws-java-sdk</artifactId>
            <version>1.11.747</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-aws-autoconfigure -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-aws-autoconfigure</artifactId>
            <version>2.2.1.RELEASE</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-aws-messaging -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-aws-messaging</artifactId>
            <version>1.0.0.RELEASE</version>
        </dependency>
```

### 2. Credentials in properties

```properties
cloud.aws.credentials.accessKey=aKey
cloud.aws.credentials.secretKey=sKey
cloud.aws.region.static=us-west-2
cloud.aws.stack.auto=false
```

### 3. Create controller

```java
@Slf4j
@RestController
@RequestMapping("/api/create/endpoint/{token}")
public class Controller {

    private final AmazonSNS amazonSNS;

    public Controller(AmazonSNS amazonSNS) {
        this.amazonSNS = amazonSNS;
    }

    @PostMapping
    public void createEndpoint(@PathVariable("token") String token) {

        CreatePlatformEndpointRequest request = new CreatePlatformEndpointRequest();

        request.setPlatformApplicationArn("arn:aws:sns:us-west-2:3148964431616:app/GCM/LifeFeel");
        request.setToken(token);

        log.info("Sending request with token : " + token);

        CreatePlatformEndpointResult platformEndpoint = amazonSNS.createPlatformEndpoint(request);
        log.info("Result : " + platformEndpoint.toString());
    }
}
```

You can send test request to see how it works:
```
curl -X POST http://localhost:8080/api/create/endpoint/myToken
```

Then, you can see:
```text
Sending request with token : myToken
Result : {EndpointArn: arn:aws:sns:us-west-2:614856443116:endpoint/GCM/LifeFeel/3083a565-f1dd-3ee6-8744-9033e6b29442}
```

![](https://i.imgur.com/pS6xJcs.png)

**NOTE**

If you send, already existing token - you would get an existing endpoint arn. You can try it with repeating a request


You can see some exception (WARN) when aws tries to retrieve user metadata from endpoint:

```text

2020-06-12 10:02:25.941  WARN 17544 --- [           main] com.amazonaws.util.EC2MetadataUtils      : Unable to retrieve the requested metadata (/latest/meta-data/instance-id). Failed to connect to service endpoint: 

com.amazonaws.SdkClientException: Failed to connect to service endpoint: 
	at com.amazonaws.internal.EC2ResourceFetcher.doReadResource(EC2ResourceFetcher.java:100) ~[aws-java-sdk-core-1.11.747.jar:na]
	at com.amazonaws.internal.InstanceMetadataServiceResourceFetcher.getToken(InstanceMetadataServiceResourceFetcher.java:91) ~[aws-java-sdk-core-1.11.747.jar:na]
	...
	at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:397) ~[spring-boot-2.2.2.RELEASE.jar:2.2.2.RELEASE]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:315) ~[spring-boot-2.2.2.RELEASE.jar:2.2.2.RELEASE]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1226) ~[spring-boot-2.2.2.RELEASE.jar:2.2.2.RELEASE]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1215) ~[spring-boot-2.2.2.RELEASE.jar:2.2.2.RELEASE]
	at com.apploidxxx.amazonsnsexample.AmazonSnsExampleApplication.main(AmazonSnsExampleApplication.java:10) ~[classes/:na]
Caused by: java.net.SocketException: Network is unreachable: connect
	at java.base/java.net.PlainSocketImpl.waitForConnect(Native Method) ~[na:na]
	... 29 common frames omitted
```

If you  don't want to see this stacktrace from config request - you can set log level error for util class:
```properties
logging.level.com.amazonaws.util.EC2MetadataUtils=error
```

See issue in spring-cloud-aws: [github.com/spring-cloud/spring-cloud-aws/issues](https://github.com/spring-cloud/spring-cloud-aws/issues/556#issuecomment-636159990)

And one more here:
```properties
logging.level.com.amazonaws.internal.InstanceMetadataServiceResourceFetcher=error
```

### 4. Creating topic and subscribe endpoints

```java
log.info("Creating a topic ...");
CreateTopicResult topicResult = amazonSNS.createTopic("TEST_MESSAGE_TOPIC");

SubscribeResult subscribeResult = amazonSNS.subscribe(topicResult.getTopicArn(), "application", platformEndpoint.getEndpointArn());
log.info("Subscribe result : " + subscribeResult.toString());

PublishResult publishResult = amazonSNS.publish(topicResult.getTopicArn(), "Hello, dude!"); // public message to topic
log.info("Publish message result " + publishResult.toString());
```

So, you can have endpoints subscribed to topics

![](https://i.imgur.com/k6OpVSK.png)

