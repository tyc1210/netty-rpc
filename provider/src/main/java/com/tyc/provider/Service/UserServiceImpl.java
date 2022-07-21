package com.tyc.provider.Service;

import com.tyc.common.model.User;
import com.tyc.common.service.UserService;
import com.tyc.provider.annotation.RpcService;
import org.springframework.stereotype.Component;

import java.util.Map;

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
//    private static Map<Long,User> userMap;
//
//    static {
//        // 模拟数据库读取数据
//        User u1 = new User(1L,"张三",20);
//        User u2 = new User(2L,"李四",21);
//        User u3 = new User(3L,"王五",22);
//        userMap.put(u1.getId(),u1);
//        userMap.put(u2.getId(),u2);
//        userMap.put(u3.getId(),u3);
//    }

    @Override
    public User getUserById(Long id) {
//        return userMap.get(id);
        return null;
    }
}
