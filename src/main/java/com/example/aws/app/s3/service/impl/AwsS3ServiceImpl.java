package com.example.aws.app.s3.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Duration;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.aws.app.s3.service.AwsS3Service;
import jakarta.annotation.Resource;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Service
public class AwsS3ServiceImpl implements AwsS3Service {

  /** 파일 다운로드 유효한 최대 시간 **/
  private final int FILE_DOWNLOAD_VALID_MAX_MINUTE = 30;
  
  @Resource(name = "s3ClientApNortheast2")
  private S3Client s3ClientApNortheast2;

  @Resource(name = "s3PresignerApNortheast2")
  private S3Presigner s3PresignerApNortheast2;
  
  /**
   * s3에 파일 업로드
   * 
   * @param file
   * @param uploadFilePath
   */
  @Override
  public void uploadS3Object(MultipartFile file, String uploadFilePath) {

    if (file == null || file.isEmpty()) {// 파일 존재 여부 체크
      System.out.println("업로드 할 파일이 존재하지 않습니다.");
      return;
    }

    // TODO 파일 확장자 체크가 필요하다면 체크 후 노티 후 리턴

    PutObjectRequest putObjectRequest =
        PutObjectRequest.builder().bucket("bucketName").key(uploadFilePath).build();

    try {
      RequestBody requestBody = getRequestBody(file);

      s3ClientApNortheast2.putObject(putObjectRequest, requestBody);

      System.out.println("s3 파일 업로드 성공");

    } catch (IOException ioe) {
      String errorMsg =
          String.format("s3 업로드 파일 읽는 중 오류 발생 > 파일명  : %s", file.getOriginalFilename());
      System.out.println(errorMsg);
    }

  }

  /**
   * S3 PresignerUrl 가져오기
   * 
   * @param uploadFileFullPath
   * @return
   */
  @Override
  public String getPresignedUrl(String uploadFileFullPath) {

    GetObjectRequest getObjectRequest =
        GetObjectRequest.builder().bucket("bucketName").key(uploadFileFullPath).build();

    GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(FILE_DOWNLOAD_VALID_MAX_MINUTE))
        .getObjectRequest(getObjectRequest).build();

    PresignedGetObjectRequest presignedGetObjectRequest =
        s3PresignerApNortheast2.presignGetObject(getObjectPresignRequest);

    return presignedGetObjectRequest.url().toString();
  }


  /**
   * s3 업로드 파일 읽기
   * 
   * @param multipartFile
   * @return
   * @throws IOException
   */
  private RequestBody getRequestBody(MultipartFile multipartFile) throws IOException {

    byte[] bytes = IOUtils.toByteArray(multipartFile.getInputStream());
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

    return RequestBody.fromInputStream(byteArrayInputStream, bytes.length);
  }

}
