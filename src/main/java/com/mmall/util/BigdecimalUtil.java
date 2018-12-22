package com.mmall.util;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2018/12/20.
 */
public class BigdecimalUtil {
    private BigdecimalUtil() {

    }

    /**
     * 加
     * @param d1
     * @param d2
     * @return
     */
    public static BigDecimal add(double d1, double d2) {
        BigDecimal b1 = new BigDecimal(Double.toString(d1));
        BigDecimal b2 = new BigDecimal(Double.toString(d2));
        return b1.add(b2);
    }
    /**
     * 减
     * @param d1
     * @param d2
     * @return
     */
    public static BigDecimal sub(double d1, double d2) {
        BigDecimal b1 = new BigDecimal(Double.toString(d1));
        BigDecimal b2 = new BigDecimal(Double.toString(d2));
        return b1.subtract(b2);
    }
    /**
     * 乘
     * @param d1
     * @param d2
     * @return
     */
    public static BigDecimal mul(double d1, double d2) {
        BigDecimal b1 = new BigDecimal(Double.toString(d1));
        BigDecimal b2 = new BigDecimal(Double.toString(d2));
        return b1.multiply(b2);
    }
    /**
     * 除
     * @param d1
     * @param d2
     * @return
     */
    public static BigDecimal div(double d1, double d2) {
        BigDecimal b1 = new BigDecimal(Double.toString(d1));
        BigDecimal b2 = new BigDecimal(Double.toString(d2));
        return b1.divide(b2,2,BigDecimal.ROUND_HALF_UP);//四舍五入 保留两位小数
    }
}
