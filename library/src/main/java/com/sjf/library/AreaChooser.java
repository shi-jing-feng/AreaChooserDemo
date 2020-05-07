package com.wicep.library;

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

import com.wicep.library.adapter.AreaChooseAdapter;
import com.wicep.library.entity.Area;
import com.wicep.library.entity.City;
import com.wicep.library.entity.County;
import com.wicep.library.entity.Province;
import com.wicep.library.listener.OnChooseCompleteListener;
import com.wicep.library.listener.OnChooseListener;
import com.wicep.library.util.AreaUtil;
import com.wicep.library.util.WindowUtil;

import java.util.List;

import static com.wicep.library.constant.Constant.县;
import static com.wicep.library.constant.Constant.市;
import static com.wicep.library.constant.Constant.省;

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
    private int mAreaLevel = 省;

    public AreaChooser(@NonNull Data data, @NonNull Listener listener) {
        this.mData = data;
        this.mListener = listener;
        this.mAreaDataList = data.mAreaDataList;
        this.mParentView = data.mActivity.findViewById(android.R.id.content);
    }

    /**
     * 初始化处理收货地址选择区域点击事件
     */
    private void initAreaAction() {
        //设置为省级数据
        tvProvinceLabel.setOnClickListener(view -> {
            final List<Area> provinceList = mAreaDataList;

            setSelectedStatus(tvProvinceLabel);
            mAdapter.setData(provinceList, mAreaLevel = 省);
            mAdapter.notifyDataSetChanged();
        });
        //设置为市级数据
        tvCityLabel.setOnClickListener(view -> {
            final int provincePosition = mAdapter.getPosition(省);
            final Province province = mAreaDataList.get(provincePosition).getArea();
            final List<Area> cityList = province.getCityList();

            setSelectedStatus(tvCityLabel);
            mAdapter.setData(cityList, mAreaLevel = 市);
            mAdapter.notifyDataSetChanged();
        });
        //设置县级数据
        tvCountyLabel.setOnClickListener(view -> {
            final int provincePosition = mAdapter.getPosition(省);
            final int cityPosition = mAdapter.getPosition(市);
            final Province province = mAreaDataList.get(provincePosition).getArea();
            final City city = province.getCityList().get(cityPosition).getArea();
            final List<Area> countyList = city.getCountyList();

            setSelectedStatus(tvCountyLabel);
            mAdapter.setData(countyList, mAreaLevel = 县);
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
                case 省:
                    final Province province = (Province) data;
                    final List<Area> cityList = province.getCityList();

                    tvCountyLabel.setText("请选择");
                    tvCountyLabel.setVisibility(View.GONE);
                    tvProvinceLabel.setText(province.getName());

                    if (mData.mLevel == 省 || cityList == null || cityList.size() == 0) {
                        mListener.mOnChooseCompleteListener.onComplete(province, null, null);

                        mAreaChoicePopupwindow.dismiss();
                        return;
                    }

                    tvCityLabel.setText("请选择");
                    setSelectedStatus(tvCityLabel);
                    tvCityLabel.setVisibility(View.VISIBLE);

                    //更新数据
                    mAdapter.setData(province.getCityList(), mAreaLevel = 市);
                    mAdapter.notifyDataSetChanged();
                    break;
                case 市:
                    final City city = (City) data;
                    final List<Area> countyList = city.getCountyList();

                    tvCityLabel.setText(city.getName());

                    if (mData.mLevel == 市 || countyList == null || countyList.size() == 0) {
                        final int provincePosition = mAdapter.getPosition(省);
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
                    mAdapter.setData(countyList, mAreaLevel = 县);
                    mAdapter.notifyDataSetChanged();
                    break;
                case 县:
                    final County county = (County) data;

                    tvCountyLabel.setText(county.getName());

                    if (mListener.mOnChooseCompleteListener != null) {
                        final int provincePosition = mAdapter.getPosition(省);
                        final int cityPosition = mAdapter.getPosition(市);
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
        mAdapter.setData(mAreaDataList, mAreaLevel = 省);
        mAdapter.notifyDataSetChanged();
        mAdapter.reset();
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
     * 隐藏
     */
    public void hide() {
        if (mAreaChoicePopupwindow != null) {
            mAreaChoicePopupwindow.dismiss();
        }
    }

    /**
     * 设置省（用于初始化显示）
     * @param province 省
     */
    //TODO 功能待添加
    private void setArea(@NonNull Province province) {

    }

    /**
     * 设置省市（用于初始化显示）
     * @param province 省
     * @param city 市
     */
    //TODO 功能待添加
    private void setArea(@NonNull Province province, @NonNull City city) {

    }

    /**
     * 设置省市县（用于初始化显示）
     * @param province 省
     * @param city 市
     * @param county 县
     */
    //TODO 功能待添加
    private void setArea(@NonNull Province province, @NonNull City city, @NonNull County county) {

    }

    /**
     * 获取指定级别的数据
     * @param areaLevel 指定级别
     * @return 数据
     */
    public List<Area> getAreaDataList(int areaLevel) {
        List<Area> areaList = null;

        switch (areaLevel) {
            case 省:
                areaList = mAreaDataList;
                break;
            case 市:
                final int provincePosition1 = mAdapter.getPosition(省);

                if (provincePosition1 > -1) {
                    final Province province = mAreaDataList.get(provincePosition1).getArea();

                    areaList = province.getCityList();
                }
                break;
            case 县:
                final int provincePosition2 = mAdapter.getPosition(省);

                if (provincePosition2 > -1) {
                    final Province province = mAreaDataList.get(provincePosition2).getArea();
                    final int cityPosition = mAdapter.getPosition(市);

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
        public Builder setLevel(@IntRange(from = 省, to = 县) int level) {
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
        private int mLevel = 县;
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
