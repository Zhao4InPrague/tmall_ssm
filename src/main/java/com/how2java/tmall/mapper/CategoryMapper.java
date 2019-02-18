package com.how2java.tmall.mapper;

import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.util.Page;

import java.util.List;

public interface CategoryMapper {

    //这里的page也没有初始化呀start和count这些信息，咋填进去的？admin_category_list?start=5这样的get，咋和page对象勾连的？？？
    List<Category> list(Page page);

    int total();

    void add(Category category);

    void delete(int id);

    Category get(int id);

    void update(Category category);

}
