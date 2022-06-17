package com.github.andylke.demo.randomuser;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties({DownloadImportRandomUserManagerProperties.class})
@Import({DownloadImportRandomUserManagerStepConfig.class})
public class DownloadImportRandomUserJobConfig {

  public static final String JOB_NAME = "downloadImportRandomUser";

  @Autowired private JobBuilderFactory jobBuilderFactory;

  @Autowired private Step downloadImportRandomUserManagerStep;

  @Bean(JOB_NAME)
  public Job downloadImportRandomUserJob() {
    return jobBuilderFactory
        .get(JOB_NAME)
        .incrementer(new RunIdIncrementer())
        .start(downloadImportRandomUserManagerStep)
        .build();
  }
}
