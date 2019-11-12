package com.wicep.library.entity;

import com.wicep.library.util.CastUtil;

import static com.wicep.library.constant.Constant.县;
import static com.wicep.library.constant.Constant.市;
import static com.wicep.library.constant.Constant.省;

/**
 * Function: 地区抽象实体类
 * Author: ShiJingFeng
 * Date: 2019/11/12 11:14
 * Description:
 */
public abstract class Area {

    /**
     * 获取区域级别
     * @return 区域级别
     */
    public int getAreaLevel() {
        final int areaLevel;

        if (this instanceof Province) {
            areaLevel = 省;
        } else if (this instanceof City) {
            areaLevel = 市;
        } else if (this instanceof County) {
            areaLevel = 县;
        } else {
            areaLevel = -1;
        }

        return areaLevel;
    }

    /**
     *  获取区域
     * @return 区域
     */
    public <T extends Area> T getArea() {
        return CastUtil.cast(this);
    }

}
