package com.tyc.common.model;

/**
 * 类描述
 *
 * @author tyc
 * @version 1.0
 * @date 2022-08-03 15:02:34
 */
public enum SerializeType {
    JSON("JSON",0);

    private String msg;
    private Integer code;

    SerializeType(String msg, Integer code) {
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }


    public Integer getCode() {
        return code;
    }
}
