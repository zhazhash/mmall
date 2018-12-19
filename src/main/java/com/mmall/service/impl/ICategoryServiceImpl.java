package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2018/11/26.
 */
@Service
public class ICategoryServiceImpl  implements ICategoryService{
    @Autowired
    private CategoryMapper categoryMapper;
    private Logger log = LoggerFactory.getLogger(ICategoryServiceImpl.class);
    /**
     * 添加品类信息
     * @param categoryName
     * @param parentId
     * @return
     */
    public ServerResponse addCategory(String categoryName , Integer parentId){
        if(parentId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("添加品类错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);
        int intCount = categoryMapper.insert(category);
        if(intCount > 0){
            return  ServerResponse.createBySuccessMessage("添加品类成功");
        }
        return  ServerResponse.createByErrorMessage("添加品类失败");

    }

    /**
     * 修改品类信息
     * @param categoryName 品类名称
     * @param categoryId 品类主键
     * @return
     */
    public ServerResponse updateCategory(String categoryName,Integer categoryId){
        if(categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("修改品类错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setId(categoryId);
        int intCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(intCount > 0){
            return ServerResponse.createBySuccessMessage("修改品类成功");
        }
        return ServerResponse.createByErrorMessage("修改品类失败");
    }

    /**
     * 获取分类信息 ，平级
     * @param parentId
     * @return
     */
    public  ServerResponse<List<Category>> selectChildrenParallelCategory(Integer parentId){
        if (parentId == null){
            return ServerResponse.createByErrorMessage("品类ID为空");
        }
        List<Category> list = categoryMapper.selectChildrenParallelCategory(parentId);
        if(CollectionUtils.isEmpty(list)){
            log.info("未找到当前分类的子分类");
        }
        return  ServerResponse.createBySuccess(list);
    }

    /**
     * 获取分类信息 ，包括子节点
     * @param parentId
     * @return
     */
    public ServerResponse selectCategoryAndDeepChildrenById(Integer parentId){
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categorySet,parentId);
        List<Integer> categoryIdList = Lists.newArrayList();
        if(categorySet != null){
            for(Category categoryItem : categorySet){
                    categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }

    /**
     * 递归获取分类信息
     * @param categorySet
     * @param categoryId
     * @return
     */
    private Set<Category> findChildCategory(Set<Category> categorySet , Integer categoryId){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if(category != null){
                categorySet.add(category);
            }
            List<Category> categoryList = categoryMapper.selectChildrenParallelCategory(categoryId);
            for(Category categoryItem : categoryList){
                findChildCategory(categorySet,categoryItem.getId());
            }
        return  categorySet;
    }


}
