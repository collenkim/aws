package com.example.aws.exception;

/**
 * 발송 실패에 대한 Exception
 */
public class SendFailException extends RuntimeException {

  /**
   * class 직렬화 Version Id
   */
  private static final long serialVersionUID = 7638238261123477123L;

  public SendFailException() {
    super();
  }

  public SendFailException(String msg) {
    super(msg);
  }

}
