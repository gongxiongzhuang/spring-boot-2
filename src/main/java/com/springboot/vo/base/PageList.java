package com.springboot.vo.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @Description TODO
 * @Date 2019/2/22 17:59
 * @Created by gongxz
 */
@ApiModel(value = "PageList",description = "分页对象")
public class PageList<T> implements Serializable {

    @ApiModelProperty(value = "页码",name = "currentPage",dataType = "int")
    private int currentPage = 1;

    @ApiModelProperty(value = "页面记录数",name = "pageSize",dataType = "int")
    private int pageSize = 1;

    @ApiModelProperty(value = "总页数",name = "totalPage",dataType = "int")
    private int totalPage = 10;

    @ApiModelProperty(value = "总记录数",name = "totalCount",dataType = "long")
    private long totalCount = 10;

    @ApiModelProperty(value = "分页记录",name = "list",dataType = "List")
    private List<T> list;

    public PageList() {
    }

    public PageList(List<T> list){
        this.list = list;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
