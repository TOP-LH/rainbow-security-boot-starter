package com.rainbow.security.service;

/**
 * @author lihao3
 * @Date 2021/1/7 13:46
 */
public interface RainbowTokenService {

    /**
     * 生成Token令牌（默认格式为：UUID+-+四位随机数）
     * 切记：Token一定要唯一，否则将会出错
     *
     * @param load 负载因素
     * @return Token令牌
     */
    String generateToken(Object load);
}
