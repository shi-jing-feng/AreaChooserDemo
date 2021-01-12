package com.shijingfeng.area_chooser.util;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;

import com.shijingfeng.area_chooser.global.Global;

/**
 * Function:
 * Date: 2020/9/3 13:59
 * Description:
 *
 * Author: ShiJingFeng
 */
public class ResourceUtil {

    /**
     * 通过 String资源ID 获取 String文本
     * @param stringId String资源ID
     * @return String文本
     */
    public static String getStringById(@StringRes int stringId) {
        return Global.sContext.getResources().getString(stringId);
    }

    /**
     * 通过 Color资源ID 获取 Color
     * @param colorId Color资源ID
     * @return Color
     */
    public static @ColorInt int getColorById(@ColorRes int colorId) {
        return Global.sContext.getResources().getColor(colorId);
    }

}
