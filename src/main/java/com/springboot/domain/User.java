package com.springboot.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

@ApiModel(value="User")
public class User implements Serializable {
    @ApiModelProperty(value="物理主键",name="id",dataType="Long")
    private Long id;

    @ApiModelProperty(value="姓名",name="name",dataType="String")
    private String name;

    @ApiModelProperty(value="年龄",name="age",dataType="Integer")
    private Integer age;

    @ApiModelProperty(value="uuid作为代理主键，每个表必须的字段，所有的表关联关系都用uuid作为关联，设置uuid为索引或者外建",name="uuid",dataType="String")
    private String uuid;

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid == null ? null : uuid.trim();
    }
}