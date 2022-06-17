package com.github.andylke.demo.user;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GenerateUserSummaryJobConfig {

  @Autowired private JobBuilderFactory jobBuilderFactory;

  @Autowired private Step generateUserSummaryByNationalityStep;

  @Bean
  public Job generateUserSummaryJob() {
    return jobBuilderFactory
        .get("generateUserSummary")
        .incrementer(new RunIdIncrementer())
        .start(generateUserSummaryByNationalityStep)
        .build();
  }
}
