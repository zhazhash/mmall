package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Administrator on 2018/12/17.
 */
@Service("iFileService")
public class FileServiceImpl  implements IFileService {

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);


    public String upload(MultipartFile file , String path){
        String fileName = file.getOriginalFilename();
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtensionName;
        logger.info("开始上传文件，上传文件名：{}，上传路径名：{}，新文件名：{}。",fileName,path,uploadFileName);

        File fileDir = new File(path);
        if(!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File  targetFile  = new File(path,uploadFileName);

        try {
            file.transferTo(targetFile); //上传文件
            FTPUtil.uploadFile(Lists.newArrayList(targetFile)); //上传至ftp
            targetFile.delete(); //删除文件

        } catch (IOException e) {
            logger.error("文件上传失败，失败原因：" ,e);
            return null;
        }
    return  targetFile.getName();
    }

}
