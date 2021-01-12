package com.shijingfeng.area_chooser.entity;

/**
 * Function: 实体类结构
 * Date: 2020/5/13 11:42
 * Description:
 *
 * Author: ShiJingFeng
 */
public class EntityStructure {

    /** 省级(省, 直辖市, 自治区) 邮政编码 字段名称 */
    public String provinceCodeFiled = "code";
    /** 省级(省, 直辖市, 自治区) 名称 字段名称 */
    public String provinceNameField = "name";
    /** 省级(省, 直辖市, 自治区) 市列表 字段名称 */
    public String provinceChildrenFiled = "cityList";

    /** 市级(地级市, 区) 邮政编码 字段名称 */
    public String cityCodeField = "code";
    /** 市级(地级市, 区) 名称 字段名称 */
    public String cityNameField = "name";
    /** 市级(地级市, 区) 县列表 字段名称 */
    public String cityChildrenField = "areaList";

    /** 县级(县级市, 县) 邮政编码 字段名称 */
    public String countyCodeField = "code";
    /** 县级(县级市, 县) 名称 字段名称 */
    public String countyNameField = "name";
    /** 县级(县级市, 县) 镇列表 字段名称 */
    public String countyChildrenField = "";

    /** 镇级(镇, 乡) 邮政编码 字段名称 */
    public String townCodeField = "code";
    /** 镇级(镇, 乡) 名称 字段名称 */
    public String townNameField = "name";

}
