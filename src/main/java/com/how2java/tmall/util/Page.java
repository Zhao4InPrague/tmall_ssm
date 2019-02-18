package com.how2java.tmall.util;

public class Page {

    //这个到底在干什么，Page page是如何和和项目关联起来的呢?

    private int start;
    private int count;
    private int total;
    private String param;

    private static final int defaultCount = 5; //默认每页显示5条

    public int getStart() {
        return start;
    }
    public void setStart(int start) {
        this.start = start;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    //这俩page到底在干什么
    public Page(){
        count = defaultCount;
    }
    public Page(int start, int count){
        this();
        this.start = start;
        this.count = count;
    }

    public int getTotal() {
        return total;
    }
    public void setTotal(int total) {
        this.total = total;
    }
    public String getParam() {
        return param;
    }
    public void setParam(String param) {
        this.param = param;
    }

    public boolean isHasPreviouse() {
        if(start == 0) {
            return false;
        }
        return true;
    }
    public boolean isHasNext(){
        if(start == getLast()) {
            return false;
        }
        return true;
    }
    public int getTotalPage(){
        int totalPage;
        if(0 == total%count) {
            totalPage = total / count;
        } else {
            totalPage = total/count + 1;
        }

        if(0 == totalPage) {
            totalPage = 1;
        }
        return totalPage;
    }

    public int getLast(){
        int last;
        // 假设总数是50，是能够被5整除的，那么最后一页的开始就是45
        if(0 == total % count) {
            last = total - count;
        } else {
            // 假设总数是51，不能够被5整除的，那么最后一页的开始就是50
            last = total - total % count;
        }
        last = last < 0? 0: last;
        return last;
    }

    @Override
    public String toString() {
        return "Page [start=" + start + ", count=" + count + ", total=" + total + ", getStart()=" + getStart()
                + ", getCount()=" + getCount() + ", isHasPreviouse()=" + isHasPreviouse() + ", isHasNext()="
                + isHasNext() + ", getTotalPage()=" + getTotalPage() + ", getLast()=" + getLast() + "]";
    }

}
