package com.dataart.joinme.configurations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "amazon.s3")
@ConstructorBinding
@AllArgsConstructor
@Getter
public class AwsCredentials {
    private final String awsAccessKeyId;
    private final String awsSecretAccessKey;
    private final String s3Bucket;
    private final String s3Region;
}
