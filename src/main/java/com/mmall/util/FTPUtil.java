package com.mmall.util;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2018/12/17.
 */
public class FTPUtil {
    private static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

    private static  String FTPIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static  String FTPUser = PropertiesUtil.getProperty("ftp.user");
    private static  String FTPPass = PropertiesUtil.getProperty("ftp.pass");

    private String ip;
    private int port;
    private String user;
    private String password;
    private FTPClient FTPClient;


    public FTPUtil(String ip, int port, String user, String password) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.password = password;
    }


    public  static boolean uploadFile(List<File> fileList) throws IOException {
        FTPUtil ftpUtil = new FTPUtil(FTPIp,21,FTPUser,FTPPass);
        logger.info("开始链接ftp服务器");
        boolean result = ftpUtil.uploadFile("img",fileList);
        logger.info("结束上传，上传结果：{}",result);
        return result;
    }

    private  boolean uploadFile(String remotePath,List<File> fileList) throws IOException {
        boolean uploaded = true;
        FileInputStream fis = null;
        if(connectServer(this.ip,this.port,this.user,this.password)){
            try {
                FTPClient.changeWorkingDirectory(remotePath); //更改工作目录
                FTPClient.setBufferSize(1024);
                FTPClient.setControlEncoding("UTF-8");
                FTPClient.setFileType(FTP.BINARY_FILE_TYPE);//上传类型为2进制
                FTPClient.enterLocalPassiveMode(); //打开本地被动模式
                for(File fileItem : fileList){
                    fis = new FileInputStream(fileItem);
                    FTPClient.storeFile(fileItem.getName(),fis); //上传
                }
            } catch (IOException e) {
                logger.error("上传文件异常",e);
                uploaded = false;
            } finally {
                fis.close();
                FTPClient.disconnect();
            }
        }
    return uploaded;
    }
    private boolean connectServer(String ip ,int port ,String user,String password){
        boolean isSuccess = false;
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip,port);
            isSuccess =  ftpClient.login(user,password);
        } catch (IOException e) {
            logger.error("连接FTP服务器异常",e);
        }
        return isSuccess;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public org.apache.commons.net.ftp.FTPClient getFTPClient() {
        return FTPClient;
    }

    public void setFTPClient(org.apache.commons.net.ftp.FTPClient FTPClient) {
        this.FTPClient = FTPClient;
    }



}
