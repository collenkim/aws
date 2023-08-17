package com.example.aws.app.ses.dto;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class AwsSesV2DTO {

	@Setter
	@Getter
	public static class ReqSendEmail{
		
		private String senderNm;//
		private String emailTitle;//이메일 제목
		private String emailContent;//이메일 내용
		private String senderEmail;//발신자 이메일
		private List<String> receiveEmailList;//수신자 이메일 목록
		private List<String> ccEmailList;//참조 이메일 목록
		private List<String> bccEmailList;//숨은 참조 이메일 목록
	}
	
}
