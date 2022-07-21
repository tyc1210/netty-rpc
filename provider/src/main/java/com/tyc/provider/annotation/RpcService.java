package com.tyc.provider.annotation;

import org.springframework.context.annotation.Configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 类描述
 *
 * @author tyc
 * @version 1.0
 * @date 2022-07-21 14:03:39
 */
@Target({ElementType.TYPE})
@Configuration
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {

}
