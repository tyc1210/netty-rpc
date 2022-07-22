package com.tyc.common.model;

/**
 * 类描述
 *
 * @author tyc
 * @version 1.0
 * @date 2022-07-21 18:06:27
 */
public class RpcResult {
    private Long id;
    private String resultData;

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
