package com.shijingfeng.area_chooser.util;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.shijingfeng.area_chooser.entity.Area;
import com.shijingfeng.area_chooser.entity.City;
import com.shijingfeng.area_chooser.entity.County;
import com.shijingfeng.area_chooser.entity.EntityStructure;
import com.shijingfeng.area_chooser.entity.Province;
import com.shijingfeng.area_chooser.listener.OnResultListener;

import java.io.BufferedReader;
import java.io.InputStream;
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
     *
     * @param inputStream Json数据文件输入流
     * @return 数据
     */
    @WorkerThread
    public static List<Area> getAreaLocalData(InputStream inputStream) {
        return getAreaLocalData(inputStream, null);
    }

    /**
     * 获取省市县三级数据
     *
     * @param inputStream Json数据文件输入流
     * @param entityStructure 实体类结构
     * @return 数据
     */
    @WorkerThread
    public static List<Area> getAreaLocalData(InputStream inputStream, @Nullable EntityStructure entityStructure) {
        if (inputStream == null) {
            throw new IllegalArgumentException("AreaUtil getAreaLocalData(InputStream, EntityStructure) 中的 InputStream不能为空");
        }
        if (entityStructure == null) {
            // 使用默认实体类结构
            entityStructure = new EntityStructure();
        }

        final List<Area> provinces = new ArrayList<>();

        try (Reader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            final JsonElement provinceJsonElement = JsonParser.parseReader(reader);

            if (provinceJsonElement != null && !provinceJsonElement.isJsonNull() && provinceJsonElement.isJsonArray()) {
                // 省列表
                final JsonArray provinceListArray = provinceJsonElement.getAsJsonArray();

                // 遍历省列表，添加省
                for (JsonElement provinceElement : provinceListArray) {
                    if (provinceElement != null && !provinceElement.isJsonNull() && provinceElement.isJsonObject()) {
                        final Province province = new Province();
                        final JsonObject provinceObject = provinceElement.getAsJsonObject();
                        final JsonElement provinceCode = provinceObject.get(entityStructure.provinceCodeFiled);
                        final JsonElement provinceName = provinceObject.get(entityStructure.provinceNameField);
                        final JsonElement provinceCityListElement = provinceObject.get(entityStructure.provinceChildrenFiled);

                        // 设置 省 邮政编码
                        if (provinceCode != null && !provinceCode.isJsonNull() && provinceCode.isJsonPrimitive()) {
                            final JsonPrimitive provinceCodePrimitive = provinceCode.getAsJsonPrimitive();

                            if (provinceCodePrimitive.isNumber()) {
                                province.setCode(String.valueOf(provinceCodePrimitive.getAsNumber().intValue()));
                            } else if (provinceCodePrimitive.isString()) {
                                province.setCode(provinceCodePrimitive.getAsString());
                            }
                        }
                        // 设置 省 名称
                        if (provinceName != null && !provinceName.isJsonNull() && provinceName.isJsonPrimitive()) {
                            final JsonPrimitive provinceNamePrimitive = provinceName.getAsJsonPrimitive();

                            if (provinceNamePrimitive.isString()) {
                                province.setName(provinceNamePrimitive.getAsString());
                            }
                        }

                        // 省 内 市列表
                        if (provinceCityListElement != null && !provinceCityListElement.isJsonNull() && provinceCityListElement.isJsonArray()) {
                            final JsonArray provinceCityListArray = provinceCityListElement.getAsJsonArray();
                            final List<Area> cityList = new ArrayList<>();

                            // 遍历 省 内 市列表
                            for (int i = 0; i < provinceCityListArray.size(); ++i) {
                                final JsonElement cityElement = provinceCityListArray.get(i);

                                if (cityElement != null && !cityElement.isJsonNull() && cityElement.isJsonObject()) {
                                    final City city = new City();
                                    final JsonObject cityObject = cityElement.getAsJsonObject();
                                    final JsonElement cityCode = cityObject.get(entityStructure.cityCodeField);
                                    final JsonElement cityName = cityObject.get(entityStructure.cityNameField);
                                    final JsonElement cityCountyListElement = cityObject.get(entityStructure.cityChildrenField);

                                    // 设置 市 邮政编码
                                    if (cityCode != null && !cityCode.isJsonNull() && cityCode.isJsonPrimitive()) {
                                        final JsonPrimitive cityCodePrimitive = cityCode.getAsJsonPrimitive();

                                        if (cityCodePrimitive.isNumber()) {
                                            city.setCode(String.valueOf(cityCodePrimitive.getAsNumber().intValue()));
                                        } else if (cityCodePrimitive.isString()) {
                                            city.setCode(cityCodePrimitive.getAsString());
                                        }
                                    }
                                    // 设置 市 名称
                                    if (cityName != null && !cityName.isJsonNull() && cityName.isJsonPrimitive()) {
                                        final JsonPrimitive cityNamePrimitive = cityName.getAsJsonPrimitive();

                                        if (cityNamePrimitive.isString()) {
                                            city.setName(cityNamePrimitive.getAsString());
                                        }
                                    }

                                    // 市 内 县列表
                                    if (cityCountyListElement != null && !cityCountyListElement.isJsonNull() && cityCountyListElement.isJsonArray()) {
                                        final List<Area> countyList = new ArrayList<>();
                                        final JsonArray cityCountyListArray = cityCountyListElement.getAsJsonArray();

                                        for (int j = 0; j < cityCountyListArray.size(); ++j) {
                                            final JsonElement countyElement = cityCountyListArray.get(j);

                                            if (countyElement != null && !countyElement.isJsonNull() && countyElement.isJsonObject()) {
                                                final County county = new County();
                                                final JsonObject countyObject = countyElement.getAsJsonObject();
                                                final JsonElement countyCode = countyObject.get(entityStructure.countyCodeField);
                                                final JsonElement countyName = countyObject.get(entityStructure.countyNameField);

                                                // 设置 县 邮政编码
                                                if (countyCode != null && !countyCode.isJsonNull() && countyCode.isJsonPrimitive()) {
                                                    final JsonPrimitive countyCodePrimitive = countyCode.getAsJsonPrimitive();

                                                    if (countyCodePrimitive.isNumber()) {
                                                        county.setCode(String.valueOf(countyCodePrimitive.getAsNumber().intValue()));
                                                    } else if (countyCodePrimitive.isString()) {
                                                        county.setCode(countyCodePrimitive.getAsString());
                                                    }
                                                }
                                                // 设置 县 名称
                                                if (countyName != null && !countyName.isJsonNull() && countyName.isJsonPrimitive()) {
                                                    final JsonPrimitive countyNamePrimitive = countyName.getAsJsonPrimitive();

                                                    if (countyNamePrimitive.isString()) {
                                                        county.setName(countyNamePrimitive.getAsString());
                                                    }
                                                }

                                                countyList.add(county);
                                            }
                                        }

                                        city.setCountyList(countyList);
                                    }

                                    cityList.add(city);
                                }
                            }

                            province.setCityList(cityList);
                        }

                        provinces.add(province);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return provinces;
    }

    /**
     * 获取省市县三级数据
     *
     * @param inputStream Json数据文件输入流
     * @param entityStructure 实体类结构
     * @param listener 结果监听器
     * @return 线程
     */
    public static Thread getAreaLocalData( final InputStream inputStream, @Nullable final EntityStructure entityStructure, @NonNull final OnResultListener<List<Area>> listener) {
        final Thread thread = new Thread(() -> {
            EntityStructure entityStructure1 = entityStructure;

            if (inputStream == null) {
                throw new IllegalArgumentException("AreaUtil getAreaLocalData(InputStream, EntityStructure) 中的 InputStream不能为空");
            }
            if (entityStructure1 == null) {
                // 使用默认实体类结构
                entityStructure1 = new EntityStructure();
            }

            final List<Area> provinces = new ArrayList<>();

            try (Reader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                final JsonElement provinceJsonElement = JsonParser.parseReader(reader);

                if (provinceJsonElement != null && !provinceJsonElement.isJsonNull() && provinceJsonElement.isJsonArray()) {
                    // 省列表
                    final JsonArray provinceListArray = provinceJsonElement.getAsJsonArray();

                    // 遍历省列表，添加省
                    for (JsonElement provinceElement : provinceListArray) {
                        if (provinceElement != null && !provinceElement.isJsonNull() && provinceElement.isJsonObject()) {
                            final Province province = new Province();
                            final JsonObject provinceObject = provinceElement.getAsJsonObject();
                            final JsonElement provinceCode = provinceObject.get(entityStructure1.provinceCodeFiled);
                            final JsonElement provinceName = provinceObject.get(entityStructure1.provinceNameField);
                            final JsonElement provinceCityListElement = provinceObject.get(entityStructure1.provinceChildrenFiled);

                            // 设置 省 邮政编码
                            if (provinceCode != null && !provinceCode.isJsonNull() && provinceCode.isJsonPrimitive()) {
                                final JsonPrimitive provinceCodePrimitive = provinceCode.getAsJsonPrimitive();

                                if (provinceCodePrimitive.isNumber()) {
                                    province.setCode(String.valueOf(provinceCodePrimitive.getAsNumber().intValue()));
                                } else if (provinceCodePrimitive.isString()) {
                                    province.setCode(provinceCodePrimitive.getAsString());
                                }
                            }
                            // 设置 省 名称
                            if (provinceName != null && !provinceName.isJsonNull() && provinceName.isJsonPrimitive()) {
                                final JsonPrimitive provinceNamePrimitive = provinceName.getAsJsonPrimitive();

                                if (provinceNamePrimitive.isString()) {
                                    province.setName(provinceNamePrimitive.getAsString());
                                }
                            }

                            // 省 内 市列表
                            if (provinceCityListElement != null && !provinceCityListElement.isJsonNull() && provinceCityListElement.isJsonArray()) {
                                final JsonArray provinceCityListArray = provinceCityListElement.getAsJsonArray();
                                final List<Area> cityList = new ArrayList<>();

                                // 遍历 省 内 市列表
                                for (int i = 0; i < provinceCityListArray.size(); ++i) {
                                    final JsonElement cityElement = provinceCityListArray.get(i);

                                    if (cityElement != null && !cityElement.isJsonNull() && cityElement.isJsonObject()) {
                                        final City city = new City();
                                        final JsonObject cityObject = cityElement.getAsJsonObject();
                                        final JsonElement cityCode = cityObject.get(entityStructure1.cityCodeField);
                                        final JsonElement cityName = cityObject.get(entityStructure1.cityNameField);
                                        final JsonElement cityCountyListElement = cityObject.get(entityStructure1.cityChildrenField);

                                        // 设置 市 邮政编码
                                        if (cityCode != null && !cityCode.isJsonNull() && cityCode.isJsonPrimitive()) {
                                            final JsonPrimitive cityCodePrimitive = cityCode.getAsJsonPrimitive();

                                            if (cityCodePrimitive.isNumber()) {
                                                city.setCode(String.valueOf(cityCodePrimitive.getAsNumber().intValue()));
                                            } else if (cityCodePrimitive.isString()) {
                                                city.setCode(cityCodePrimitive.getAsString());
                                            }
                                        }
                                        // 设置 市 名称
                                        if (cityName != null && !cityName.isJsonNull() && cityName.isJsonPrimitive()) {
                                            final JsonPrimitive cityNamePrimitive = cityName.getAsJsonPrimitive();

                                            if (cityNamePrimitive.isString()) {
                                                city.setName(cityNamePrimitive.getAsString());
                                            }
                                        }

                                        // 市 内 县列表
                                        if (cityCountyListElement != null && !cityCountyListElement.isJsonNull() && cityCountyListElement.isJsonArray()) {
                                            final List<Area> countyList = new ArrayList<>();
                                            final JsonArray cityCountyListArray = cityCountyListElement.getAsJsonArray();

                                            for (int j = 0; j < cityCountyListArray.size(); ++j) {
                                                final JsonElement countyElement = cityCountyListArray.get(j);

                                                if (countyElement != null && !countyElement.isJsonNull() && countyElement.isJsonObject()) {
                                                    final County county = new County();
                                                    final JsonObject countyObject = countyElement.getAsJsonObject();
                                                    final JsonElement countyCode = countyObject.get(entityStructure1.countyCodeField);
                                                    final JsonElement countyName = countyObject.get(entityStructure1.countyNameField);

                                                    // 设置 县 邮政编码
                                                    if (countyCode != null && !countyCode.isJsonNull() && countyCode.isJsonPrimitive()) {
                                                        final JsonPrimitive countyCodePrimitive = countyCode.getAsJsonPrimitive();

                                                        if (countyCodePrimitive.isNumber()) {
                                                            county.setCode(String.valueOf(countyCodePrimitive.getAsNumber().intValue()));
                                                        } else if (countyCodePrimitive.isString()) {
                                                            county.setCode(countyCodePrimitive.getAsString());
                                                        }
                                                    }
                                                    // 设置 县 名称
                                                    if (countyName != null && !countyName.isJsonNull() && countyName.isJsonPrimitive()) {
                                                        final JsonPrimitive countyNamePrimitive = countyName.getAsJsonPrimitive();

                                                        if (countyNamePrimitive.isString()) {
                                                            county.setName(countyNamePrimitive.getAsString());
                                                        }
                                                    }

                                                    countyList.add(county);
                                                }
                                            }

                                            city.setCountyList(countyList);
                                        }

                                        cityList.add(city);
                                    }
                                }

                                province.setCityList(cityList);
                            }

                            provinces.add(province);
                        }
                    }
                }
                new Handler(Looper.getMainLooper()).post(() -> listener.onSuccess(provinces));
            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> listener.onFailure(e));
            }
        });

        thread.start();
        return thread;
    }

}
