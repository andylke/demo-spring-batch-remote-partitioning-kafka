package com.github.andylke.demo;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableBatchProcessing
@EnableBatchIntegration
@EnableScheduling
public class DemoSpringBatchRemotePartitioningKafka {

  public static void main(String[] args) {
    SpringApplication.run(DemoSpringBatchRemotePartitioningKafka.class, args);
  }
}
