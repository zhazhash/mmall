package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Administrator on 2018/12/17.
 */
public interface IFileService {
    String upload(MultipartFile file , String path);
}
