package com.how2java.tmall.service.impl;

import com.how2java.tmall.mapper.ProductMapper;
import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.ProductExample;
import com.how2java.tmall.pojo.ProductImage;
import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.service.ProductImageService;
import com.how2java.tmall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    ProductMapper productMapper;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductImageService productImageService;

    @Override
    public void add(Product product) {
        productMapper.insert(product);
    }

    @Override
    public void delete(int id) {
        productMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void update(Product product) {
        productMapper.updateByPrimaryKeySelective(product);
    }

    @Override
    public Product get(int id) {
        Product product = productMapper.selectByPrimaryKey(id);
        setFirstProductImage(product);
        setCategory(product);
        return product;
    }

    //property是在controller里update里setCategory，可是这里在impl里就set了，为什么不一样呢？还是说两种都可以呢？
    //product 在controller里也setCategory了...
    public void setCategory(Product product) {
        int cid = product.getCid();
        Category category = categoryService.get(cid);
        product.setCategory(category);
    }

    public void setCategory(List<Product> products) {
        for(Product product: products) {
            setCategory(product);
        }
    }

    @Override
    public List list(int id) {
        ProductExample example = new ProductExample();
        example.createCriteria().andCidEqualTo(id);
        example.setOrderByClause("id desc");
        List result = productMapper.selectByExample(example);
        setCategory(result);
        setFirstProductImage(result);
        return result;
    }

    @Override
    public void setFirstProductImage(Product p) {
        List<ProductImage> productImages = productImageService.list(p.getId(), ProductImageService.type_single);
        if(!productImages.isEmpty()) {
            ProductImage productImage = productImages.get(0);
            p.setFirstProductImage(productImage);
        }
    }

    public void setFirstProductImage(List<Product> products) {
        for (Product product: products) {
            setFirstProductImage(product);
        }
    }
}
