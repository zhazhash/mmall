package com.mmall.controller.portal;

import com.mmall.common.ServerResponse;
import com.mmall.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2018/12/17.
 */
@Controller
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    private IProductService iProductService;

    @RequestMapping("detail")
    @ResponseBody
    public ServerResponse detail(Integer productId){
        return  iProductService.getProductDetail(productId);
    }
    @RequestMapping("list")
    @ResponseBody
    public ServerResponse list(@RequestParam(value = "keyword" , required = false)String keyword,@RequestParam(value = "categoryId" , required = false)Integer categoryId,@RequestParam(value = "pageNum" ,defaultValue = "1") int pageNum , @RequestParam(value = "pageSize" ,defaultValue = "10")int pageSize){
        return  iProductService.getProductByKeywordCategory(keyword,categoryId,pageNum,pageSize);
    }
}
