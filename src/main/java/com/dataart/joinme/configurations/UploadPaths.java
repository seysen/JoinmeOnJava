package com.dataart.joinme.configurations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "upload")
@ConstructorBinding
@AllArgsConstructor
@Getter
public class UploadPaths {
    private final String path;
    private final String url;
    private final String aws;
}
