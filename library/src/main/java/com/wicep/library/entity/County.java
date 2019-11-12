package com.wicep.library.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Function: 县实体类
 * Created by shijingfeng on 19-1-17.
 */
public class County extends Area {

    @SerializedName("code")
    private String code;
    @SerializedName("name")
    private String name;

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
