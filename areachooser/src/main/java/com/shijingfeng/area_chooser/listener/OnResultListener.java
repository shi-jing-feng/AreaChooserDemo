package com.shijingfeng.area_chooser.listener;

import androidx.annotation.Nullable;

/**
 * Function: 结果 监听器
 * Date: 2020/5/8 10:55
 * Description:
 *
 * @author ShiJingFeng
 */
public interface OnResultListener<T> {

    /**
     * 成功回调
     * @param data 数据
     */
    default void onSuccess(@Nullable T data) {}

    /**
     * 失败回调
     * @param throwable 失败异常
     */
    default void onFailure(@Nullable Throwable throwable) {}

}
