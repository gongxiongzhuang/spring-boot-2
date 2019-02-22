package com.springboot.comm.Enum;

/**
 * @Description 返回码
 * @Date 2019/2/22 12:10
 * @Created by gongxz
 */
public enum CodeMessage {

    Success("000000", "操作成功"),
    ParamError("400001", "传入参数错误！"),
    Error("500000", "系统繁忙"),
    ;


    private String code;
    private String msg;
    CodeMessage(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }}
