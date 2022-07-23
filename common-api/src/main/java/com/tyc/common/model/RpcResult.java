package com.tyc.common.model;

/**
 * 类描述
 *
 * @author tyc
 * @version 1.0
 * @date 2022-07-21 18:06:27
 */
public class RpcResult {
    private Integer code;
    private Long id;
    private String resultData;

    public RpcResult(Integer code, Long id, String resultData) {
        this.code = code;
        this.id = id;
        this.resultData = resultData;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResultData() {
        return resultData;
    }

    public void setResultData(String resultData) {
        this.resultData = resultData;
    }
}
