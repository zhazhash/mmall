package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUserName(String userName);

    User selectLogin(@Param("userName") String userName, @Param("password") String password);

    int checkEmail(String email);

    String selectQuestionByUserName(String username);

    int checkAnswer(@Param("username") String username,@Param("question")  String question, @Param("answer") String answer);

    int updatePasswordByUserName(@Param("username")String username ,@Param("md5Password")String md5Password);

    int checkPassword(@Param("id")Integer id,@Param("oldPassworod") String oldPassworod);

    int checkEmailByUserId(@Param("email")String email, @Param("id")Integer id);
}