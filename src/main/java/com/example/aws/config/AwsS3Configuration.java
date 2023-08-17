package com.example.aws.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsS3Configuration {

	
	/**
	 * S3 Client (S3 관련 기능 제공 클라이언트)
	 * 
	 * @return
	 */
	@Bean
	public S3Client s3Client() {
		
		Region region = Region.AP_NORTHEAST_2;
		
		//Role 기반 클라이언트라면 credentialsProvider는 필요 없음
		S3Client s3client = S3Client.builder()
										.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("accessKeyId", "secretAccessKeyId")))
										.region(region)
										.build();
		
		return s3client; 
	}
	
}
