package com.github.andylke.demo.randomuser;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.integration.partition.RemotePartitioningManagerStepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class DownloadImportRandomUserManagerStepConfig {

  public static final String DOWNLOAD_IMPORT_RANDOM_USER_REQUESTS_TOPIC =
      "download-import-random-user-requests";

  public static final String DOWNLOAD_IMPORT_RANDOM_USER_REPLIES_TOPIC =
      "download-import-random-user-replies";

  @Autowired private RemotePartitioningManagerStepBuilderFactory stepBuilderFactory;

  @Autowired private DownloadImportRandomUserManagerProperties properties;

  @Autowired private FormattingConversionService conversionService;

  @Autowired private JobExplorer jobExplorer;

  @Bean
  public Step downloadImportRandomUserManagerStep() {
    return stepBuilderFactory
        .get("downloadImportRandomUserManager")
        .partitioner("downloadImportRandomUserWorkerStep", downloadRandomUserPartitioner())
        .jobExplorer(jobExplorer)
        .gridSize(properties.getGridSize())
        .outputChannel(downloadImportRandomUserManagerRequestsChannel())
        .build();
  }

  @Bean
  public DownloadRandomUserPartitioner downloadRandomUserPartitioner() {
    return new DownloadRandomUserPartitioner(properties.getTotalPage());
  }

  @Bean
  public DirectChannel downloadImportRandomUserManagerRequestsChannel() {
    return new DirectChannel();
  }

  @Bean
  public IntegrationFlow downloadImportRandomUserManagerRequestsFlow(
      KafkaTemplate<String, String> kafkaTemplate) {
    return IntegrationFlows.from(downloadImportRandomUserManagerRequestsChannel())
        .transform(source -> conversionService.convert(source, byte[].class))
        .handle(
            Kafka.outboundChannelAdapter(kafkaTemplate)
                .topic(DOWNLOAD_IMPORT_RANDOM_USER_REQUESTS_TOPIC))
        .get();
  }

  @Bean
  public NewTopic downloadImportRandomUserRequestsTopic() {
    return TopicBuilder.name(DOWNLOAD_IMPORT_RANDOM_USER_REQUESTS_TOPIC)
        .partitions(properties.getPartitions())
        .build();
  }
}
