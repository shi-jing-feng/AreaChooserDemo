package com.shijingfeng.area_chooser.listener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.shijingfeng.area_chooser.AreaChooser;
import com.shijingfeng.area_chooser.entity.City;
import com.shijingfeng.area_chooser.entity.County;
import com.shijingfeng.area_chooser.entity.Province;

/**
 * Function: 选择区域完成监听器
 * Author: ShiJingFeng
 * Date: 2019/11/12 16:55
 * Description:
 */
public interface OnChooseCompleteListener {

    /**
     * 选中的省市县数据
     * @param areaChooser AreaChooser
     * @param province 选择的省
     * @param city 选择的市
     * @param county 选择的县
     */
    void onComplete(@NonNull AreaChooser areaChooser, @Nullable Province province, @Nullable City city, @Nullable County county);

}
