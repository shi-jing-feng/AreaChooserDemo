package com.shijingfeng.area_chooser.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Function: 县实体类
 *
 * @author shijingfeng
 * @date 19-1-17
 */
public class County extends Area {

    /** 县邮政编码 */
    @SerializedName("code")
    private String code = "";

    /** 县名称 */
    @SerializedName("name")
    private String name = "";

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
