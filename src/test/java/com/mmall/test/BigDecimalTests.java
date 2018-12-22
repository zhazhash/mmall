package com.mmall.test;

import org.junit.Test;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2018/12/20.
 */
public class BigDecimalTests {

    @Test
    public void test(){
        BigDecimal b1 = new BigDecimal("1.2");
        BigDecimal b2 = new BigDecimal("2.2");
        System.out.print(b1.add(b2));
    }
}
