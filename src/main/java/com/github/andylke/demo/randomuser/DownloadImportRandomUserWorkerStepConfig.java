package com.github.andylke.demo.randomuser;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.integration.partition.RemotePartitioningWorkerStepBuilderFactory;
import org.springframework.batch.integration.partition.StepExecutionRequest;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.core.ConsumerFactory;

import com.github.andylke.demo.user.User;
import com.github.andylke.demo.user.UserRepository;

@Configuration
@EnableConfigurationProperties({DownloadImportRandomUserProperties.class})
public class DownloadImportRandomUserWorkerStepConfig {

  @Autowired private RemotePartitioningWorkerStepBuilderFactory stepBuilderFactory;

  @Autowired private DownloadImportRandomUserProperties properties;

  @Autowired private UserRepository userRepository;

  @Autowired private FormattingConversionService conversionService;

  @Bean
  public Step downloadImportRandomUserWorkerStep() {
    return stepBuilderFactory
        .get("downloadImportRandomUserWorker")
        .inputChannel(downloadImportRandomUserWorkerRequestsChannel())
        .<RandomUser, User>chunk(properties.getChunkSize())
        .reader(randomUserRestServiceReader(null, null))
        .processor(randomUserToUserProcessor())
        .writer(userRepositoryWriter())
        .build();
  }

  @Bean
  @StepScope
  public RandomUserRestServiceReader randomUserRestServiceReader(
      @Value("#{stepExecutionContext['startPagePerPartition']}") Integer startPagePerPartition,
      @Value("#{stepExecutionContext['totalPagePerPartition']}") Integer totalPagePerPartition) {
    return new RandomUserRestServiceReader(
        startPagePerPartition, totalPagePerPartition, properties.getPageSize());
  }

  @Bean
  public RandomUserToUserProcessor randomUserToUserProcessor() {
    return new RandomUserToUserProcessor();
  }

  @Bean
  public RepositoryItemWriter<? super User> userRepositoryWriter() {
    return new RepositoryItemWriterBuilder<User>().repository(userRepository).build();
  }

  @Bean
  public QueueChannel downloadImportRandomUserWorkerRequestsChannel() {
    return new QueueChannel();
  }

  @Bean
  public IntegrationFlow downloadImportRandomUserWorkerRequestsFlow(
      ConsumerFactory<String, String> consumerFactory) {
    return IntegrationFlows.from(
            Kafka.messageDrivenChannelAdapter(
                consumerFactory,
                DownloadImportRandomUserManagerStepConfig
                    .DOWNLOAD_IMPORT_RANDOM_USER_REQUESTS_TOPIC))
        .transform(source -> conversionService.convert(source, StepExecutionRequest.class))
        .channel(downloadImportRandomUserWorkerRequestsChannel())
        .get();
  }
}
