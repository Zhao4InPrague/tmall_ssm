package com.how2java.tmall.service;

import com.how2java.tmall.pojo.Order;
import com.how2java.tmall.pojo.OrderItem;

import java.util.List;

public interface OrderItemService {

    void add(OrderItem c);

    void delete(int id);

    void update(OrderItem c);

    OrderItem get(int id);

    List list();

    //这fill是真恶心,为啥要给order填充呢，自己身上没有么
    void fill(List<Order> os);

    void fill(Order o);

    int getSaleCount(int pid);

}
