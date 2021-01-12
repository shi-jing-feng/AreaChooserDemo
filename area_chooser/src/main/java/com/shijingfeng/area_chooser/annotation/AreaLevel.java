package com.shijingfeng.area_chooser.annotation;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static com.shijingfeng.area_chooser.constant.Constant.CITY;
import static com.shijingfeng.area_chooser.constant.Constant.COUNTY;
import static com.shijingfeng.area_chooser.constant.Constant.PROVINCE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Function: 区域级别
 * Date: 2020/5/8 10:37
 * Description:
 * Author: ShiJingFeng
 */
@IntDef({
    // 省级
    PROVINCE,
    // 市级
    CITY,
    // 县级
    COUNTY
})
@Target({
    // 类属性
    FIELD,
    // 函数参数
    PARAMETER,
    // 局部变量
    LOCAL_VARIABLE
})
@Retention(SOURCE)
public @interface AreaLevel {}
