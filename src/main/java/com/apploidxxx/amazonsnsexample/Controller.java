package com.apploidxxx.amazonsnsexample;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @author Arthur Kupriyanov on 12.06.2020
 */
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

        request.setPlatformApplicationArn("arn:aws:sns:us-west-2:614856443116:app/GCM/LifeFeel");
        request.setToken(token);

        log.info("Sending request with token : " + token);

        CreatePlatformEndpointResult platformEndpoint = amazonSNS.createPlatformEndpoint(request);
        log.info("Result : " + platformEndpoint.toString());

        log.info("Creating a topic ...");
        CreateTopicResult topicResult = amazonSNS.createTopic("TEST_MESSAGE_TOPIC");

        SubscribeResult subscribeResult = amazonSNS.subscribe(topicResult.getTopicArn(), "application", platformEndpoint.getEndpointArn());
        log.info("Subscribe result : " + subscribeResult.toString());

        PublishResult publishResult = amazonSNS.publish(topicResult.getTopicArn(), "Hello, dude!"); // public message to topic
        log.info("Publish message result " + publishResult.toString());

    }
}
