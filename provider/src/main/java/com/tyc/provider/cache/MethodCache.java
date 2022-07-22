package com.tyc.provider.cache;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类描述
 *
 * @author tyc
 * @version 1.0
 * @date 2022-07-21 17:28:38
 */
public class MethodCache {
    public static Map<String, MethodContext> methodMap = new ConcurrentHashMap<>();

    public static class MethodContext{
        private Method method;
        private String beanName;

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public String getBeanName() {
            return beanName;
        }

        public void setBeanName(String beanName) {
            this.beanName = beanName;
        }

        public MethodContext(Method method, String beanName) {
            this.method = method;
            this.beanName = beanName;
        }
    }
}
