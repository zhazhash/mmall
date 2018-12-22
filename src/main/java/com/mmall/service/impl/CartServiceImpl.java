package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.common.Const;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2018/12/20.
 */
@Service("iCartService")
public class CartServiceImpl  implements ICartService{
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    /**
     * 加入购物车功能
     * @param productId
     * @param userId
     * @param count
     * @return
     */
    @Override
    public ServerResponse add(Integer productId, Integer userId, Integer count) {
      Cart cart = cartMapper.selectCartByUserIdProductId(productId,userId); //查看商品是否为空
      if(cart == null ){
          Cart cartItem = new Cart();
          cartItem.setQuantity(count);
          cartItem.setUserId(userId);
          cartItem.setProductId(productId);
          cartItem.setChecked(Const.Cart.CHECKED);
          cartMapper.insert(cartItem);
      }else {
          count = cart.getQuantity() + count;
          cart.setQuantity(count);
          cartMapper.updateByPrimaryKeySelective(cart);
      }
        CartVo cartVo = this.getCartLimit(userId);

        return ServerResponse.createBySuccess(cartVo);
    }

    private CartVo getCartLimit(Integer userId){
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        BigDecimal cartTotalPrice = new BigDecimal("0");
        if(CollectionUtils.isNotEmpty(cartList)){
            for(Cart cart : cartList){
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setUserId(cart.getUserId());
                cartProductVo.setId(cart.getId());
                cartProductVo.setProductId(cart.getProductId());

                Product product  = productMapper.selectByPrimaryKey(cart.getProductId());
                if(product != null){
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductSubTitle(product.getSubtitle());
                    cartProductVo.setProductStock(product.getStock());
                    cartProductVo.setProductStatus(product.getStatus());

                    //判断库存
                    int buyLimitCount = 0;
                    if(product.getStock() >= cart.getQuantity()){
                        buyLimitCount = cart.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else{
                        buyLimitCount = product.getStock();
                        Cart cartQuantity = new Cart();
                        cartQuantity.setId(cart.getId());
                        cartQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算商品总价
                    cartProductVo.setProductPrice(BigdecimalUtil.mul(product.getPrice().doubleValue(),buyLimitCount));
                    cartProductVo.setProductChecked(cart.getChecked());
                    if(cart.getChecked() == Const.Cart.CHECKED){
                        cartTotalPrice = BigdecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductPrice().doubleValue());
                    }
                    cartProductVoList.add(cartProductVo);
                }
            }
            cartVo.setCartTotalPrice(cartTotalPrice);
            cartVo.setCartProductVoList(cartProductVoList);
            cartVo.setAllChecked(getAllStatus(userId));
            cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        }

        return cartVo;
    }


    private boolean getAllStatus(Integer userId){
        if(userId == null){
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }
}
