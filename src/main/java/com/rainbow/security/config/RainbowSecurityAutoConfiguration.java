package com.rainbow.security.config;

import com.rainbow.security.aop.aspect.RainbowSecurityAspect;
import com.rainbow.security.propertie.RainbowSecurityProperties;
import com.rainbow.security.service.RainbowSecurityService;
import com.rainbow.security.service.impl.RainbowSecurityServiceImpl;
import com.rainbow.security.util.RainbowSecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lihao3
 * @Date 2020/12/23 11:27
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(RainbowSecurityProperties.class)
public class RainbowSecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RainbowSecurityAspect rainbowSecurityAspect() {
        log.info("rainbow-security开始初始化");
        RainbowSecurityAspect rainbowSecurityAspect = new RainbowSecurityAspect();
        log.info("rainbow-security初始化完成");
        return rainbowSecurityAspect;
    }

    @Bean
    @ConditionalOnMissingBean
    public RainbowSecurityUtils rainbowSecurityUtils() {
        return new RainbowSecurityUtils();
    }

    @Bean
    @ConditionalOnMissingBean(RainbowSecurityService.class)
    public RainbowSecurityServiceImpl rainbowSecurityServiceImpl() {
        return new RainbowSecurityServiceImpl();
    }
}
