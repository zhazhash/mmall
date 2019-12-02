package com.mmall.service;

import com.mmall.common.ServerResponse;

/**
 * Created by Administrator on 2019/9/21.
 */
public interface IOrderService {
    /**
     * 查询订单调用支付宝当面付功能
     * @param userId 用户id
     * @param path 路径
     * @param orderNo 订单号
     * @return
     */
    ServerResponse pay(Integer userId, String path, Long orderNo);

    /**
     *验证订单是否正常
     * @param orderNo 商品id
     * @param quantity 商品总价
     * @return
     */
    ServerResponse checkOrderStatus(Long orderNo , Double quantity);
}
