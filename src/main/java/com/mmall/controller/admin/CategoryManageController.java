package com.mmall.controller.admin;

import com.mmall.common.ServerResponse;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2018/11/26.
 */

@Controller
@RequestMapping("/admin/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping("add_category")
    @ResponseBody
    public ServerResponse addCategory(HttpSession session, String categoryName, @RequestParam(value = "parentid",defaultValue = "0") Integer parentId) {
        ServerResponse  serverResponse = iUserService.checkAdminRole(session);
        if(serverResponse.isSuccess()){
            return iCategoryService.addCategory(categoryName,parentId);
        }
        return  serverResponse;
    }

    @RequestMapping("update_category")
    @ResponseBody
    public ServerResponse updateCategory(HttpSession session , String categoryName, Integer categoryId){
        ServerResponse serverResponse = iUserService.checkAdminRole(session);
        if(serverResponse.isSuccess()){
            return iCategoryService.updateCategory(categoryName,categoryId);
        }
        return  serverResponse;
    }

    @RequestMapping("get_category_name")
    @ResponseBody
    public  ServerResponse getChildrenParallelCategory(HttpSession session, @RequestParam(value = "parentId",defaultValue = "0") Integer parentId){
        ServerResponse serverResponse = iUserService.checkAdminRole(session);
        if(serverResponse.isSuccess()){
            return iCategoryService.selectChildrenParallelCategory(parentId);
        }
        return  serverResponse;
    }
    @RequestMapping("get_deep_category")
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenParallelCategory(HttpSession session, @RequestParam(value = "categoryId",defaultValue = "0") Integer categoryId){
        ServerResponse serverResponse = iUserService.checkAdminRole(session);
        if(serverResponse.isSuccess()){
            return iCategoryService.selectCategoryAndDeepChildrenById(categoryId);
        }
        return  serverResponse;
    }
}
