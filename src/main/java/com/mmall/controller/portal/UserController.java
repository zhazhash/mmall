package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.sun.org.apache.regexp.internal.RE;
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
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value="login.do" ,method= RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login (String username , String password , HttpSession session){
        ServerResponse<User> response = userService.login(username,password);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    /**
     * 用户退出
     * @param session
     * @return
     */
    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccessMessage("退出成功");
    }

    /**
     * 用户注册
     * @param user
     * @return
     */
    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        return userService.register(user);
    }

    /**
     * 验证用户名或邮箱
     * @param str
     * @param type
     * @return
     */
    @RequestMapping(value = "check_valid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str,String type){
        return userService.checkValid(str, type);
    }

    /**
     * 获取用户信息
     * @param session
     * @return
     */
    @RequestMapping(value = "get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user != null){
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户尚未登录，无法获取用户信息");
    }

    /**
     * 获取用户密码提示问题
     * @param username
     * @return
     */
    @RequestMapping(value = "forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){
         return userService.selectQuestion(username);
    }

    /**
     * 获取用户密码提示答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping(value = "forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question ,String answer){
        return userService.checkAnswer(username,question,answer);
    }

    /**
     * 使用问题更改密码
     * @param username
     * @param newPassword
     * @param forgetToken
     * @return
     */
    @RequestMapping(value = "forget_reset_password.do")
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username,String newPassword,String forgetToken){
        return userService.forgetResetPassword(username,newPassword,forgetToken);
    }

    /**
     * 登录中更改密码
     * @param session
     * @param oldPassworod
     * @param newPassword
     * @return
     */
    @RequestMapping(value = "reset_password.do")
    @ResponseBody
    public ServerResponse<String> resetPassword(HttpSession session , String oldPassworod,String newPassword){
       User user = (User) session.getAttribute(Const.CURRENT_USER);
            if(user == null){
                return ServerResponse.createByErrorMessage("用户未登录");
            }
           return  userService.ResetPassword(oldPassworod,newPassword,user);
    }

    /**
     * 更新用户信息
     * @param session
     * @param user
     * @return
     */
    @RequestMapping(value = "update_information.do")
    @ResponseBody
    public ServerResponse<User> updateInformation (HttpSession session,User user){
        User currntUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currntUser == null){
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        user.setId(currntUser.getId());
        user.setUsername(currntUser.getUsername());
        ServerResponse<User> response =  userService.updateInformation(user);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    @RequestMapping(value = "get_information.do")
    @ResponseBody
    public ServerResponse<User> getInformation(HttpSession session){
        User currntUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currntUser == null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请进行登录");
        }
        return userService.getInformation(currntUser.getId());
    }

}
