package com.example.aws.app.sqs.init;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import com.example.aws.app.sqs.consumer.AwsSqsConsumer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import software.amazon.awssdk.services.sqs.SqsClient;

/**
 * 여러개의 컨슈머를 생성
 */
@Configuration
public class CreateMultiConsumer {

  @Resource(name = "sqsClientApNortheast2")
  private SqsClient sqsClientApNortheast2;

  @Resource(name = "consumerThreadPoolTaskExecutor")
  private ThreadPoolTaskExecutor consumerThreadPoolTaskExecutor;

  @Resource
  private GenericApplicationContext context;

  private String awsSqsQueueUrl = "";// sqsUrl

  public static boolean isNotThreadTerminate = true;// Thread가 종료 되었는지 (배포 혹은 Shutdown 이벤트 발생 시 false로 처리)
  
  public static final ObjectMapper OM = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  @PostConstruct
  public void init() {
    AwsSqsConsumer consumer01 = new AwsSqsConsumer(sqsClientApNortheast2, awsSqsQueueUrl);
    consumerThreadPoolTaskExecutor.submit(consumer01);
  }

}
