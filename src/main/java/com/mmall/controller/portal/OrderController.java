package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2019/9/21.
 */
@Controller
@RequestMapping("/order/")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse pay(Long orderNo, HttpSession session, HttpServletRequest request){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"请您先登录");
        }
        String path = request.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(user.getId(),path,orderNo);
    }

    /**
     * 支付宝回调函数
     * @return
     */
    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public ServerResponse alipayCallBack(HttpServletRequest request){
        Map<String,String> maps = Maps.newHashMap();

        Map resultMaps = request.getParameterMap();
        for(Iterator iterator = resultMaps.keySet().iterator(); iterator.hasNext();){
            String name = (String) iterator.next();
            String [] values = (String[]) resultMaps.get(name);
            String value = "";
            for(int i = 0 ; i < values.length ; i++){
                value = (i == values.length -1) ? value + values[i] :  value + values[i] +",";
            }
            maps.put(name,value);
        }
        logger.info("支付宝回调sign:{},trade_status:{},参数:{}",maps.get("sign"),maps.get("trade_status"),maps.toString());
        maps.remove("sign_type");
        try {
            boolean rsaCheckV2Flag = AlipaySignature.rsaCheckV2(maps, Configs.getPublicKey(),"utf-8",Configs.getSignType());
            if(!rsaCheckV2Flag){
                return ServerResponse.createByErrorMessage("支付宝验证失败");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝回调异常",e);
        }
        //验证订单是否正确 todo
        //return iOrderService.checkOrderStatus(Long.valueOf(maps.get("out_trade_no ")), Double.valueOf(maps.get("total_amount ")));


        return null;
    }
}
