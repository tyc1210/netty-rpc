package com.tyc.provider.Service;

import com.tyc.common.model.User;
import com.tyc.common.service.UserService;
import com.tyc.provider.annotation.RpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 类描述
 *
 * @author tyc
 * @version 1.0
 * @date 2022-07-21 11:57:56
 */
@RpcService
@Component
public class UserServiceImpl implements UserService {
    @Autowired
    private TestService testService;

    @Override
    public User getUserById(Long id) {
        return testService.getUser();
    }
}
