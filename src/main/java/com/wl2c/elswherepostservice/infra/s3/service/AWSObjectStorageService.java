package com.wl2c.elswherepostservice.infra.s3.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.wl2c.elswherepostservice.infra.s3.dto.FileRequest;
import com.wl2c.elswherepostservice.infra.s3.dto.ImageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AWSObjectStorageService {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public void uploadFile(FileRequest file, String objectName) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType().toString());
        metadata.setContentLength(file.getSize());

        try {
            amazonS3Client.putObject(bucket, objectName, file.getInputStream(), metadata);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadImage(ImageRequest image, String objectName) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(image.getContentType().toString());
        metadata.setContentLength(image.getSize());

        try {
            amazonS3Client.putObject(bucket, objectName, image.getInputStream(), metadata);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getImageUrl(String objectName) {
        return amazonS3Client.getUrl(bucket, objectName).toString();
    }

    public String getFileUrl(String objectName) {
        return amazonS3Client.getUrl(bucket, objectName).toString();
    }

    public void delete(String objectName) {
        amazonS3Client.deleteObject(bucket, objectName);
    }
}
