package com.how2java.tmall.pojo;

import java.util.List;

public class Category {
    private Integer id;

    private String name;

    //add product
    private List<Product> products;
    private List<List<Product>> productsByRow;

    public void setProducts(List<Product> ps){
        this.products = ps;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProductsByRow(List<List<Product>> productsByRow){
        this.productsByRow = productsByRow;
    }

    public List<List<Product>> getProductsByRow() {
        return productsByRow;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
}