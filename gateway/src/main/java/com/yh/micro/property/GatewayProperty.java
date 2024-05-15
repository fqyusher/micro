package com.yh.micro.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author yusher
 * @date 4/22/24 11:37 AM
 */
@Configuration
@ConfigurationProperties(prefix = "spring.cloud.nacos.config")
@Data
public class GatewayProperty {
    private String serverAddr;

    private String dataId;

    private String group;

    private String namespace;
}
