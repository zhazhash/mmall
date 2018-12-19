package com.mmall.controller.admin;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by Administrator on 2018/11/28.
 */
@Controller
@RequestMapping("/admin/product")
public class ProductManageController {

    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;

    @RequestMapping("save")
    @ResponseBody
    public ServerResponse productSave(HttpSession session , Product product){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return  ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请登录");
        }
        if(user != null && user.getRole().intValue()  == Const.Role.ROLE_ADMIN ){
            final ServerResponse serverResponse = iProductService.saveOrUpdateProduct(product);
            return serverResponse;
        }
        return  ServerResponse.createByErrorMessage("您无权此操作");
    }

    @RequestMapping("set_sale_status")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session ,Integer  productId,Integer status){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return iProductService.setSaleStatus(productId,status);
        }
        return ServerResponse.createByErrorMessage("您无权此操作");
    }

    @RequestMapping("detail")
    @ResponseBody
    public ServerResponse getDetail(HttpSession session ,Integer  productId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return iProductService.manageProductDetail(productId);
        }
        return ServerResponse.createByErrorMessage("您无权此操作");
    }


    @RequestMapping("list")
    @ResponseBody
    public ServerResponse getList(HttpSession session , @RequestParam(value = "pageNum" , defaultValue = "1") Integer  pageNum, @RequestParam(value = "pageSize" , defaultValue = "10")Integer pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return iProductService.getProductList(pageNum,pageSize);
        }
        return ServerResponse.createByErrorMessage("您无权此操作");
    }

    @RequestMapping("search")
    @ResponseBody
    public ServerResponse productSearch(HttpSession session ,String productName , Integer productId, @RequestParam(value = "pageNum" , defaultValue = "1") Integer  pageNum, @RequestParam(value = "pageSize" , defaultValue = "10")Integer pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            return iProductService.selectProductSearch(productName,productId,pageNum,pageSize);
        }
        return ServerResponse.createByErrorMessage("您无权此操作");
    }
    @RequestMapping("upload")
    @ResponseBody
    public ServerResponse upload(HttpSession session ,@RequestParam(value = "upload_file" , required = false) MultipartFile file, HttpServletRequest request){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            Map fileMap = Maps.newHashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);
            return ServerResponse.createBySuccess(fileMap);
        }
        return ServerResponse.createByErrorMessage("您无权此操作");
    }

    /**
     * 富文本上传  ，使用simditor插件上传，按照要求返回格式
     * @param session
     * @param file
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("richtext_img_upload")
    @ResponseBody
    public Map richtextImgUpload(HttpSession session , @RequestParam(value = "upload_file" , required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response){
        Map resultMap = Maps.newHashMap();

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            resultMap.put("success",false);
            resultMap.put("msg","请登录管理员账号");
            return resultMap;
        }
        if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN){
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);

            if(StringUtils.isBlank(targetFileName)){
                resultMap.put("success",false);
                resultMap.put("msg","上传失败");
                return resultMap;
            }

            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path",url);

            response.addHeader("Access_Control-Allow-Headers","X-File-Name");
            return resultMap;
        }
        resultMap.put("success",false);
        resultMap.put("msg","权限不足");
        return resultMap;
    }
}
