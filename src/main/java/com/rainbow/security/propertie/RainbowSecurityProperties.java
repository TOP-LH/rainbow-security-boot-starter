package com.rainbow.security.propertie;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author lihao3
 * @Date 2020/12/23 11:27
 */
@Data
@Component
@ConfigurationProperties(prefix = "rainbow.security")
public class RainbowSecurityProperties {

    /**
     * token名称 (同时也是cookie名称)
     */
    private String tokenName = "rainbowSecurity";

    /**
     * token有效期，单位s 默认1天
     */
    private long timeout = 24 * 60 * 60;

    /**
     * 在多人登录同一账号时，是否共享会话 (为true时共用一个，为false时新登录挤掉旧登录)
     */
    private Boolean isShare = true;

    /**
     * 是否尝试从请求体里读取token
     */
    private Boolean isReadBody = false;

    /**
     * 是否尝试从header里读取token
     */
    private Boolean isReadHead = true;

    /**
     * 是否尝试从cookie里读取token
     */
    private Boolean isReadCookie = true;
}
