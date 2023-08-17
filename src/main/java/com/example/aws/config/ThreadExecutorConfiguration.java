package com.example.aws.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ThreadExecutorConfiguration {

  /**
   *
   * @return
   */
  @Bean(name = "consumerThreadPoolTaskExecutor")
  public ThreadPoolTaskExecutor consumerThreadPoolTaskExecutor() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setThreadNamePrefix("sqs-consumer-task-");
    taskExecutor.setCorePoolSize(10);
    taskExecutor.setMaxPoolSize(10);
    taskExecutor.setQueueCapacity(5);
    taskExecutor.afterPropertiesSet();
    return taskExecutor;
  }

}
