package com.example.aws.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class AwsSqsConfiguration {

	/**
	 * Sqs Client (sqs 관련 기능 제공 클라이언트)
	 * 
	 * @return
	 */
	@Bean
	public SqsClient sqsClient() {
		
		Region region = Region.AP_NORTHEAST_2;
		
		//Role 기반 클라이언트라면 credentialsProvider는 필요 없음
		SqsClient sqsClient = SqsClient.builder()
										.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("accessKeyId", "secretAccessKeyId")))
										.region(region)
										.build();
		
		return sqsClient; 
	}
}
