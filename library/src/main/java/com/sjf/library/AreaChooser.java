package com.sjf.library;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sjf.library.adapter.AreaChooseAdapter;
import com.sjf.library.entity.Area;
import com.sjf.library.entity.City;
import com.sjf.library.entity.County;
import com.sjf.library.entity.Province;
import com.sjf.library.listener.OnChooseCompleteListener;
import com.sjf.library.listener.OnChooseListener;
import com.sjf.library.util.AreaUtil;
import com.sjf.library.util.WindowUtil;

import java.util.List;

import static com.sjf.library.constant.Constant.COUNTY;
import static com.sjf.library.constant.Constant.CITY;
import static com.sjf.library.constant.Constant.PROVINCE;

/**
 * Function: 区域选择器
 * Author: ShiJingFeng
 * Date: 2019/10/30 18:49
 * Description:
 */
public class AreaChooser {

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
    private PopupWindow mAreaChoicePopupwindow;
    /** 区域选择 适配器 */
    private AreaChooseAdapter mAdapter;
    /** 区域数据 */
    private List<Area> mAreaDataList;
    /** 地区级别 */
    private int mAreaLevel = PROVINCE;

    public AreaChooser(@NonNull Data data, @NonNull Listener listener) {
        this.mData = data;
        this.mListener = listener;
        this.mAreaDataList = data.mAreaDataList;
        this.mParentView = data.mActivity.findViewById(android.R.id.content);
    }

    /**
     * 显示
     */
    public void show() {
        if (mAreaChoicePopupwindow != null) {
            if (!mAreaChoicePopupwindow.isShowing()) {
                WindowUtil.setWindowOutsideBackground(mData.mActivity, 0.4f);
                mAreaChoicePopupwindow.showAtLocation(mParentView, Gravity.BOTTOM, 0, 0);
            }
            return;
        }

        final LinearLayout llContent = new LinearLayout(mData.mActivity);

        llContent.setOrientation(LinearLayout.VERTICAL);

        //因为layout_area_choice根节点为merge(为了防止PopupWindow布局失效)所以必须绑定到root布局
        final View content = LayoutInflater.from(mData.mActivity).inflate(R.layout.layout_area_choice, llContent, true);

        ivCancel = content.findViewById(R.id.iv_cancel);
        tvProvinceLabel = content.findViewById(R.id.tv_province_label);
        tvCityLabel = content.findViewById(R.id.tv_city_label);
        tvCountyLabel = content.findViewById(R.id.tv_county_label);
        rvAreaList = content.findViewById(R.id.rv_area_list);

        if (mAreaDataList == null) {
            mAreaDataList = AreaUtil.getAreaLocalData(mData.mActivity);
        }

        mAdapter = new AreaChooseAdapter(mData.mActivity, mAreaDataList);

        rvAreaList.setLayoutManager(new LinearLayoutManager(mData.mActivity));
        rvAreaList.setAdapter(mAdapter);

        mAreaChoicePopupwindow = new PopupWindow();
        mAreaChoicePopupwindow.setContentView(content);
        mAreaChoicePopupwindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        mAreaChoicePopupwindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        mAreaChoicePopupwindow.setBackgroundDrawable(content.getResources().getDrawable(R.color.white));
        mAreaChoicePopupwindow.setOutsideTouchable(true);
        mAreaChoicePopupwindow.setFocusable(true);
        WindowUtil.setWindowOutsideBackground(mData.mActivity, 0.4f);
        mAreaChoicePopupwindow.showAtLocation(mParentView, Gravity.BOTTOM, 0, 0);

        initAreaAction();

    }

    /**
     * 初始化处理收货地址选择区域点击事件
     */
    private void initAreaAction() {
        //设置为省级数据
        tvProvinceLabel.setOnClickListener(view -> {
            final List<Area> provinceList = mAreaDataList;

            setSelectedStatus(tvProvinceLabel);
            mAdapter.setData(provinceList, mAreaLevel = PROVINCE);
            mAdapter.notifyDataSetChanged();
        });
        //设置为市级数据
        tvCityLabel.setOnClickListener(view -> {
            final int provincePosition = mAdapter.getPosition(PROVINCE);
            final Province province = mAreaDataList.get(provincePosition).getArea();
            final List<Area> cityList = province.getCityList();

            setSelectedStatus(tvCityLabel);
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

            setSelectedStatus(tvCountyLabel);
            mAdapter.setData(countyList, mAreaLevel = COUNTY);
            mAdapter.notifyDataSetChanged();
        });
        //取消
        ivCancel.setOnClickListener(view -> {
            mAreaChoicePopupwindow.dismiss();
        });
        //关闭窗口
        mAreaChoicePopupwindow.setOnDismissListener(() -> {
            WindowUtil.setWindowOutsideBackground(mData.mActivity, 1f);
            reset();
        });

        mAdapter.setOnItemEventListener((View view, Object data, int position, int flag) -> {
            switch (mAreaLevel) {
                case PROVINCE:
                    final Province province = (Province) data;
                    final List<Area> cityList = province.getCityList();

                    tvCountyLabel.setText("请选择");
                    tvCountyLabel.setVisibility(View.GONE);
                    tvProvinceLabel.setText(province.getName());

                    if (cityList == null || cityList.size() == 0) {
                        mListener.mOnChooseCompleteListener.onComplete(province, null, null);

                        mAreaChoicePopupwindow.dismiss();
                        return;
                    }

                    tvCityLabel.setText("请选择");
                    setSelectedStatus(tvCityLabel);
                    tvCityLabel.setVisibility(View.VISIBLE);

                    //更新数据
                    mAdapter.setData(province.getCityList(), mAreaLevel = CITY);
                    mAdapter.notifyDataSetChanged();
                    break;
                case CITY:
                    final City city = (City) data;
                    final List<Area> countyList = city.getCountyList();

                    tvCityLabel.setText(city.getName());

                    if (countyList == null || countyList.size() == 0) {
                        final int provincePosition = mAdapter.getPosition(PROVINCE);
                        final Province province1 = mAreaDataList.get(provincePosition).getArea();

                        mListener.mOnChooseCompleteListener.onComplete(province1, city, null);

                        mAreaChoicePopupwindow.dismiss();
                        return;
                    }

                    tvCityLabel.setText(city.getName());
                    tvCountyLabel.setText("请选择");
                    setSelectedStatus(tvCountyLabel);
                    tvCountyLabel.setVisibility(View.VISIBLE);

                    //更新数据
                    mAdapter.setData(countyList, mAreaLevel = COUNTY);
                    mAdapter.notifyDataSetChanged();
                    break;
                case COUNTY:
                    final County county = (County) data;

                    tvCountyLabel.setText(county.getName());

                    if (mListener.mOnChooseCompleteListener != null) {
                        final int provincePosition = mAdapter.getPosition(PROVINCE);
                        final int cityPosition = mAdapter.getPosition(CITY);
                        final Province province1 = mAreaDataList.get(provincePosition).getArea();
                        final City city1 = province1.getCityList().get(cityPosition).getArea();

                        mListener.mOnChooseCompleteListener.onComplete(province1, city1, county);
                    }

                    mAreaChoicePopupwindow.dismiss();
                    break;
                default:
                    break;
            }
        });
    }

    /**
     * 设置地区选择显示标签颜色和背景
     * @param view 要突出颜色的View
     */
    private void setSelectedStatus(View view) {
        final TextView[] views = {tvProvinceLabel, tvCityLabel, tvCountyLabel};

        if (view != null) {
            for (TextView v : views) {
                if (v.getId() == view.getId()) {
                    v.setTextColor(mData.mActivity.getResources().getColor(R.color.red));
                    v.setBackground(mData.mActivity.getResources().getDrawable(R.drawable.layer_list_underline_red));
                } else {
                    v.setTextColor(mData.mActivity.getResources().getColor(R.color.black));
                    v.setBackground(mData.mActivity.getResources().getDrawable(R.color.white));
                }
            }
        }
    }

    /**
     * 重置
     */
    private void reset() {
        tvCountyLabel.setText("请选择");
        tvCountyLabel.setVisibility(View.GONE);
        tvCityLabel.setText("请选择");
        tvCityLabel.setVisibility(View.GONE);
        tvProvinceLabel.setText("请选择");
        setSelectedStatus(tvProvinceLabel);

        //更新数据
        mAdapter.setData(mAreaDataList, mAreaLevel = PROVINCE);
        mAdapter.notifyDataSetChanged();
        mAdapter.reset();
    }

    /**
     * 隐藏
     */
    public void hide() {
        if (mAreaChoicePopupwindow != null) {
            mAreaChoicePopupwindow.dismiss();
        }
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
     * 设置选择区域完成监听器
     * @param listener 监听器
     * @return Builder
     */
    public void setOnChooseCompleteListener(OnChooseCompleteListener listener) {
        this.mListener.mOnChooseCompleteListener = listener;
    }

    /**
     * 选择区域监听器
     * @param listener 监听器
     * @return Builder
     */
    public void setOnChooseListener(OnChooseListener listener) {
        this.mListener.mOnChooseListener = listener;
    }

    /**
     * 构建器类
     */
    public static class Builder {

        private Data mData = new Data();
        private Listener mListener = new Listener();

        public Builder(@NonNull Activity activity) {
            this.mData.mActivity = activity;
        }

        /**
         * 设置联动级别
         * @param level 级别
         * @return Builder
         */
        public Builder setLevel(@IntRange(from = PROVINCE, to = COUNTY) int level) {
            this.mData.mLevel = level;
            return this;
        }

        /**
         * 设置主题颜色
         * @param color 主题颜色
         * @return Builder
         */
        public Builder setThemeColor(@ColorInt int color) {
            this.mData.mColor = color;
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
    private static class Data {

        /** 联动级别 默认3级联动（省市县）*/
        private int mLevel = 3;
        /** 主题颜色 默认红色 */
        private int mColor = Color.parseColor("#FF0000");
        /** Context */
        private Activity mActivity;
        /** 省市县数据 */
        private List<Area> mAreaDataList;

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
