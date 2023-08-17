package com.example.aws.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2Client;

@Configuration
public class AwsSesConfiguration {


  /**
   * SesV2 Client (ses 관련 기능 제공 클라이언트)
   * 
   * @return
   */
  @Bean("sesV2ClientApNortheast2")
  public SesV2Client sesV2ClientApNortheast2() {

    Region region = Region.AP_NORTHEAST_2;// 서울 리즌

    // Role 기반 클라이언트라면 credentialsProvider는 필요 없음
    SesV2Client sesV2Client = SesV2Client.builder()
        .credentialsProvider(StaticCredentialsProvider
            .create(AwsBasicCredentials.create("accessKeyId", "secretAccessKeyId")))
        .region(region).build();

    return sesV2Client;
  }

}
