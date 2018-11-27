package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Category;

import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2018/11/26.
 */
public interface ICategoryService {
    ServerResponse addCategory(String categoryName , Integer parentId);
    ServerResponse updateCategory(String categoryName,Integer categoryId);
    ServerResponse<List<Category>> selectChildrenParallelCategory(Integer parentId);
    ServerResponse selectCategoryAndDeepChildrenById(Integer parentId);
}
