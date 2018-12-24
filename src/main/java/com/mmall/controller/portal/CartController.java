package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2018/12/20.
 */
@Controller
@RequestMapping("/cart/")
public class CartController {
    @Autowired
    private ICartService iCartService;

    @RequestMapping("add")
    @ResponseBody
    public ServerResponse add(HttpSession session, Integer productId, Integer count) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        return iCartService.add(productId, user.getId(), count);
    }

    @RequestMapping("update")
    @ResponseBody
    public ServerResponse update(HttpSession session, Integer productId, Integer count) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，请登录");
        }
        return iCartService.update(productId, user.getId(), count);
    }

    @RequestMapping("delete")
    @ResponseBody
    public ServerResponse deleteProduct(HttpSession session, String productIds) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        return iCartService.deleteProduct(productIds, user.getId());
    }

    @RequestMapping("list")
    @ResponseBody
    public ServerResponse list(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        return iCartService.list(user.getId());
    }

    @RequestMapping("select_all")
    @ResponseBody
    public ServerResponse selectAll(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        return iCartService.selectOrUnSelect(user.getId(), null, Const.Cart.CHECKED);
    }

    @RequestMapping("un_select_all")
    @ResponseBody
    public ServerResponse UnSelectAll(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        return iCartService.selectOrUnSelect(user.getId(), null, Const.Cart.UN_CHECKED);
    }

    @RequestMapping("select")
    @ResponseBody
    public ServerResponse select(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        return iCartService.selectOrUnSelect(user.getId(), productId, Const.Cart.CHECKED);
    }

    @RequestMapping("un_select")
    @ResponseBody
    public ServerResponse UnSelect(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录");
        }
        return iCartService.selectOrUnSelect(user.getId(), productId, Const.Cart.UN_CHECKED);
    }

    @RequestMapping("get_cart_product_count")
    @ResponseBody
    public ServerResponse getCartProductCount(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createBySuccess("0");
        }
        return iCartService.selectCartProductCount(user.getId());
    }

}
