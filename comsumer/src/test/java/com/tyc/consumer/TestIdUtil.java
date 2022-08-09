package com.tyc.consumer;

import com.tyc.common.IDUtil;

/**
 * 类描述
 *
 * @author tyc
 * @version 1.0
 * @date 2022-08-09 10:40:45
 */

public class TestIdUtil {
    public static void main(String[] args) {

        for (long i = 0; i < 21474837481L; i++) {
            IDUtil.getRequestId();
        }
        System.out.println(IDUtil.getRequestId());
    }
}
