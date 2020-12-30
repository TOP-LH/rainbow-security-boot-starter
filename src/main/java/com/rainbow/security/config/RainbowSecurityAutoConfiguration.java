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
 * 自动装配类
 *
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
        RainbowSecurityAspect rainbowSecurityAspect = new RainbowSecurityAspect();
        log.info("\n" +
                "            _       _                                                   _ _         \n" +
                "           (_)     | |                                                 (_) |        \n" +
                "  _ __ __ _ _ _ __ | |__   _____      ________ ___  ___  ___ _   _ _ __ _| |_ _   _ \n" +
                " | '__/ _` | | '_ \\| '_ \\ / _ \\ \\ /\\ / /______/ __|/ _ \\/ __| | | | '__| | __| | | |\n" +
                " | | | (_| | | | | | |_) | (_) \\ V  V /       \\__ \\  __/ (__| |_| | |  | | |_| |_| |\n" +
                " |_|  \\__,_|_|_| |_|_.__/ \\___/ \\_/\\_/        |___/\\___|\\___|\\__,_|_|  |_|\\__|\\__, |\n" +
                "                                                                               __/ |\n" +
                "                                                                              |___/ \n" +
                " : : rainbow-security : :                                         (v{})","1.0.2");
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
