package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigdecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2018/12/20.
 */
@Service("iCartService")
public class CartServiceImpl implements ICartService {
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    /**
     * 加入购物车功能
     *
     * @param productId
     * @param userId
     * @param count
     * @return
     */
    @Override
    public ServerResponse add(Integer productId, Integer userId, Integer count) {
        if (productId == null || count == null) {
            return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIdProductId(productId, userId); //查看商品是否为空
        if (cart == null) {
            Product product = productMapper.selectByPrimaryKey(productId);
            if(product != null){
                Cart cartItem = new Cart();
                cartItem.setQuantity(count);
                cartItem.setUserId(userId);
                cartItem.setProductId(productId);
                cartItem.setChecked(Const.Cart.CHECKED);
                cartMapper.insert(cartItem);
            }
            return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "无此商品");
        } else {
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }

    /**
     * 更新购物车
     *
     * @param productId
     * @param userId
     * @param count
     * @return
     */
    @Override
    public ServerResponse update(Integer productId, Integer userId, Integer count) {
        if (productId == null || count == null) {
            return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIdProductId(productId, userId);
        if (cart != null) {
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }

    /**
     * 删除购物车商品功能
     *
     * @param productIds
     * @param userId
     * @return
     */
    @Override
    public ServerResponse deleteProduct(String productIds, Integer userId) {
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if (CollectionUtils.isEmpty(productList)) {
            return ServerResponse.createByErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteByUserIdAndProductIds(userId, productList);
        return this.list(userId);
    }

    @Override
    public ServerResponse list(Integer userId) {
        CartVo cartVo = this.getCartLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    /**
     * 全选 ，全不选，单选，单反选
     *
     * @param userId
     * @param productId
     * @param checked
     * @return
     */
    @Override
    public ServerResponse selectOrUnSelect(Integer userId, Integer productId, int checked) {
        cartMapper.selectOrUnselect(userId, productId, checked);
        return this.list(userId);
    }

    /**
     * 查看商品数量
     *
     * @param userId
     * @return
     */
    @Override
    public ServerResponse selectCartProductCount(Integer userId) {
        if (userId == null) {
            return ServerResponse.createBySuccess("0");
        }
        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }

    /**
     * 购物车核心类，查询某人的购物车内所有商品
     *
     * @param userId
     * @return
     */
    private CartVo getCartLimit(Integer userId) {
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId); //通过userid获取购物车列表
        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        BigDecimal cartTotalPrice = new BigDecimal("0");//购物车总价
        if (CollectionUtils.isNotEmpty(cartList)) {
            for (Cart cart : cartList) {
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setUserId(cart.getUserId());
                cartProductVo.setId(cart.getId());
                cartProductVo.setProductId(cart.getProductId());

                Product product = productMapper.selectByPrimaryKey(cart.getProductId());
                if (product != null) {
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductSubTitle(product.getSubtitle());
                    cartProductVo.setProductStock(product.getStock());
                    cartProductVo.setProductStatus(product.getStatus());

                    //判断库存
                    int buyLimitCount = 0;
                    if (product.getStock() >= cart.getQuantity()) {
                        buyLimitCount = cart.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    } else {
                        buyLimitCount = product.getStock();
                        Cart cartQuantity = new Cart();
                        cartQuantity.setId(cart.getId());
                        cartQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算商品总价
                    cartProductVo.setProductPrice(BigdecimalUtil.mul(product.getPrice().doubleValue(), buyLimitCount));
                    cartProductVo.setProductChecked(cart.getChecked());
                    if (cart.getChecked() == Const.Cart.CHECKED) {
                        cartTotalPrice = BigdecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductPrice().doubleValue());
                    }
                    cartProductVoList.add(cartProductVo);
                }
            }
            cartVo.setCartTotalPrice(cartTotalPrice);
            cartVo.setCartProductVoList(cartProductVoList); //购物车
            cartVo.setAllChecked(getAllStatus(userId));
            cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        }

        return cartVo;
    }


    private boolean getAllStatus(Integer userId) {
        if (userId == null) {
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }
}
