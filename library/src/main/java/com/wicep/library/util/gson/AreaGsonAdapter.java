package com.wicep.library.util.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.wicep.library.entity.Area;

import java.lang.reflect.Type;

/**
 * Function: Gson Area类型解析适配器
 * Author: ShiJingFeng
 * Date: 2019/11/12 13:05
 * Description:
 */
public class AreaGsonAdapter implements JsonDeserializer<Area> {

    @Override
    public Area deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        return null;
    }
}
