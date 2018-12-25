package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2018/12/25.
 */
@Service
public class ShippingSercieImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public ServerResponse add(Integer userId, Shipping shipping) {
        if (userId != null && shipping != null) {
            shipping.setUserId(userId);
            int intCount = shippingMapper.insert(shipping);
            if (intCount > 0) {
                return ServerResponse.createBySuccess("地址添加成功", shipping.getId());
            }
        }
        return ServerResponse.createByErrorMessage("地址添加失败");
    }

    @Override
    public ServerResponse delete(Integer userId, Integer shippingId) {
        if (userId != null && shippingId != null) {
            int intCount = shippingMapper.deleteByShipIdAndUserId(userId, shippingId);
            if (intCount > 0) {
                return ServerResponse.createBySuccess();
            }
        }
        return ServerResponse.createByErrorMessage("地址删除失败");
    }

    @Override
    public ServerResponse update(Integer userId, Shipping shipping) {
        if (userId != null && shipping != null) {
            shipping.setUserId(userId);
            int intCount = shippingMapper.updateShippingByUserId(shipping);
            if (intCount > 0) {
                return ServerResponse.createBySuccess();
            }
        }
        return ServerResponse.createByErrorMessage("地址修改失败");
    }

    @Override
    public ServerResponse select(Integer userId, String shippingId) {
        if (userId != null && shippingId != null) {
            Shipping shipping = shippingMapper.selectByUserIdAndShippingId(userId,shippingId);
            if(shipping != null){
                return ServerResponse.createBySuccess(shipping);
            }
        }
        return ServerResponse.createByErrorMessage("地址获取失败");
    }

    @Override
    public ServerResponse getList(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
