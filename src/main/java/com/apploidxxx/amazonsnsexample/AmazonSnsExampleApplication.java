package com.apploidxxx.amazonsnsexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.context.config.annotation.EnableContextInstanceData;

@EnableContextInstanceData
@SpringBootApplication
public class AmazonSnsExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(AmazonSnsExampleApplication.class, args);
    }

}
