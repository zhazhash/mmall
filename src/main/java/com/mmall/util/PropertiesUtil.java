package com.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;


public class PropertiesUtil {

    private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    private static Properties pops;

    static {
        String fileName = "mmall.properties";
        pops = new Properties();
        try {
            pops.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName),"UTF-8"));
        } catch (IOException e) {
            logger.error("配置文件读取异常",e);
        }
    }
    public static String getProperty(String key){
       String value =  pops.getProperty(key);
       if(StringUtils.isBlank(value)){
           return  null;
       }
       return  value;
    }

    public  static String getProperty (String key , String defaultValue){
        String value = pops.getProperty(key,defaultValue);
        return value;
    }



}
