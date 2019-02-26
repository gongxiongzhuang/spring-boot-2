package com.springboot.comm.page;

import com.github.pagehelper.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @Description 分页工具
 * @Date 2019/2/22 17:59
 * @Created by gongxz
 */
@ApiModel(value = "PageList",description = "分页对象")
public class PageList<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "当前页",dataType = "int",name = "pageNum")
    private int pageNum;
    @ApiModelProperty(value = "每页的数量",dataType = "int",name = "pageSize")
    private int pageSize;
    @ApiModelProperty(value = "当前页的数量",dataType = "int",name = "size")
    private int size;
    @ApiModelProperty(value = "总页数",dataType = "int",name = "pages")
    private int pages;
    @ApiModelProperty(value = "总记录数",dataType = "long",name = "total")
    private long total;
    @ApiModelProperty(value = "结果集",dataType = "List",name = "list")
    private List<T> list;

    //@ApiModelProperty(value = "前一页",dataType = "int",name = "prePage")
    //private int prePage;
    //@ApiModelProperty(value = "下一页",dataType = "int",name = "nextPage")
    //private int nextPage;
    //@ApiModelProperty(value = "是否有前一页",dataType = "boolean",name = "hasPreviousPage")
    //private boolean hasPreviousPage = false;
    //@ApiModelProperty(value = "是否有下一页",dataType = "boolean",name = "hasNextPage")
    //private boolean hasNextPage = false;


    //由于startRow和endRow不常用，这里说个具体的用法
    //可以在页面中"显示startRow到endRow 共size条数据"
    //当前页面第一个元素在数据库中的行号
    //private int startRow;
    //当前页面最后一个元素在数据库中的行号
    //private int endRow;
    //是否为第一页
    //private boolean isFirstPage = false;
    //是否为最后一页
    //private boolean isLastPage = false;

    //导航页码数
    //private int navigatePages;
    //所有导航页号
    //private int[] navigatepageNums;
    //导航条上的第一页
    //private int navigateFirstPage;
    //导航条上的最后一页
    //private int navigateLastPage;

    /**
     * 包装Page对象
     *
     * @param list          page结果
     */
    public PageList(List<T> list) {
        this.list = list;
        if (list instanceof Page) {
            this.total = ((Page)list).getTotal();
        } else {
            this.total = (long)list.size();
        }
        if (list instanceof Page) {
            Page page = (Page) list;
            this.pageNum = page.getPageNum();
            this.pageSize = page.getPageSize();

            this.pages = page.getPages();
            this.size = page.size();
            //由于结果是>startRow的，所以实际的需要+1
            /*if (this.size == 0) {
                this.startRow = 0;
                this.endRow = 0;
            } else {
                this.startRow = page.getStartRow() + 1;
                //计算实际的endRow（最后一页的时候特殊）
                this.endRow = this.startRow - 1 + this.size;
            }*/
        } else {
            this.pageNum = 1;
            this.pageSize = list.size();

            this.pages = this.pageSize > 0 ? 1 : 0;
            this.size = list.size();
            //this.startRow = 0;
            //this.endRow = list.size() > 0 ? list.size() - 1 : 0;
        }
        //this.navigatePages = navigatePages;
        //计算导航页
        //calcNavigatepageNums();
        //计算前后页，第一页，最后一页
        //calcPage();
        //判断页面边界
        //judgePageBoudary();
    }

    public int getPageNum() {
        return this.pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPages() {
        return this.pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public long getTotal() {
        return this.total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getList() {
        return this.list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("PageInfo{");
        sb.append("pageNum=").append(this.pageNum);
        sb.append(", pageSize=").append(this.pageSize);
        sb.append(", size=").append(this.size);
        //sb.append(", startRow=").append(this.startRow);
        //sb.append(", endRow=").append(this.endRow);
        sb.append(", total=").append(this.total);
        sb.append(", pages=").append(this.pages);
        sb.append(", list=").append(this.list);
        //sb.append(", prePage=").append(this.prePage);
        //sb.append(", nextPage=").append(this.nextPage);
        //sb.append(", isFirstPage=").append(this.isFirstPage);
        //sb.append(", isLastPage=").append(this.isLastPage);
        //sb.append(", hasPreviousPage=").append(this.hasPreviousPage);
        //sb.append(", hasNextPage=").append(this.hasNextPage);
        //sb.append(", navigatePages=").append(this.navigatePages);
        //sb.append(", navigateFirstPage=").append(this.navigateFirstPage);
        //sb.append(", navigateLastPage=").append(this.navigateLastPage);
        sb.append(", navigatepageNums=");
        /*if (this.navigatepageNums == null) {
            sb.append("null");
        } else {
            sb.append('[');

            for(int i = 0; i < this.navigatepageNums.length; ++i) {
                sb.append(i == 0 ? "" : ", ").append(this.navigatepageNums[i]);
            }

            sb.append(']');
        }*/
        sb.append(", total=").append(this.total);
        sb.append(", list=").append(this.list);
        sb.append('}');
        return sb.toString();
    }
}
