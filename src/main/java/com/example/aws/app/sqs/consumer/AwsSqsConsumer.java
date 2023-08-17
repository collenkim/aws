package com.example.aws.app.sqs.consumer;

import static com.example.aws.app.sqs.init.CreateMultiConsumer.OM;
import static com.example.aws.app.sqs.init.CreateMultiConsumer.isNotThreadTerminate;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import com.example.aws.app.sqs.dto.AwsSqsDTO;
import com.example.aws.exception.SendFailException;
import com.fasterxml.jackson.core.JsonProcessingException;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

/**
 * SQS 데이터를 주기적으로 컨슘해오는 컨슈머
 */
public class AwsSqsConsumer implements Runnable {

  private final SqsClient sqsClient;
  private final String awsSqsQueueUrl;

  public AwsSqsConsumer(SqsClient sqsClient, String awsSqsQueueUrl) {// 생성자
    this.sqsClient = sqsClient;
    this.awsSqsQueueUrl = awsSqsQueueUrl;
  }

  @Override
  public void run() {

    do {

      // WaitTime 설정 1초 : Consumer 2대 일 경우 MultiThread 값에 비례하여, 설정
      ReceiveMessageResponse receiveMessageResponse =
          sqsClient.receiveMessage(ReceiveMessageRequest.builder().maxNumberOfMessages(10)
              .queueUrl(awsSqsQueueUrl).waitTimeSeconds(1).visibilityTimeout(30).build());

      List<Message> messageList = receiveMessageResponse.messages();

      if (CollectionUtils.isNotEmpty(messageList)) {// SQS에 컨슘할 메시지가 존재할 경우

        for (Message message : messageList) {

          try {

            //@formatter:off
            AwsSqsDTO.ReqSqs resultObject = OM.readValue(message.body(), AwsSqsDTO.ReqSqs.class);// SQS 메시지 파싱하여 Object로 변환

            // Sqs Message 처리 로직

            sqsClient.deleteMessage(DeleteMessageRequest.builder().queueUrl(awsSqsQueueUrl)
                .receiptHandle(message.receiptHandle()).build());// SQS 메시지 삭제

            //@formatter:on

          } catch (JsonProcessingException ex) {
            String errorMsg = "Json 데이터 파싱 오류로 인한 메시지 삭제";
            System.out.println(errorMsg);
            sqsClient.deleteMessage(DeleteMessageRequest.builder().queueUrl(awsSqsQueueUrl)
                .receiptHandle(message.receiptHandle()).build());// SQS 메시지 삭제
          } catch (SendFailException e) {
            String errorMsg = "Consumer 처리 로직 실패로 인한 메시지 삭제";
            System.out.println(errorMsg);
            sqsClient.deleteMessage(DeleteMessageRequest.builder().queueUrl(awsSqsQueueUrl)
                .receiptHandle(message.receiptHandle()).build());// SQS 메시지 삭제
          }
        }
      }

    } while (isNotThreadTerminate);
  }

}
