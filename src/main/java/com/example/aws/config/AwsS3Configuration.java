package com.example.aws.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class AwsS3Configuration {


  /**
   * S3 Client (S3 관련 기능 제공 클라이언트)
   * 
   * @return
   */
  @Bean("s3ClientApNortheast2")
  public S3Client s3ClientApNortheast2() {

    Region region = Region.AP_NORTHEAST_2;

    //@formatter:off
    S3Configuration s3Configuration =
        S3Configuration.builder().pathStyleAccessEnabled(true).build();// s3 버킷에 접근할 때 호스트 이름 대신 경로 URL을 사용하도록 설정하는 옵션
    //@formatter:on
    
    // Role 기반 클라이언트라면 credentialsProvider는 필요 없음
    S3Client s3client = S3Client.builder()
        .credentialsProvider(StaticCredentialsProvider
            .create(AwsBasicCredentials.create("accessKeyId", "secretAccessKeyId")))
        .region(region).serviceConfiguration(s3Configuration).build();

    return s3client;
  }
  
  /**
   * S3 PreSinger Client (S3 관련 기능 제공 클라이언트)
   * 
   * @return
   */
  @Bean("s3PresignerApNortheast2")
  public S3Presigner s3PresignerApNortheast2() {

    Region region = Region.AP_NORTHEAST_2;

    // Role 기반 클라이언트라면 credentialsProvider는 필요 없음
    S3Presigner sePresigner = S3Presigner.builder()
        .credentialsProvider(StaticCredentialsProvider
            .create(AwsBasicCredentials.create("accessKeyId", "secretAccessKeyId")))
        .region(region).build();

    return sePresigner;
  }

}
