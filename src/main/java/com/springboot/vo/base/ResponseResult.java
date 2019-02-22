package com.springboot.vo.base;

import com.springboot.comm.Enum.CodeMessage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Description 返回信息
 * @Date 2019/2/22 14:43
 * @Created by gongxz
 */
@ApiModel(value = "ResponseResult",description = "返回信息")
public class ResponseResult<T> {

    @ApiModelProperty(value = "返回编码",dataType = "String",name = "rspCode")
    private String rspCode ;
    @ApiModelProperty(value = "返回描述",dataType = "String",name = "rspMsg")
    private String rspMsg;
    @ApiModelProperty(value = "返回json数据对象",dataType = "Object",name = "data")
    private T data;

    public ResponseResult() {
        this(CodeMessage.Success.getCode(),CodeMessage.Success.getMsg());
    }

    public ResponseResult(String rspCode, String rspMsg) {
        this.rspCode = rspCode;
        this.rspMsg = rspMsg;
    }

    public ResponseResult(String rspCode, String rspMsg, T data) {
        this.rspCode = rspCode;
        this.rspMsg = rspMsg;
        this.data = data;
    }

    public ResponseResult(T data) {
        this.data = data;
    }

    public String getRspCode() {
        return rspCode;
    }

    public void setRspCode(String rspCode) {
        this.rspCode = rspCode;
    }

    public String getRspMsg() {
        return rspMsg;
    }

    public void setRspMsg(String rspMsg) {
        this.rspMsg = rspMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
