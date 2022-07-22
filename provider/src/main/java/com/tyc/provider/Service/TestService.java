package com.tyc.provider.Service;

import com.tyc.common.model.User;
import org.springframework.stereotype.Service;

/**
 * 类描述
 *
 * @author tyc
 * @version 1.0
 * @date 2022-07-21 16:16:58
 */
@Service
public class TestService {
    public User getUser(){
        User user = new User(1L,"张三",22);
        return user;
    }
}
