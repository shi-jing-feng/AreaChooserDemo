package com.sjf.library.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Function: 省实体类
 * Created by shijingfeng on 19-1-17.
 */
public class Province extends Area {

    @SerializedName("code")
    private String code;
    @SerializedName("name")
    private String name;
    /**
     * 注意抽象类无法序列化，需自行处理 (参考 {@link com.sjf.library.util.AreaUtil})
     */
    @SerializedName("cityList")
    private List<Area> cityList;

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

    public List<Area> getCityList() {
        return cityList;
    }

    public void setCityList(List<Area> cityList) {
        this.cityList = cityList;
    }
}
