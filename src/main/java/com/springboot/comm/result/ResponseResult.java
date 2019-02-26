package com.springboot.comm.result;

import com.springboot.comm.Enum.CodeMessage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description 返回信息
 * @Date 2019/2/22 14:43
 * @Created by gongxz
 */
@ApiModel(value = "ResponseResult",description = "返回信息")
public class ResponseResult<T> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ApiModelProperty(value = "返回编码",dataType = "String",name = "rspCode")
    private String rspCode ;
    @ApiModelProperty(value = "返回描述",dataType = "String",name = "rspMsg")
    private String rspMsg;
    @ApiModelProperty(value = "返回json数据对象",dataType = "Object",name = "data")
    private T data;

    public ResponseResult() {
        this(CodeMessage.Success);
    }

    public ResponseResult(CodeMessage codeMessage) {
        this(codeMessage.getMsg(),codeMessage.getCode());
    }

    public ResponseResult(T data) {
        this(CodeMessage.Success);
        this.data = data;
    }

    public ResponseResult(CodeMessage codeMessage, T data) {
        this(codeMessage);
        this.data = data;
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
