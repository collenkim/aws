package com.example.aws.app.ses.service.impl;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import org.springframework.stereotype.Service;
import com.example.aws.app.ses.dto.AwsSesV2DTO;
import com.example.aws.app.ses.service.AwsSesV2Service;
import com.example.aws.exception.SendFailException;
import jakarta.annotation.Resource;
import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.Body;
import software.amazon.awssdk.services.sesv2.model.Content;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.GetAccountRequest;
import software.amazon.awssdk.services.sesv2.model.GetAccountResponse;
import software.amazon.awssdk.services.sesv2.model.Message;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;
import software.amazon.awssdk.services.sesv2.model.SendQuota;
import software.amazon.awssdk.services.sesv2.model.SesV2Exception;

/**
 * Aws Ses V2 기능 사용을 위한 서비스
 */
@Service
@RequiredArgsConstructor
public class AwsSesV2ServiceImpl implements AwsSesV2Service {

  @Resource
  private SesV2Client sesV2ClientApNortheast2;

  /**
   * AWS SES 이메일 발송
   * 
   * @param reqSendEmail
   */
  @Override
  public void sendEmail(AwsSesV2DTO.ReqSendEmail reqSendEmail) {

    try {

      Destination destination =
          Destination.builder().toAddresses(reqSendEmail.getReceiveEmailList())
              .ccAddresses(reqSendEmail.getCcEmailList())
              .bccAddresses(reqSendEmail.getBccEmailList()).build();// 받는사람 세팅

      // Create the subject and body of the message.
      Content subject = Content.builder().charset(Charset.forName("UTF-8").name())
          .data(reqSendEmail.getEmailTitle()).build();// 이메일 제목

      Content textBody = Content.builder().charset(Charset.forName("UTF-8").name())
          .data(reqSendEmail.getEmailContent()).build();// 이메일 내용

      Body body = Body.builder().html(textBody).build();

      // Create a message with the specified subject and body.
      Message message = Message.builder().subject(subject).body(body).build();

      EmailContent emailContent = EmailContent.builder().simple(message).build();

      // Assemble the email.
      SendEmailRequest request =
          SendEmailRequest.builder()
              .fromEmailAddress(new InternetAddress(reqSendEmail.getSenderEmail().strip(),
                  reqSendEmail.getSenderNm()).toString())
              .destination(destination).content(emailContent).build();

      sesV2ClientApNortheast2.sendEmail(request);

    } catch (UnsupportedEncodingException uee) {

      String errorMsg = String.format(
          "AWS SES 이메일 발송 시 잘못된 발신자 이메일 주소 및 SenderNm이 확인되어 발송을 하지 않습니다. 요청 senderEmail : %s, senderNm : %s",
          reqSendEmail.getSenderEmail(), reqSendEmail.getSenderNm());
      System.out.println(errorMsg);
      throw new SendFailException(errorMsg);

    } catch (SesV2Exception se) {

      if (se.isThrottlingException()) {

        //@formatter:off
        if (se.awsErrorDetails().errorMessage().contains("Daily message quota exceeded")) {// 일일 메시지 할당량 초과
          String errorMsg = "일일 메시지 할당량을 초과하여, 다른 리즌으로 교체하여, 발송을 진행합니다. 해당 리즌의 할당량 확인 필요.";
          System.out.println(errorMsg);
        } else if (se.awsErrorDetails().errorMessage().contains("Maximum sending rate exceeded")) {// 최대 전송 속도 초과
          String errorMsg = "최대 전송속도를 초과하여, 다른 리즌으로 교체하여, 발송을 진행합니다. 해당 리즌의 최대 전송속도를 확인해주세요.";
          System.out.println(errorMsg);
        } else {
          String errorMsg = "일일 할당량 및 최대 전송속도 초과를 제외한 이슈 발생 확인 필요. 다른 리즌으로 교체하여, 발송을 진행합니다.";
          System.out.println(errorMsg);
        }
        
        // TODO ses client를 여러개 사용할 경우 해당 부분에서 해당 client가 일일 할당량 초과 및 최대 TPS를 초과하였을 경우 다른 리즌의 client로 교체하여 발송 할 수 있도록 재귀 함수 호출시 여러개 client 사용이 가능
        //@formatter:on

      } else {// 할당량 관련 Exception을 제외한 나머지 Exception에 대한 예외 처리
        throw new SendFailException(se.awsErrorDetails().errorMessage());
      }

    } catch (Exception e) {
      String errorMsg = String.format(
          "AWS SES 발송 시 확인 되지 않은 에러가 발생하여, 해당 이메일은 발송이 되지 않았습니다. errorMsg : %s, 발신 이메일 : %s",
          e.getMessage(), reqSendEmail.getSenderEmail());
      System.out.println(errorMsg);
      throw new SendFailException(errorMsg);
    }
  }


  /**
   * 일일 할당량 정보에 대한 응답
   * 
   * @param sesV2Client
   * @return
   */
  @Override
  public SendQuota getAwsSendQuotaBySesV2Client(SesV2Client sesV2Client) {

    //@formatter:off
    GetAccountRequest getAccountRequest = GetAccountRequest.builder().build();
    GetAccountResponse getAccountResponse = sesV2Client.getAccount(getAccountRequest);// ses account role 권한을 갖고 있어야 조회 가능
    //@formatter:on

    SendQuota sendQuota = getAccountResponse.sendQuota();
    return sendQuota;
  }

}
