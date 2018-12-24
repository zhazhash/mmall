package com.mmall.service;

import com.mmall.common.ServerResponse;

/**
 * Created by Administrator on 2018/12/20.
 */
public interface ICartService {
    ServerResponse add(Integer productId, Integer userId, Integer count);

    ServerResponse update(Integer productId, Integer userId, Integer count);

    ServerResponse deleteProduct(String productIds, Integer userId);

    ServerResponse list(Integer userId);

    ServerResponse selectOrUnSelect(Integer userId, Integer productId, int checked);

    ServerResponse selectCartProductCount(Integer userId);
}
