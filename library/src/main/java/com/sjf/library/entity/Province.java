package com.sjf.library.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Function: 省实体类
 *
 * @author shijingfeng
 * @date 19-1-17
 */
public class Province extends Area {

    /** 省邮政编码 */
    @SerializedName("code")
    private String code = "";

    /** 省名称 */
    @SerializedName("name")
    private String name = "";

    /**
     * 市列表
     * 注意抽象类无法使用 Gson 序列化，需自行处理 (参考 {@link com.sjf.library.util.AreaUtil})
     */
    @SerializedName("cityList")
    private List<Area> cityList = new ArrayList<>();

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
