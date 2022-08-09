package com.tyc.consumer.controller;

import com.tyc.common.model.User;
import com.tyc.common.service.UserService;
import com.tyc.consumer.annotation.RpcReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试
 *
 * @author tyc
 * @version 1.0
 * @date 2022-07-22 10:04:11
 */
@RestController
public class TestController {

    @RpcReference
    private UserService userService;

    @GetMapping("/user/{id}")
    public User getUserServiceById(@PathVariable Long id){
        return userService.getUserById(id);
    }

}
