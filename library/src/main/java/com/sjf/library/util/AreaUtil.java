package com.sjf.library.util;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sjf.library.entity.Area;
import com.sjf.library.entity.City;
import com.sjf.library.entity.County;
import com.sjf.library.entity.Province;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Function: 区域工具类
 * Created by shijingfeng on 19-1-18.
 */
public class AreaUtil {

    /**
     * 获取省市县三级数据
     * @return 数据
     */
    public static List<Area> getAreaLocalData(Context context) {
        List<Area> provinces = new ArrayList<>();
        Reader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(context.getAssets().open("json/province_city_county.json")));

            final JsonParser parser = new JsonParser();
            final JsonArray provinceListArray = parser.parse(reader).getAsJsonArray();

            for (JsonElement provinceElement : provinceListArray) {
                final Province province = new Province();
                final JsonObject provinceObject = provinceElement.getAsJsonObject();
                final JsonElement provinceCode = provinceObject.get("code");
                final JsonElement provinceName = provinceObject.get("name");
                final JsonElement provinceCityListElement = provinceObject.get("cityList");

                province.setCode(provinceCode == null ? null : provinceCode.getAsString());
                province.setName(provinceName == null ? null : provinceName.getAsString());

                if (provinceCityListElement != null) {
                    final JsonArray provinceCityListArray = provinceCityListElement.getAsJsonArray();
                    final List<Area> cityList = new ArrayList<>();

                    for (int i = 0; i < provinceCityListArray.size(); ++i) {
                        final City city = new City();
                        final JsonElement cityElement = provinceCityListArray.get(i);
                        final JsonObject cityObject = cityElement.getAsJsonObject();
                        final JsonElement cityCode = cityObject.get("code");
                        final JsonElement cityName = cityObject.get("name");
                        final JsonElement cityCountyListElement = cityObject.get("areaList");

                        city.setCode(cityCode == null ? null : cityCode.getAsString());
                        city.setName(cityName == null ? null : cityName.getAsString());

                        if (cityCountyListElement != null) {
                            final List<Area> countyList = new ArrayList<>();
                            final JsonArray cityCountyListArray = cityCountyListElement.getAsJsonArray();

                            for (int j = 0; j < cityCountyListArray.size(); ++j) {
                                final County county = new County();
                                final JsonElement countyElement = cityCountyListArray.get(j);
                                final JsonObject countyObject = countyElement.getAsJsonObject();
                                final JsonElement countyCode = countyObject.get("code");
                                final JsonElement countyName = countyObject.get("name");

                                county.setCode(countyCode == null ? null : countyCode.getAsString());
                                county.setName(countyName == null ? null : countyName.getAsString());

                                countyList.add(county);
                            }
                            city.setCountyList(countyList);
                        }
                        cityList.add(city);
                    }
                    province.setCityList(cityList);
                }
                provinces.add(province);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return provinces;
    }

}
