package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2018/12/25.
 */
@Controller
@RequestMapping("/shipping/")
public class ShippingController {

    @Autowired
    private IShippingService iShippingService;

    @RequestMapping("add")
    @ResponseBody
    public ServerResponse add(HttpSession session, Shipping shipping) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(), "请先登录");
        }
        return iShippingService.add(user.getId(), shipping);
    }

    @RequestMapping("delete")
    @ResponseBody
    public ServerResponse delete(HttpSession session , Integer shippingId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"请您先登录");
        }
        return iShippingService.delete(user.getId(),shippingId);
    }

    @RequestMapping("update")
    @ResponseBody
    public ServerResponse update(HttpSession session, Shipping shipping){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"请您先登录");
        }
        return iShippingService.update(user.getId(),shipping);
    }

    @RequestMapping("select")
    @ResponseBody
    public ServerResponse select(HttpSession session,String shippingId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"请您先登录");
        }
        return iShippingService.select(user.getId(),shippingId);
    }

    @RequestMapping("list")
    @ResponseBody
    public ServerResponse list(HttpSession session, @RequestParam(value = "pageNum" ,defaultValue = "1") int pageNum,
                               @RequestParam(value = "pageSize" ,defaultValue = "10") int pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"请您先登录");
        }
        return iShippingService.getList(user.getId(),pageNum,pageSize);
    }
}
