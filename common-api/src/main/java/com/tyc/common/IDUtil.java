package com.tyc.common;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 类描述
 *
 * @author tyc
 * @version 1.0
 * @date 2022-07-21 11:26:01
 */
public class IDUtil {
    private static AtomicInteger id = new AtomicInteger(0);

    public static String getId(){
        return UUID.randomUUID().toString().replace("-","");
    }

    public static Integer getRequestId(){
        Integer result = id.incrementAndGet();
        if(result.equals(Integer.MAX_VALUE)){
            id.set(0);
        }
        return result;
    }
}
