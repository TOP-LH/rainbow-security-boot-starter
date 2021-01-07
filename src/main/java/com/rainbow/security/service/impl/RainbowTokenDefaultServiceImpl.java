package com.rainbow.security.service.impl;

import com.rainbow.security.service.RainbowTokenService;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

/**
 * @author lihao3
 * @Date 2021/1/7 13:49
 */
@Service
public class RainbowTokenDefaultServiceImpl implements RainbowTokenService {

    @Override
    public String generateToken(Object load) {
        // 获取UUID
        String uuid = UUID.randomUUID().toString();
        // 获取四位随机数
        String salt = getRandom2(4);
        String token = uuid + "-" + salt;
        return token;
    }

    /**
     * 获取N位随机数
     *
     * @param len N
     * @return
     */
    private static String getRandom2(int len) {
        Random r = new Random();
        StringBuilder rs = new StringBuilder();
        for (int i = 0; i < len; i++) {
            rs.append(r.nextInt(10));
        }
        return rs.toString();
    }
}
