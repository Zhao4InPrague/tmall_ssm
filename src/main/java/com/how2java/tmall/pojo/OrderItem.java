package com.how2java.tmall.pojo;

public class OrderItem {
    private Integer id;

    private Integer pid;

    private Integer oid;

    private Integer uid;

    private Integer number;

    /*非数据库字段*/
    //既然已经有了pid干嘛又要product？可能要放在对象里直接打到前端吧
    //好像主要是为了firstProductImage
    //应该是为了上面，不然的话用pid都可以去product的
    private Product product;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public Integer getOid() {
        return oid;
    }

    public void setOid(Integer oid) {
        this.oid = oid;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}