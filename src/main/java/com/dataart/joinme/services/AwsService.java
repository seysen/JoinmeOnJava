package com.dataart.joinme.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.dataart.joinme.configurations.AwsCredentials;
import com.dataart.joinme.configurations.UploadPaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class AwsService {
    private final AmazonS3 s3client;
    private final AwsCredentials awsCredentials;
    private final UploadPaths uploadPaths;

    private final Logger logger = LoggerFactory.getLogger(AwsService.class);

    @Autowired
    public AwsService(AmazonS3 s3client, AwsCredentials awsCredentials, UploadPaths uploadPaths) {
        this.s3client = s3client;
        this.awsCredentials = awsCredentials;
        this.uploadPaths = uploadPaths;
    }

    public String uploadToAws(String storeName, File storeDestination) {
        logger.debug("Uploading to AWS file: {} from: {}", storeName, storeDestination);
        s3client.putObject(
                new PutObjectRequest(
                        awsCredentials.getS3Bucket(),
                        storeName,
                        storeDestination
                )
        );
        return uploadPaths.getAws() + storeName;
    }
}
