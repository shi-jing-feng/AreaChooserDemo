package com.sjf.library.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Function: 市实体类
 *
 * @author shijingfeng
 * @date 19-1-17
 */
public class City extends Area {

    /** 市邮政编码 */
    @SerializedName("code")
    private String code = "";

    /** 市名称 */
    @SerializedName("name")
    private String name = "";

    /**
     * 县列表
     * 注意抽象类无法序列化，需自行处理 (参考 {@link com.sjf.library.util.AreaUtil})
     */
    @SerializedName("areaList")
    private List<Area> countyList = new ArrayList<>();

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
