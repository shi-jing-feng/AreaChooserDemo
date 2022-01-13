package com.shijingfeng.area_chooser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shijingfeng.area_chooser.adapter.AreaChooseAdapter;
import com.shijingfeng.area_chooser.annotation.AreaLevel;
import com.shijingfeng.area_chooser.constant.Constant;
import com.shijingfeng.area_chooser.entity.Area;
import com.shijingfeng.area_chooser.entity.City;
import com.shijingfeng.area_chooser.entity.County;
import com.shijingfeng.area_chooser.entity.Province;
import com.shijingfeng.area_chooser.listener.OnChooseCompleteListener;
import com.shijingfeng.area_chooser.listener.OnChooseListener;
import com.shijingfeng.area_chooser.util.AreaUtil;
import com.shijingfeng.area_chooser.util.ResourceUtil;
import com.shijingfeng.area_chooser.util.SizeUtil;
import com.shijingfeng.area_chooser.util.WindowUtil;
import com.shijingfeng.library.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.shijingfeng.area_chooser.constant.Constant.COUNTY;
import static com.shijingfeng.area_chooser.constant.Constant.CITY;
import static com.shijingfeng.area_chooser.constant.Constant.PROVINCE;
import static com.shijingfeng.area_chooser.constant.Constant.UNKNOWN_AREA_LEVEL;

/**
 * Function: 区域选择器
 * Author: ShiJingFeng
 * Date: 2019/10/30 18:49
 * Description:
 */
public class AreaChooser {

    private TextView tvTitle;
    private ImageView ivCancel;
    private TextView tvProvinceLabel;
    private TextView tvCityLabel;
    private TextView tvCountyLabel;
    private RecyclerView rvAreaList;

    /** 自定义扩展数据 */
    private final Data mData;
    /** 监听器 */
    private final Listener mListener;
    /** Parent View */
    private final View mParentView;
    /** 区域选择 PopupWindow */
    private PopupWindow mAreaChoicePopupWindow;
    /** 区域选择 适配器 */
    private AreaChooseAdapter mAdapter;
    /** 区域数据 */
    private List<Area> mAreaDataList;
    /** 地区级别 */
    private int mAreaLevel = PROVINCE;

    /** 是否正在显示 */
    private boolean mIsShowing = false;

    public AreaChooser(@NonNull Data data, @NonNull Listener listener) {
        this.mData = data;
        this.mListener = listener;
        this.mAreaDataList = data.mAreaDataList;
        this.mParentView = data.activity.findViewById(android.R.id.content);
        init();
    }

    /**
     * 初始化
     */
    @SuppressLint("InflateParams")
    private void init() {
        final View content = LayoutInflater.from(mData.activity).inflate(R.layout.layout_area_chooser, null);

        tvTitle = content.findViewById(R.id.tv_title);
        ivCancel = content.findViewById(R.id.iv_cancel);
        tvProvinceLabel = content.findViewById(R.id.tv_province_label);
        tvCityLabel = content.findViewById(R.id.tv_city_label);
        tvCountyLabel = content.findViewById(R.id.tv_county_label);
        rvAreaList = content.findViewById(R.id.rv_area_list);

        // 设置数据
        if (mAreaDataList == null) {
            try {
                final InputStream inputStream = mData.activity.getAssets().open("json/province_city_county.json");

                mAreaDataList = AreaUtil.getAreaLocalData(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 设置标题
        if (mData.title != null) {
            tvTitle.setText(mData.title);
        }
        // 设置标签选中Tab为省级
        setSelectedStatus(PROVINCE);

        // 设置地区列表适配器
        rvAreaList.setLayoutManager(new LinearLayoutManager(mData.activity));
        rvAreaList.setAdapter(mAdapter = new AreaChooseAdapter(mData.activity, mData, mAreaDataList));

        // 设置PopupWindow
        mAreaChoicePopupWindow = new PopupWindow();
        mAreaChoicePopupWindow.setContentView(content);
        mAreaChoicePopupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        mAreaChoicePopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        mAreaChoicePopupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mAreaChoicePopupWindow.setOutsideTouchable(true);
        mAreaChoicePopupWindow.setFocusable(true);

        initAction();
    }

    /**
     * 初始化事件
     */
    @SuppressLint("NotifyDataSetChanged")
    private void initAction() {
        //设置为省级数据
        tvProvinceLabel.setOnClickListener(view -> {
            final List<Area> provinceList = mAreaDataList;

            setSelectedStatus(PROVINCE);
            mAdapter.setData(provinceList, mAreaLevel = PROVINCE);
            mAdapter.notifyDataSetChanged();
        });
        //设置为市级数据
        tvCityLabel.setOnClickListener(view -> {
            final int provincePosition = mAdapter.getPosition(PROVINCE);
            final Province province = mAreaDataList.get(provincePosition).getArea();
            final List<Area> cityList = province.getCityList();

            setSelectedStatus(CITY);
            mAdapter.setData(cityList, mAreaLevel = CITY);
            mAdapter.notifyDataSetChanged();
        });
        //设置县级数据
        tvCountyLabel.setOnClickListener(view -> {
            final int provincePosition = mAdapter.getPosition(PROVINCE);
            final int cityPosition = mAdapter.getPosition(CITY);
            final Province province = mAreaDataList.get(provincePosition).getArea();
            final City city = province.getCityList().get(cityPosition).getArea();
            final List<Area> countyList = city.getCountyList();

            setSelectedStatus(COUNTY);
            mAdapter.setData(countyList, mAreaLevel = COUNTY);
            mAdapter.notifyDataSetChanged();
        });
        //取消
        ivCancel.setOnClickListener(view -> {
            mIsShowing = false;
            mAreaChoicePopupWindow.dismiss();
        });
        //关闭窗口
        mAreaChoicePopupWindow.setOnDismissListener(() -> {
            mIsShowing = false;
            WindowUtil.setWindowOutsideBackground(mData.activity, 1f);
        });

        mAdapter.setOnItemEventListener((View view, Object data, int position, int flag) -> {
            switch (mAreaLevel) {
                case PROVINCE:
                    final Province province = (Province) data;
                    final List<Area> cityList = province.getCityList();

                    tvCountyLabel.setText(ResourceUtil.getStringById(R.string.请选择));
                    tvCountyLabel.setVisibility(View.GONE);
                    tvProvinceLabel.setText(province.getName());

                    if (mListener.mOnChooseListener != null) {
                        mListener.mOnChooseListener.onChoose(province);
                    }
                    if (mData.level == PROVINCE || cityList == null || cityList.size() == 0) {
                        if (mListener.mOnChooseCompleteListener != null) {
                            mListener.mOnChooseCompleteListener.onComplete(this, province, null, null);
                        }

                        mAreaChoicePopupWindow.dismiss();
                        return;
                    }

                    tvCityLabel.setText(ResourceUtil.getStringById(R.string.请选择));
                    setSelectedStatus(CITY);
                    tvCityLabel.setVisibility(View.VISIBLE);

                    //更新数据
                    mAdapter.setData(province.getCityList(), mAreaLevel = CITY);
                    mAdapter.notifyDataSetChanged();
                    break;
                case CITY:
                    final City city = (City) data;
                    final List<Area> countyList = city.getCountyList();

                    tvCityLabel.setText(city.getName());

                    if (mListener.mOnChooseListener != null) {
                        mListener.mOnChooseListener.onChoose(city);
                    }
                    if (mData.level == CITY || countyList == null || countyList.size() == 0) {
                        if (mListener.mOnChooseCompleteListener != null) {
                            final int provincePosition = mAdapter.getPosition(PROVINCE);
                            final Province province1 = mAreaDataList.get(provincePosition).getArea();

                            mListener.mOnChooseCompleteListener.onComplete(this, province1, city, null);
                        }

                        mAreaChoicePopupWindow.dismiss();
                        return;
                    }

                    tvCityLabel.setText(city.getName());
                    tvCountyLabel.setText(ResourceUtil.getStringById(R.string.请选择));
                    setSelectedStatus(COUNTY);
                    tvCountyLabel.setVisibility(View.VISIBLE);

                    //更新数据
                    mAdapter.setData(countyList, mAreaLevel = COUNTY);
                    mAdapter.notifyDataSetChanged();
                    break;
                case COUNTY:
                    final County county = (County) data;

                    tvCountyLabel.setText(county.getName());

                    if (mListener.mOnChooseListener != null) {
                        mListener.mOnChooseListener.onChoose(county);
                    }
                    if (mListener.mOnChooseCompleteListener != null) {
                        final int provincePosition = mAdapter.getPosition(PROVINCE);
                        final int cityPosition = mAdapter.getPosition(CITY);
                        final Province province1 = mAreaDataList.get(provincePosition).getArea();
                        final City city1 = province1.getCityList().get(cityPosition).getArea();

                        mListener.mOnChooseCompleteListener.onComplete(this, province1, city1, county);
                    }

                    mAreaChoicePopupWindow.dismiss();
                    break;
                default:
                    break;
            }
        });
    }

    /**
     * 设置地区选择显示标签颜色和背景
     *
     * @param areaLevel 区域级别
     */
    private void setSelectedStatus(int areaLevel) {
        final GradientDrawable gradientDrawable = new GradientDrawable();
        final LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{gradientDrawable});
        final int distance = SizeUtil.dp2px(-3F);

        gradientDrawable.setColor(Color.WHITE);
        gradientDrawable.setStroke(SizeUtil.dp2px(2F), mData.color);

        layerDrawable.setLayerInset(0, distance, distance, distance, 0);

        switch (areaLevel) {
            case PROVINCE:
                tvProvinceLabel.setTextColor(mData.color);
                tvProvinceLabel.setBackground(layerDrawable);

                tvCityLabel.setTextColor(Color.BLACK);
                tvCityLabel.setBackgroundColor(Color.WHITE);
                tvCountyLabel.setTextColor(Color.BLACK);
                tvCountyLabel.setBackgroundColor(Color.WHITE);
                break;
            case CITY:
                tvProvinceLabel.setTextColor(Color.BLACK);
                tvProvinceLabel.setBackgroundColor(Color.WHITE);

                tvCityLabel.setTextColor(mData.color);
                tvCityLabel.setBackground(layerDrawable);

                tvCountyLabel.setTextColor(Color.BLACK);
                tvCountyLabel.setBackgroundColor(Color.WHITE);
                break;
            case COUNTY:
                tvProvinceLabel.setTextColor(Color.BLACK);
                tvProvinceLabel.setBackgroundColor(Color.WHITE);
                tvCityLabel.setTextColor(Color.BLACK);
                tvCityLabel.setBackgroundColor(Color.WHITE);

                tvCountyLabel.setTextColor(mData.color);
                tvCountyLabel.setBackground(layerDrawable);
                break;
            default:
                break;
        }
    }

    /**
     * 重置
     */
    @SuppressLint("NotifyDataSetChanged")
    private void reset() {
        tvCountyLabel.setText(ResourceUtil.getStringById(R.string.请选择));
        tvCountyLabel.setVisibility(View.GONE);
        tvCityLabel.setText(ResourceUtil.getStringById(R.string.请选择));
        tvCityLabel.setVisibility(View.GONE);
        tvProvinceLabel.setText(ResourceUtil.getStringById(R.string.请选择));

        setSelectedStatus(PROVINCE);

        //更新数据
        mAdapter.setData(mAreaDataList, mAreaLevel = PROVINCE);
        mAdapter.reset();
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 设置默认选中的位置
     *
     * @param provincePosition 选中的 省 Position
     * @param cityPosition     选中的 市 Position
     * @param countyPosition   选中的 县 Position
     */
    @SuppressLint("NotifyDataSetChanged")
    private void setPosition(int provincePosition, int cityPosition, int countyPosition) {
        final int areaLevel;

        if (countyPosition >= 0) {
            areaLevel = COUNTY;
        } else if (cityPosition >= 0) {
            areaLevel = CITY;
        } else if (provincePosition >= 0) {
            areaLevel = PROVINCE;
        } else {
            areaLevel = UNKNOWN_AREA_LEVEL;
        }

        Province province;
        City city;
        County county;

        final List<Area> areaList;

        switch (areaLevel) {
            case PROVINCE:
                province = mAreaDataList.get(provincePosition).getArea();
                areaList = mAreaDataList;

                tvProvinceLabel.setText(province.getName());
                tvProvinceLabel.setVisibility(View.VISIBLE);
                tvCityLabel.setText(ResourceUtil.getStringById(R.string.请选择));
                tvCityLabel.setVisibility(View.GONE);
                tvCountyLabel.setText(ResourceUtil.getStringById(R.string.请选择));
                tvCountyLabel.setVisibility(View.GONE);

                setSelectedStatus(PROVINCE);
                break;
            case CITY:
                province = mAreaDataList.get(provincePosition).getArea();
                city = province.getCityList().get(cityPosition).getArea();
                areaList = province.getCityList();

                tvProvinceLabel.setText(province.getName());
                tvProvinceLabel.setVisibility(View.VISIBLE);

                tvCityLabel.setText(city.getName());
                tvCityLabel.setVisibility(View.VISIBLE);

                tvCountyLabel.setText(ResourceUtil.getStringById(R.string.请选择));
                tvCountyLabel.setVisibility(View.GONE);

                setSelectedStatus(CITY);
                break;
            case COUNTY:
                province = mAreaDataList.get(provincePosition).getArea();
                city = province.getCityList().get(cityPosition).getArea();
                county = city.getCountyList().get(countyPosition).getArea();
                areaList = city.getCountyList();

                tvProvinceLabel.setText(province.getName());
                tvProvinceLabel.setVisibility(View.VISIBLE);

                tvCityLabel.setText(city.getName());
                tvCityLabel.setVisibility(View.VISIBLE);

                tvCountyLabel.setText(county.getName());
                tvCountyLabel.setVisibility(View.VISIBLE);

                setSelectedStatus(COUNTY);
                break;
            default:
                areaList = null;
                break;
        }
        mAdapter.setPosition(provincePosition, cityPosition, countyPosition);
        mAdapter.setData(areaList, mAreaLevel = areaLevel);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 显示
     */
    public void show() {
        if (mIsShowing) {
            return;
        }
        mIsShowing = true;
        WindowUtil.setWindowOutsideBackground(mData.activity, 0.4f);
        mAreaChoicePopupWindow.showAtLocation(mParentView, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 隐藏
     */
    public void hide() {
        if (!mIsShowing) {
            return;
        }
        mIsShowing = false;
        mAreaChoicePopupWindow.dismiss();
    }

    /**
     * 设置标题文本
     * @param title 标题文本
     * @return Builder
     */
    public AreaChooser setTitle(@NonNull String title) {
        tvTitle.setText(title);
        return this;
    }

    /**
     * 设置省市县（用于初始化显示）
     * @param province 省
     * @param city 市
     * @param county 县
     * @return AreaChooser
     */
    public AreaChooser setArea(@Nullable Province province, @Nullable City city, @Nullable County county) {
        if (province == null) {
            return this;
        }
        for (int i = 0; i < mAreaDataList.size(); ++i) {
            final Province curProvince = mAreaDataList.get(i).getArea();

            if (curProvince.getCode().equals(province.getCode())) {
                if (city == null) {
                    setPosition(i, -1, -1);
                    return this;
                } else {
                    for (int j = 0; j < curProvince.getCityList().size(); ++j) {
                        final City curCity = curProvince.getCityList().get(j).getArea();

                        if (curCity.getCode().equals(city.getCode())) {
                            if (county == null) {
                                setPosition(i, j, -1);
                                return this;
                            } else {
                                for (int k = 0; k < curCity.getCountyList().size(); ++k) {
                                    final County curCounty = curCity.getCountyList().get(k).getArea();

                                    if (curCounty.getCode().equals(county.getCode())) {
                                        setPosition(i, j, k);
                                        return this;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return this;
    }

    /**
     * 获取指定级别的数据
     * @param areaLevel 指定级别
     * @return 数据
     */
    public List<Area> getAreaDataList(int areaLevel) {
        List<Area> areaList = null;

        switch (areaLevel) {
            case PROVINCE:
                areaList = mAreaDataList;
                break;
            case CITY:
                final int provincePosition1 = mAdapter.getPosition(PROVINCE);

                if (provincePosition1 > -1) {
                    final Province province = mAreaDataList.get(provincePosition1).getArea();

                    areaList = province.getCityList();
                }
                break;
            case COUNTY:
                final int provincePosition2 = mAdapter.getPosition(PROVINCE);

                if (provincePosition2 > -1) {
                    final Province province = mAreaDataList.get(provincePosition2).getArea();
                    final int cityPosition = mAdapter.getPosition(CITY);

                    if (cityPosition > -1) {
                        final City city = province.getCityList().get(cityPosition).getArea();

                        areaList = city.getCountyList();
                    }
                }
                break;
            default:
                break;
        }

        return areaList;
    }

    /**
     * 获取 省
     *
     * @param provinceCode 省代码
     * @return 省
     */
    @Nullable
    public Province getProvince(@NonNull String provinceCode) {
        if (TextUtils.isEmpty(provinceCode)
                || mAreaDataList == null
                || mAreaDataList.isEmpty()) {
            return null;
        }
        if (mAreaDataList.get(0).getAreaLevel() != Constant.PROVINCE) {
            return null;
        }
        for (Area curArea : mAreaDataList) {
            final Province curProvince = curArea.getArea();

            if (TextUtils.equals(curProvince.getCode(), provinceCode)) {
                return curProvince;
            }
        }
        return null;
    }

    /**
     * 获取 市
     *
     * @param provinceCode 省代码
     * @param cityCode 市代码
     * @return 市
     */
    @Nullable
    public City getCity(@NonNull String provinceCode, @NonNull String cityCode) {
        final Province province;
        final List<Area> cityList;

        province = getProvince(provinceCode);
        if (province == null) {
            return null;
        }
        cityList = province.getCityList();
        if (cityList == null || cityList.isEmpty()) {
            return null;
        }
        if (cityList.get(0).getAreaLevel() != Constant.CITY) {
            return null;
        }
        for (Area curArea : cityList) {
            final City curCity = curArea.getArea();

            if (TextUtils.equals(curCity.getCode(), cityCode)) {
                return curCity;
            }
        }
        return null;
    }

    /**
     * 获取 县
     *
     * @param provinceCode 省代码
     * @param cityCode 市代码
     * @param countyCode 县代码
     * @return 县
     */
    @Nullable
    public County getCounty(@NonNull String provinceCode, @NonNull String cityCode, @NonNull String countyCode) {
        final City city;
        final List<Area> countyList;

        city = getCity(provinceCode, cityCode);
        if (city == null) {
            return null;
        }
        countyList = city.getCountyList();
        if (countyList == null || countyList.isEmpty()) {
            return null;
        }
        if (countyList.get(0).getAreaLevel() != Constant.COUNTY) {
            return null;
        }
        for (Area curArea : countyList) {
            final County curCounty = curArea.getArea();

            if (TextUtils.equals(curCounty.getCode(), countyCode)) {
                return curCounty;
            }
        }
        return null;
    }

    /**
     * 选择区域监听器
     * @param listener 监听器
     * @return Builder
     */
    public AreaChooser setOnChooseListener(OnChooseListener listener) {
        this.mListener.mOnChooseListener = listener;
        return this;
    }

    /**
     * 设置选择区域完成监听器
     * @param listener 监听器
     * @return Builder
     */
    public AreaChooser setOnChooseCompleteListener(OnChooseCompleteListener listener) {
        this.mListener.mOnChooseCompleteListener = listener;
        return this;
    }

    /**
     * 构建器类
     */
    public static class Builder {

        private Data mData = new Data();
        private Listener mListener = new Listener();

        public Builder(@NonNull Activity activity) {
            this.mData.activity = activity;
        }

        /**
         * 设置联动级别
         * @param level 级别
         * @return Builder
         */
        public Builder setLevel(@AreaLevel int level) {
            this.mData.level = level;
            return this;
        }

        /**
         * 设置主题颜色
         * @param color 主题颜色
         * @return Builder
         */
        public Builder setThemeColor(@ColorInt int color) {
            this.mData.color = color;
            return this;
        }

        /**
         * 设置标题文本
         * @param title 标题文本
         * @return Builder
         */
        public Builder setTitle(String title) {
            this.mData.title = title;
            return this;
        }

        /**
         * 省市县数据
         * @param areaDataList 数据
         * @return Builder
         */
        public Builder setAreaData(@NonNull List<Area> areaDataList) {
            this.mData.mAreaDataList = areaDataList;
            return this;
        }

        /**
         * 设置选择区域完成监听器
         * @param listener 监听器
         * @return Builder
         */
        public Builder setOnChooseCompleteListener(OnChooseCompleteListener listener) {
            this.mListener.mOnChooseCompleteListener = listener;
            return this;
        }

        /**
         * 选择区域监听器
         * @param listener 监听器
         * @return Builder
         */
        public Builder setOnChooseListener(OnChooseListener listener) {
            this.mListener.mOnChooseListener = listener;
            return this;
        }

        /**
         * 创建AreaChooser
         * @return AreaChooser
         */
        public AreaChooser create() {
            return new AreaChooser(mData, mListener);
        }

        /**
         * 显示 AreaChooser
         * @return AreaChooser
         */
        public AreaChooser show() {
            final AreaChooser areaChooser = new AreaChooser(mData, mListener);

            areaChooser.show();

            return areaChooser;
        }
    }

    /**
     * 自定义扩展数据类
     */
    public static class Data {

        /** Context */
        public Activity activity;
        /** 联动级别 默认3级联动（省市县）*/
        public @AreaLevel int level = COUNTY;
        /** 主题颜色 默认红色 */
        public int color = ResourceUtil.getColorById(R.color.red);
        /** 标题文本 */
        public String title = null;
        /** 省市县数据 */
        public List<Area> mAreaDataList;

    }

    /**
     * 监听器
     */
    private static class Listener {

        /** 选择区域完成监听器 */
        private OnChooseCompleteListener mOnChooseCompleteListener;
        /** 选择区域监听器 */
        private OnChooseListener mOnChooseListener;

    }

}
