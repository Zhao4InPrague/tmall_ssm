package com.how2java.tmall.controller;

import com.github.pagehelper.PageHelper;
import com.how2java.tmall.comparator.*;
import com.how2java.tmall.pojo.*;
import com.how2java.tmall.service.*;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Collections;

@Controller
@RequestMapping("")
public class ForeController {

    @Autowired
    UserService userService;
    @Autowired
    ProductService productService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    OrderService orderService;

    @RequestMapping("foreregister")
    public String register(Model model, User user) {
        String name = user.getName();
        name = HtmlUtils.htmlEscape(name);
        user.setName(name);
        boolean exist = userService.isExist(name);

        if(exist){
            String msg = "名字已经注册了!";
            model.addAttribute("msg", msg);
            model.addAttribute("user", null);
            return "fore/register";
        }

        userService.add(user);
        return "redirect:registerSuccessPage";

    }

    @RequestMapping("forelogin")
    public String login(@RequestParam("name") String name, @RequestParam("password") String password, Model model, HttpSession session) {
        name = HtmlUtils.htmlEscape(name);
        User user = userService.get(name, password);

        if(user == null) {
            model.addAttribute("msg", "账号密码错误");
            return "fore/login";
        }
        session.setAttribute("user", user);
        return "redirect:forehome";
    }

    @RequestMapping("forelogout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:forehome";
    }

    @RequestMapping("foreproduct")
    public String product(int pid, Model model) {
        Product p = productService.get(pid);
        List<ProductImage> productSingleImages = productImageService.list(pid, ProductImageService.type_single);
        List<ProductImage> productDetailImages = productImageService.list(pid, ProductImageService.type_detail);
        p.setProductSingleImages(productSingleImages);
        p.setProductDetailImages(productDetailImages);
        List<PropertyValue> pvs = propertyValueService.list(pid);
        List<Review> reviews = reviewService.list(pid);
        productService.setSaleAndReviewNumber(p);
        model.addAttribute("reviews", reviews);
        model.addAttribute("p", p);
        model.addAttribute("pvs", pvs);
        return "fore/product";
    }

    @RequestMapping("forecheckLogin")
    @ResponseBody//估计就是返回不是链接字符串，而是body了所以写成这个样子
    public String checkLogin(HttpSession session) {
        User user = (User)session.getAttribute("user");
        if(null == user) {
            return "fail";
        }
        return "success";
    }

    @RequestMapping("foreloginAjax")
    @ResponseBody
    public String loginAjax(@RequestParam("name") String name, @RequestParam("password") String password,HttpSession session) {
        name = HtmlUtils.htmlEscape(name);
        User user = userService.get(name, password);
        if(null == user) {
            return "fail";
        }
        session.setAttribute("user", user);
        return "success";
    }

    @RequestMapping("forecategory")
    public String category(int cid, String sort, Model model){
        Category c = categoryService.get(cid);
        productService.fill(c);
        productService.setSaleAndReviewNumber(c.getProducts());
        if(null!=sort){
            switch(sort){
                case "review":
                    Collections.sort(c.getProducts(),new ProductReviewComparator());
                    break;
                case "date" :
                    Collections.sort(c.getProducts(),new ProductDateComparator());
                    break;

                case "saleCount" :
                    Collections.sort(c.getProducts(),new ProductSaleCountComparator());
                    break;

                case "price":
                    Collections.sort(c.getProducts(),new ProductPriceComparator());
                    break;

                case "all":
                    Collections.sort(c.getProducts(),new ProductAllComparator());
                    break;
            }
        }
        model.addAttribute("c", c);
        return "fore/category";
    }

    @RequestMapping("foresearch")
    public String search(String keyword, Model model) {
        PageHelper.offsetPage(0, 20);
        List<Product> ps = productService.search(keyword);
        productService.setSaleAndReviewNumber(ps);
        model.addAttribute("ps", ps);
        return "fore/searchResult";
    }

    @RequestMapping("forebuyone")
    public String buyone(int pid, int num, HttpSession session) {
        Product p = productService.get(pid);
        User u = (User) session.getAttribute("user");
        int oiid = 0;
        boolean found = false;

        List<OrderItem> ois = orderItemService.listByUser(u.getId());
        for(OrderItem oi: ois) {
            if(oi.getProduct().getId().intValue() == p.getId().intValue()){
                oi.setNumber(oi.getNumber()+num);
                orderItemService.update(oi);
                oiid = oi.getId();
                found = true;
                break;
            }
        }
        if(!found) {
            OrderItem oi = new OrderItem();
            //为啥这个时候不setProduct
            oi.setNumber(num);
            oi.setPid(pid);
            oi.setUid(u.getId());
            orderItemService.add(oi);
            oiid = oi.getId();
        }
        return "redirect:forebuy?oiid="+oiid;
    }

    @RequestMapping("forebuy")
    public String buy(String[] oiid ,Model model, HttpSession session){
        List<OrderItem> ois = new ArrayList<>();
        float total = 0;
        for(String strid: oiid) {
            int id = Integer.parseInt(strid);
            OrderItem oi = orderItemService.get(id);
            total += oi.getProduct().getPromotePrice()*oi.getNumber();
            ois.add(oi);
        }
        session.setAttribute("ois", ois);
        model.addAttribute("total", total);
        return "fore/buy";
    }

    @RequestMapping("foreaddCart")
    @ResponseBody
    public String addCart(int pid, int num, Model model,HttpSession session) {
        Product p = productService.get(pid);
        User user =(User)  session.getAttribute("user");
        boolean found = false;

        List<OrderItem> ois = orderItemService.listByUser(user.getId());
        for (OrderItem oi : ois) {
            if(oi.getProduct().getId().intValue()==p.getId().intValue()){
                oi.setNumber(oi.getNumber()+num);
                orderItemService.update(oi);
                found = true;
                break;
            }
        }

        if(!found){
            OrderItem oi = new OrderItem();
            oi.setUid(user.getId());
            oi.setNumber(num);
            oi.setPid(pid);
            orderItemService.add(oi);
        }
        return "success";
    }

    @RequestMapping("forecart")
    public String cart(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<OrderItem> ois = orderItemService.listByUser(user.getId());
        model.addAttribute("ois", ois);
        return "fore/cart";
    }

    @RequestMapping("forechangeOrderItem")
    @ResponseBody
    public String changeOrderItem(Model model, HttpSession session, int pid, int number) {
        User user = (User) session.getAttribute("user");
        if(user == null) {
            return "fail";
        }
        List<OrderItem> ois = orderItemService.listByUser(user.getId());
        for(OrderItem oi: ois) {
            if(oi.getProduct().getId().intValue() == pid) {
                oi.setNumber(number);
                orderItemService.update(oi);
                break;
            }
        }
        return "success";
    }

    @RequestMapping("foredeleteOrderItem")
    @ResponseBody
    public String deleteOrderItem(Model model, HttpSession session, int oiid) {
        User user = (User) session.getAttribute("user");
        if(user == null) {
            return "fail";
        }
        orderItemService.delete(oiid);
        return "success";
    }

    @RequestMapping("forecreateOrder")
    public String createOrder(Model model, Order o, HttpSession session) {
        //像是setUser，setProducts这些没有关联数据库的对象数据，都不会在这种情况下set的，只会在要用的时候
        //比如有了uid，那么用UserService.get(uid)取出，用的时候再set到对象里
        User user = (User) session.getAttribute("user");
        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + RandomUtils.nextInt(10000);
        o.setOrderCode(orderCode);
        o.setDeliveryDate(new Date());
        o.setUid(user.getId());
        o.setStatus(OrderService.waitPay);
        List<OrderItem> ois = (List<OrderItem>) session.getAttribute("ois");
        float total = orderService.add(o, ois);
        return "redirect:forealipay?oid="+o.getId() +"&total="+total;
    }

    @RequestMapping("forepayed")
    public String payed(int oid, float total, Model model) {
        //这个就是简单的更新订单状态就好了
        Order order = orderService.get(oid);
        order.setStatus(OrderService.waitDelivery);
        order.setPayDate(new Date());
        orderService.update(order);
        model.addAttribute("o", order);
        return "fore/payed";
    }

    @RequestMapping("forebought")
    public String bought(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        List<Order> os = orderService.list(user.getId(), OrderService.delete);
        orderItemService.fill(os);
        model.addAttribute("os", os);
        return "fore/bought";
    }

    @RequestMapping("foreconfirmPay")
    public String comfirmPay(Model model, int oid) {
        Order o = orderService.get(oid);
        orderItemService.fill(o);
        model.addAttribute("o", o);
        return "fore/confirmPay";
    }

    @RequestMapping("foreorderConfirmed")
    public String orderConfirmed(Model model, int oid) {
        Order o = orderService.get(oid);
        o.setStatus(OrderService.waitReview);
        o.setConfirmDate(new Date());
        orderService.update(o);
        return "fore/orderConfirmed";
    }

    @RequestMapping("foredeleteOrder")
    @ResponseBody
    public String deleteOrder(Model model, int oid) {
        Order o = orderService.get(oid);
        o.setStatus(OrderService.delete);
        orderService.update(o);
        return "success";
    }

    @RequestMapping("forereview")
    public String review(Model model, int oid) {
        Order order = orderService.get(oid);
        orderItemService.fill(order);
        Product p = order.getOrderItems().get(0).getProduct();
        List<Review> reviews = reviewService.list(p.getId());
        productService.setSaleAndReviewNumber(p);
        model.addAttribute("p",p);
        model.addAttribute("o", order);
        model.addAttribute("reviews", reviews);
        return "fore/review";
    }

    @RequestMapping("foredoreview")
    public String doreview(Model model, HttpSession session, @RequestParam("oid") int oid, @RequestParam("pid") int pid, String content) {
        Order o = orderService.get(oid);
        o.setStatus(OrderService.finish);
        orderService.update(o);

        Product p = productService.get(pid);
        content = HtmlUtils.htmlEscape(content);

        User user = (User) session.getAttribute("user");
        Review review = new Review();
        review.setContent(content);
        review.setPid(pid);
        review.setCreateDate(new Date());
        review.setUid(user.getId());
        reviewService.add(review);
        return "redirect:forereview?oid="+oid+"&showonly=true";
    }

}
