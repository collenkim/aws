package com.example.aws.app.ses.service;

import com.example.aws.app.ses.dto.AwsSesV2DTO;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.SendQuota;

/**
 * Aws Ses V2 기능 사용을 위한 인터페이스
 */
public interface AwsSesV2Service {

  /**
   * AWS SES 이메일 발송
   * 
   * @param reqSendEmail
   */
  void sendEmail(AwsSesV2DTO.ReqSendEmail reqSendEmail);

  /**
   * 일일 할당량 정보 조회
   * 
   * @param sesV2Client
   * @return
   */
  SendQuota getAwsSendQuotaBySesV2Client(SesV2Client sesV2Client);
  
  /**
   * AWS SES 금지 목록에 이메일이 포함되어 있는지 여부 체크
   * 
   * @param sesV2Client
   * @param emailAddress
   * @return
   */
  boolean isValidAwsSesSuppressedEmailAddress(SesV2Client sesV2Client, String emailAddress);
  
  /**
   * AWS SES 금지 목록에서 이메일 주소 제거
   * 
   * @param sesV2Client
   * @param emailAddress
   */
  void deleteAwsSesSuppressedEmailAddress(SesV2Client sesV2Client, String emailAddress);
}
