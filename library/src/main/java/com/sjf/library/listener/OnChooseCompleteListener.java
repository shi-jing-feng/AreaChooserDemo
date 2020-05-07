package com.sjf.library.listener;

import androidx.annotation.Nullable;

import com.sjf.library.entity.City;
import com.sjf.library.entity.County;
import com.sjf.library.entity.Province;

/**
 * Function: 选择区域完成监听器
 * Author: ShiJingFeng
 * Date: 2019/11/12 16:55
 * Description:
 */
public interface OnChooseCompleteListener {

    /**
     * 选中的省市县数据
     * @param province 选择的省
     * @param city 选择的市
     * @param county 选择的县
     */
    void onComplete(@Nullable Province province, @Nullable City city, @Nullable County county);

}
