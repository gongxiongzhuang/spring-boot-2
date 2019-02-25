package com.springboot.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

@ApiModel(value="Permission")
public class Permission implements Serializable {
    @ApiModelProperty(value="",name="id",dataType="Integer")
    private Integer id;

    @ApiModelProperty(value="",name="permissionName",dataType="String")
    private String permissionName;

    @ApiModelProperty(value="",name="createTime",dataType="Date")
    private Date createTime;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName == null ? null : permissionName.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}