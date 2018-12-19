package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;


/**
 * Created by epro on 2017/12/5.
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 登录
     * @param userName
     * @param password
     * @return
     */
    @Override
    public ServerResponse<User> login(String userName, String password) {
        int resultCount = userMapper.checkUserName(userName);
        if(resultCount == 0){
            return  ServerResponse.createByErrorMessage("用户名为空");
        }
        password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(userName,password);
        if(user == null){
            return ServerResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登录成功",user);
    }

    /**
     * 注册
     * @param user
     * @return
     */
    @Override
    public ServerResponse<String> register(User user) {
        ServerResponse vaildResponse = this.checkValid(user.getUsername(),Const.USERNAME);
        if(!vaildResponse.isSuccess()){
            return vaildResponse;
        }
        vaildResponse = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!vaildResponse.isSuccess()){
            return vaildResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    /**
     * 验证用户名是否存在
     * @param str
     * @param type
     * @return
     */
    public ServerResponse<String> checkValid(String str,String type){
        if(StringUtils.isNotBlank(type)){
            if(Const.USERNAME.equals(type)){
                int resultCount = userMapper.checkUserName(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if(Const.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if(resultCount > 0){
                    return ServerResponse.createByErrorMessage("邮箱已存在");
                }
            }
        }else{
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");
    }

    /**
     * 获取密码提示问题
     * @param username
     * @return
     */
    @Override
    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);

        if(validResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String question = userMapper.selectQuestionByUserName(username);
        if(StringUtils.isNotBlank(question)){
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("密码提示问题为空");
    }

    /**
     * 验证问题提示答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if(resultCount > 0){
            String fotGetToken = java.util.UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username,fotGetToken);
            return ServerResponse.createBySuccess(fotGetToken);
        }
        return ServerResponse.createByErrorMessage("问题回答错误");
    }

    /**
     * 未登录修改密码
     * @param username
     * @param newPassword
     * @param forgetToken
     * @return
     */
    @Override
    public ServerResponse<String> forgetResetPassword(String username, String newPassword, String forgetToken) {
        if(!StringUtils.isNotBlank(forgetToken)){
            return ServerResponse.createByErrorMessage("参数错误，无token");
        }
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()){
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if(!StringUtils.isNotBlank(token)){
            return ServerResponse.createByErrorMessage("token无效或已过期");
        }
        if(StringUtils.equals(token,forgetToken)){
            String md5Password = MD5Util.MD5EncodeUtf8(newPassword);
            int rowCount = userMapper.updatePasswordByUserName(username,md5Password);
            if(rowCount > 0 ){
                return ServerResponse.createBySuccess("修改密码成功");
            }
        }else{
            return ServerResponse.createByErrorMessage("token错误，请重新获取重置密码的token");
        }

        return  ServerResponse.createByErrorMessage("修改密码失败");
    }

    /**
     * 登录后重置密码
     * @param oldPassworod
     * @param newPassword
     * @param user
     * @return
     */
    @Override
    public ServerResponse<String> ResetPassword(String oldPassworod, String newPassword, User user) {
        int resultCount = userMapper.checkPassword(user.getId(),MD5Util.MD5EncodeUtf8(oldPassworod));
        if(resultCount > 0){
            String password = MD5Util.MD5EncodeUtf8(newPassword);
            user.setPassword(password);
            int rowCount = userMapper.updateByPrimaryKeySelective(user);
            if(rowCount > 0){
                return ServerResponse.createBySuccess("修改密码成功");
            }else {
                return ServerResponse.createByErrorMessage("修改密码失败");
            }
        }else{
            return ServerResponse.createByErrorMessage("密码不正确");
        }

    }

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    @Override
    public ServerResponse<User> updateInformation(User user) {
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount > 0 ){
            return ServerResponse.createByErrorMessage("邮箱已存在，无法更新");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setAnswer(user.getAnswer());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setPhone(user.getPhone());
        int rowCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(rowCount > 0){
            return ServerResponse.createBySuccess("个人信息修改成功",updateUser);
        }
        return ServerResponse.createByErrorMessage("个人信息修改失败");
    }

    /**
     * 获取用户信息
     * @param id
     * @return
     */
    @Override
    public ServerResponse<User> getInformation(Integer id) {
        User user = userMapper.selectByPrimaryKey(id);
        if(user == null){
            return ServerResponse.createByErrorMessage("查无此用户");
        }
        user.setPhone(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }

    /**
     * 验证用户是否是管理员
     * @param
     * @return
     */
    public ServerResponse checkAdminRole(HttpSession session ){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return  ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        if(user != null && user.getRole().intValue()  == Const.Role.ROLE_ADMIN ){
            return ServerResponse.createBySuccess();
        }
        return  ServerResponse.createByErrorMessage("您无权此操作");

    }
}
