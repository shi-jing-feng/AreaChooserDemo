package com.sjf.library.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Function: 市实体类
 * Created by shijingfeng on 19-1-17.
 */
public class City extends Area {

    @SerializedName("code")
    private String code;
    @SerializedName("name")
    private String name;
    /**
     * 注意抽象类无法序列化，需自行处理 (参考 {@link com.sjf.library.util.AreaUtil})
     */
    @SerializedName("areaList")
    private List<Area> countyList;

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

    public List<Area> getCountyList() {
        return countyList;
    }

    public void setCountyList(List<Area> countyList) {
        this.countyList = countyList;
    }
}
