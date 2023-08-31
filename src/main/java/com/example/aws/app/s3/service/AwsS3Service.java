package com.example.aws.app.s3.service;

import org.springframework.web.multipart.MultipartFile;

public interface AwsS3Service {

  /**
   * s3에 파일 업로드
   * 
   * @param file
   * @param uploadPath
   */
  void uploadS3Object(MultipartFile file, String uploadPath);
  
  /**
   * S3 PresignerUrl 가져오기
   * 
   * @param uploadFileFullPath
   * @return
   */
  String getPresignedUrl(String uploadFileFullPath);
}
