package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;

/**
 * Created by Administrator on 2018/11/28.
 */
public interface IProductService {
    ServerResponse saveOrUpdateProduct(Product product);
    ServerResponse<String> setSaleStatus (Integer  productId,Integer status);
    ServerResponse manageProductDetail(Integer productId);
    ServerResponse getProductList (Integer pageNum , Integer pageSize);

    ServerResponse selectProductSearch(String productName, Integer productId, Integer pageNum, Integer pageSize);
    ServerResponse getProductDetail(Integer productId);

    ServerResponse getProductByKeywordCategory(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy);
}
