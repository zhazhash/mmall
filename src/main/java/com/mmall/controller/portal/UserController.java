package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by epro on 2017/12/5.
 */
@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService userService;

    /**
     * 用户登录
     * @param userName
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value="login.do" ,method= RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login (String userName , String password , HttpSession session){
        ServerResponse<User> response = userService.login(userName,password);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    @RequestMapping(value = "logout.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccessMessage("退出成功");
    }

    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        return userService.register(user);
    }

    @RequestMapping(value = "checkValid",method = RequestMethod.POST)
    public ServerResponse<String> checkValid(String str,String type){
        return userService.checkValid(str, type);
    }

}
