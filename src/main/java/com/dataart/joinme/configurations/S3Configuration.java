package com.dataart.joinme.configurations;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Configuration {
    private final AwsCredentials awsCredentials;

    @Autowired
    public S3Configuration(AwsCredentials awsCredentials) {
        this.awsCredentials = awsCredentials;
    }

    @Bean
    public AmazonS3 s3client() {
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(
                awsCredentials.getAwsAccessKeyId(), awsCredentials.getAwsSecretAccessKey());
        return AmazonS3ClientBuilder.standard().withRegion(Regions.fromName(awsCredentials.getS3Region()))
                .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
                .build();
    }
}
