package com.tjkcht.pojo;

/**
 * 统一http响应结果封装
 *
 * @author
 * @date 2018/06/09
 */
public class Result {
    /**
     * 状态码 0--成功
     */
    private final Integer code;
    /**
     * 消息
     */
    private final String message;
    /**
     * 消息类型
     */
    private final int msgType;
    /**
     * 数据内容，比如列表，实体
     */
    private final Object data;

    private Result(final Builder builder) {
        this.code = builder.code;
        this.message = builder.message;
        this.data = builder.data;
        this.msgType = builder.msgType;
    }

    public static class Builder {
        private final Integer code;
        private String message;
        private Object data;
        private int msgType;

        public Builder(final Integer code) {
            this.code = code;
        }

        public Builder message(final String message) {
            this.message = message;
            return this;
        }

        public Builder data(final Object data) {
            this.data = data;
            return this;
        }

        public Builder msgType(final int msgType) {
            this.msgType = msgType;
            return this;
        }

        public Result build() {
            return new Result(this);
        }
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public Object getData() {
        return this.data;
    }

    public int getMsgType() {
        return msgType;
    }
}
