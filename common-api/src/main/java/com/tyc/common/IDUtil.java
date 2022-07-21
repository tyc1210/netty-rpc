package com.tyc.common;

import java.util.UUID;

/**
 * 类描述
 *
 * @author tyc
 * @version 1.0
 * @date 2022-07-21 11:26:01
 */
public class IDUtil {
    public static String getId(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
