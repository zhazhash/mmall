package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;

/**
 * Created by epro on 2017/12/5.
 */
public interface IUserService {

    ServerResponse<User> login(String userName,String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str, String type);
}
