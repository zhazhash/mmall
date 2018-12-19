package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtils;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2018/11/28.
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService{

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;
    /**
     * 添加或修改商品
     * @param product
     * @return
     */
    public ServerResponse saveOrUpdateProduct(Product product){
        if(product != null){
            if (StringUtils.isNotBlank(product.getSubImages()) ){
                String [] subImgArray  = product.getSubImages().split(",");
                if(subImgArray.length >0){
                    product.setMainImage(subImgArray[0]);
                }
                if(product.getId() != null){
                    int intCount = productMapper.updateByPrimaryKey(product);
                    if(intCount > 0){
                        return  ServerResponse.createBySuccessMessage("修改商品成功");
                    }
                    return  ServerResponse.createByErrorMessage("修改商品失败");
                }else {
                    int intCount = productMapper.insert(product);
                    if(intCount > 0 ){
                        return ServerResponse.createBySuccessMessage("添加商品成功");
                    }
                    return ServerResponse.createByErrorMessage("添加商品失败");
                }
            }
        }
        return ServerResponse.createByErrorMessage("商品为空");
    }

    /**
     * 更新商品状态
     * @param productId
     * @param status
     * @return
     */
    public  ServerResponse<String> setSaleStatus (Integer  productId,Integer status){
        if(productId == null || status == null){
            return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int intCount = productMapper.updateByPrimaryKeySelective(product);
        if(intCount > 0){
            return  ServerResponse.createBySuccessMessage("更新商品状态成功");
        }
        return ServerResponse.createByErrorMessage("更新商品状态失败");
    }

    /**
     * 获取商品详情
     * @param productId 商品id
     * @return
     */
    public ServerResponse manageProductDetail(Integer productId){
        if (productId == null){
            return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product =  productMapper.selectByPrimaryKey(productId);
        if (product == null){
            return ServerResponse.createByErrorMessage("无此商品或已下架");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    /**
     * 封装前台使用的vo
     * @param product
     * @return
     */
    private ProductDetailVo assembleProductDetailVo(Product product) {
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setName(product.getName());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setSubTitle(product.getSubtitle());
        productDetailVo.setStock(product.getStock());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","localhost"));
        productDetailVo.setCreateTime(DateTimeUtils.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtils.dateToStr(product.getUpdateTime()));
        return  productDetailVo;
    }

    /**
     * 获取商品列表
     * @param pageNum
     * @param pageSize
     * @return
     */
    public  ServerResponse getProductList (Integer pageNum , Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);//开启pageHelper分页
        List<Product> productList = productMapper.selectList();
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product productItem : productList){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);

        return ServerResponse.createBySuccess(pageResult);
    }

    /**
     * 封装前台使用列表
     * @param productItem
     * @return
     */
    private ProductListVo assembleProductListVo(Product productItem) {
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(productItem.getId());
        productListVo.setCategoryId(productItem.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","localhost"));
        productListVo.setName(productItem.getName());
        productListVo.setStatus(productItem.getStatus());
        productListVo.setSubtitle(productItem.getSubtitle());
        productListVo.setPrice(productItem.getPrice());
        productListVo.setMainImage(productItem.getMainImage());
        return  productListVo;
    }

    public ServerResponse selectProductSearch(String productName, Integer productId, Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        if(StringUtils.isNotBlank(productName)){
            productName = new StringBuffer("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.selectByNameAndProductId(productName,productId);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product productItem : productList){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);

        return ServerResponse.createBySuccess(pageResult);
    }


    /**
     * 前台获取商品详情
     * @param productId 商品id
     * @return
     */
    public ServerResponse getProductDetail(Integer productId){
        if (productId == null){
            return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product =  productMapper.selectByPrimaryKey(productId);
        if (product == null){
            return ServerResponse.createByErrorMessage("无此商品或已下架");
        }
        if(product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("无此商品或已下架");
        }
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    /**
     * 商品列表
     * @param keyword
     * @param categoryId
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    @Override
    public ServerResponse getProductByKeywordCategory(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy) {
        List<Integer> categoryIdList = Lists.newArrayList();
        if(StringUtils.isBlank(keyword) && categoryId == null){
            ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        if(categoryId != null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if(category == null && StringUtils.isBlank(keyword)){
                PageHelper.startPage(pageNum,pageSize);
                List<ProductDetailVo> productDetailVos = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productDetailVos);
                ServerResponse.createBySuccess(pageInfo);
            }
            categoryIdList = (List<Integer>) iCategoryService.selectCategoryAndDeepChildrenById(category.getId()).getData();
        }
        if(StringUtils.isNotBlank(keyword)){
            keyword = new StringBuffer("%").append(keyword).append("%").toString();
        }
        PageHelper.startPage(pageNum,pageSize);
        if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
             PageHelper.orderBy(orderBy.replace("_"," "));
        }
        List<Product> productList = productMapper.selectByNameAndCagegoryIds(StringUtils.isBlank(keyword)? null :keyword,categoryIdList.size() == 0 ? null:categoryIdList);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product product : productList){
            ProductListVo  productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }


}
