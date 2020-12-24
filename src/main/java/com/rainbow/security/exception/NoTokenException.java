package com.rainbow.security.exception;

/**
 * token为空时的异常
 *
 * @author lihao3
 * @Date 2020/12/24 9:50
 */
public class NoTokenException extends RuntimeException {

    public NoTokenException(String message) {
        super(message);
    }
}
