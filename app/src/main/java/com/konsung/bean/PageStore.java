package com.konsung.bean;

import java.io.Serializable;

/**
 * 分页
 * @author HEXM
 */
public class PageStore implements Serializable {

    private static final long serialVersionUID = -8156769932279879407L;
    public static final int DEFAULT_PAGE_BAR_STYLE = 3;
    //数据库中符合条件的数据总数
    private int maxSize;
    //当前页面的页数
    private int thisPage = 1;
    //总页数
    private int pageCount;
    //html超链接
    private String pageBar;
    private String url;
    private int length;
    //每页的记录数
    private int size = 20;
    private int previous;
    private int next;
    private int first;
    private int beginRow;
    private int endRow;

    public PageStore() {
    }

    public PageStore(int maxSize, int thisPage, String url, int i) {
        this.thisPage = thisPage;
        this.size = i;
        this.maxSize = maxSize;
        this.url = url;
        this.init();
    }

    /**
     * 初始化私有参数
     */
    private void init() {
        this.length = this.maxSize / this.size;
        if (this.maxSize % this.size != 0)
            this.length += 1;
        if (this.thisPage >= this.length) {
            this.previous = this.length - 1;
            this.thisPage = this.length;
            this.next = this.length;
        } else if (this.thisPage <= 1) {
            this.previous = 1;
            this.thisPage = 1;
            this.next = 2;
        } else {
            this.previous = this.thisPage - 1;
            this.next = this.thisPage + 1;
        }
        this.first = (this.thisPage - 1) * this.size;
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getThisPage() {
        return this.thisPage;
    }

    public void setThisPage(int thisPage) {
        this.thisPage = thisPage;
    }

    public int getPageCount() {
        return this.pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getPageBar() {
        return this.pageBar;
    }

    public void setPageBar(String pageBar) {
        this.pageBar = pageBar;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getLength() {
        return this.length;
    }

    public int getBeginRow() {
        return this.beginRow;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPrevious() {
        return this.previous;
    }

    public void setPrevious(int previous) {
        this.previous = previous;
    }

    public int getNext() {
        return this.next;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public int getFirst() {
        return this.first;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public void setBeginRow(int beginRow) {
        this.beginRow = beginRow;
    }

    public int getEndRow() {
        return this.endRow;
    }

    public void setEndRow(int endRow) {
        this.endRow = endRow;
    }
}
