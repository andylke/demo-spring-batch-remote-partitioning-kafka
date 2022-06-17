package com.github.andylke.demo.support;

import java.io.IOException;

import org.springframework.batch.integration.partition.StepExecutionRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.DefaultDeserializer;
import org.springframework.core.serializer.DefaultSerializer;
import org.springframework.core.serializer.support.SerializationFailedException;

@Configuration(proxyBeanMethods = false)
class RemotePartitionConfiguration {

  private final DefaultSerializer serializer = new DefaultSerializer();

  private final DefaultDeserializer deserializer = new DefaultDeserializer();

  @Bean
  public Converter<StepExecutionRequest, byte[]> stepExecutionRequestToByteArrayConverter() {
    return new Converter<StepExecutionRequest, byte[]>() {

      @Override
      public byte[] convert(StepExecutionRequest source) {
        try {
          return serializer.serializeToByteArray(source);
        } catch (IOException e) {
          throw new SerializationFailedException("", e);
        }
      }
    };
  }

  @Bean
  public Converter<byte[], StepExecutionRequest> byteArrayToStepExecutionRequestConverter() {
    return new Converter<byte[], StepExecutionRequest>() {

      @Override
      public StepExecutionRequest convert(byte[] source) {
        try {
          return (StepExecutionRequest) deserializer.deserializeFromByteArray(source);
        } catch (IOException e) {
          throw new DeserializationFailedException("", e);
        }
      }
    };
  }
}
