package com.github.andylke.demo.user;

import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class GenerateUserSummaryStepConfig {

  public static final String SUMMARY_FILE_PATH = "target/user-summary-by-nationality.csv";

  private static final String[] SUMMARY_FIELD_NAMES = new String[] {"nationality", "count"};

  @Autowired private StepBuilderFactory stepBuilderFactory;

  @Bean
  public Step generateUserSummaryByNationalityStep() {
    return stepBuilderFactory
        .get("generateUserSummaryByNationality")
        .<UserSummaryByNationality, UserSummaryByNationality>chunk(20)
        .reader(userSummaryByNationalityReader())
        .writer(userSummaryByNationalityFileWriter())
        .build();
  }

  @Bean
  @StepScope
  public UserSummaryByNationalityReader userSummaryByNationalityReader() {
    return new UserSummaryByNationalityReader();
  }

  @Bean
  @StepScope
  public FlatFileItemWriter<? super UserSummaryByNationality> userSummaryByNationalityFileWriter() {
    return new FlatFileItemWriterBuilder<UserSummaryByNationality>()
        .name("userSummaryByNationalityFileWriter")
        .resource(new FileSystemResource(SUMMARY_FILE_PATH))
        .encoding("UTF-8")
        .shouldDeleteIfExists(true)
        .delimited()
        .names(SUMMARY_FIELD_NAMES)
        .headerCallback(callback -> callback.write(StringUtils.join(SUMMARY_FIELD_NAMES, ",")))
        .build();
  }
}
