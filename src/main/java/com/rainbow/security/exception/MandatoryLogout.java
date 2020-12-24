package com.rainbow.security.exception;

/**
 * 被挤下线的异常
 *
 * @author lihao3
 * @Date 2020/12/24 12:55
 */
public class MandatoryLogout extends RuntimeException {

    public MandatoryLogout(String message) {
        super(message);
    }

}
