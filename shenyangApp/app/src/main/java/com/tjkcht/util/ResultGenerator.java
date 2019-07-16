package com.tjkcht.util;

import com.tjkcht.pojo.Result;

import cz.msebera.android.httpclient.HttpStatus;

/**
 * 响应结果生成工具
 *
 * @author
 * @date 2018/06/09
 */
public class ResultGenerator {
    private static final String DEFAULT_OK_MESSAGE = "OK";

    public static Result genOkResult() {
        return new Result
                .Builder(HttpStatus.SC_OK)
                .message(DEFAULT_OK_MESSAGE)
                .data(null)
                .msgType(0)
                .build();
    }

    public static Result genOkResult(final int msgType, final Object data) {
        return new Result
                .Builder(HttpStatus.SC_OK)
                .message(DEFAULT_OK_MESSAGE)
                .data(data)
                .msgType(msgType)
                .build();
    }

    public static Result genFailedResult( final Integer code, final String message) {
        return new Result
                .Builder(code)
                .message(message)
                .data(null)
                .msgType(-1)
                .build();
    }
}
