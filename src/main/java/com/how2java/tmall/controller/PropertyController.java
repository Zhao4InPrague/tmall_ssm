package com.how2java.tmall.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Property;
import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.service.PropertyService;
import com.how2java.tmall.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("")
public class PropertyController {

    @Autowired
    CategoryService categoryService;
    @Autowired
    PropertyService propertyService;

    @RequestMapping("admin_property_add")
    public String add(Model model, Property property) {
        propertyService.add(property);
        return "redirect:admin_property_list?cid="+ property.getCid();
    }

    @RequestMapping("admin_property_delete")
    public String delete(Model model, int id) {
        Property p = propertyService.get(id);
        propertyService.delete(id);
        return "redirect:admin_property_list?cid="+p.getCid();
    }

    @RequestMapping("admin_property_edit")
    public String edit(Model model, int pid){
        //这个只是取出property并且填充非sql关联的category
        Property property = propertyService.get(pid);
        Category category = categoryService.get(property.getCid());
        property.setCategory(category);
        model.addAttribute("p", property);
        return "admin/editProperty";
    }

    @RequestMapping("admin_property_update")
    public String update(Property property){
        propertyService.update(property);
        return "redirect:admin_property_list?cid="+property.getCid();
    }

    @RequestMapping("admin_property_list")
    public String list(int cid, Model model, Page page) {
        //1. 获取分类 cid,和分页对象page
        Category category = categoryService.get(cid);
        //2.通过PageHelper设置分页参数
        PageHelper.offsetPage(page.getStart(), page.getCount());
        //3.基于cid，获取当前分类下的属性集合
        List<Property> properties = propertyService.list(cid);
        //4.通过PageInfo获取属性总数
        int total = (int)new PageInfo<>(properties).getTotal();
        //5.把总数设置给分页page对象
        page.setTotal(total);
        //6. 拼接字符串"&cid="+c.getId()，设置给page对象的Param值。 因为属性分页都是基于当前分类下的分页，所以分页的时候需要传递这个cid
        page.setParam("&cid="+category.getId());

        model.addAttribute("ps", properties);
        // 把分类对象设置到 request的 "c" 属性上。 ( 这个c有什么用呢？ 在后面步骤的 其他-面包屑导航 中会用于显示分类名称)
        model.addAttribute("c", category);
        model.addAttribute("page", page);
        return "admin/listProperty";
    }
}
