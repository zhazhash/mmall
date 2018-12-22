package com.mmall.service;

import com.mmall.common.ServerResponse;

/**
 * Created by Administrator on 2018/12/20.
 */
public interface ICartService {
    ServerResponse add(Integer productId, Integer userId, Integer count);
}
