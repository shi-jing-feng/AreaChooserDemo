package com.shijingfeng.area_chooser.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shijingfeng.area_chooser.AreaChooser;
import com.sjf.library.R;
import com.shijingfeng.area_chooser.entity.Area;
import com.shijingfeng.area_chooser.entity.City;
import com.shijingfeng.area_chooser.entity.County;
import com.shijingfeng.area_chooser.entity.Province;
import com.shijingfeng.area_chooser.listener.OnItemEventListener;

import java.util.List;

import static com.shijingfeng.area_chooser.constant.Constant.CLICK;
import static com.shijingfeng.area_chooser.constant.Constant.COUNTY;
import static com.shijingfeng.area_chooser.constant.Constant.CITY;
import static com.shijingfeng.area_chooser.constant.Constant.PROVINCE;

/**
 * Function: 省市县区域选择适配器
 * Created by shijingfeng on 19-1-17.
 */
public class AreaChooseAdapter extends RecyclerView.Adapter<AreaChooseAdapter.AreaChooseViewHolder> {

    private Context mContext;
    private AreaChooser.Data mData;
    private List<Area> mDataList;
    private OnItemEventListener mOnItemEventListener;

    /** 地区级别 */
    private int mAreaLevel = PROVINCE;
    /** 选中的省的位置 */
    private int mCurProvincePosition = -1;
    /** 选中的市的位置 */
    private int mCurCityPosition = -1;
    /** 选中的县的位置 */
    private int mCurCountyPosition = -1;

    public AreaChooseAdapter(Context context, @NonNull AreaChooser.Data data, List<Area> dataList) {
        this.mContext = context;
        this.mData = data;
        this.mDataList = dataList;
    }

    @Override
    @NonNull
    public AreaChooseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View rootView = LayoutInflater.from(mData.activity).inflate(R.layout.adapter_item_area_name, parent, false);

        return new AreaChooseViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull final AreaChooseViewHolder holder, int position) {
        initData(holder, position);
        initAction(holder, position);
    }

    /**
     * 初始化数据
     * @param holder ViewHolder
     * @param position 位置
     */
    private void initData(final AreaChooseViewHolder holder, final int position) {
        int currentPosition = 0;

        switch (mAreaLevel) {
            case PROVINCE:
                final Province province = mDataList.get(position).getArea();

                currentPosition = mCurProvincePosition;
                holder.tvAreaName.setText(province.getName());
                break;
            case CITY:
                final City city = mDataList.get(position).getArea();

                currentPosition = mCurCityPosition;
                holder.tvAreaName.setText(city.getName());
                break;
            case COUNTY:
                final County county = mDataList.get(position).getArea();

                currentPosition = mCurCountyPosition;
                holder.tvAreaName.setText(county.getName());
                break;
            default:
                break;
        }

        //判断当前显示的位置是否是点击的位置
        if (currentPosition == position) {
            holder.tvAreaName.setTextColor(mData.color);
            holder.ivAreaSelected.setColorFilter(mData.color);
            holder.ivAreaSelected.setVisibility(View.VISIBLE);
        } else {
            holder.tvAreaName.setTextColor(Color.BLACK);
            holder.ivAreaSelected.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化事件
     * @param holder ViewHolder
     * @param position 位置
     */
    private void initAction(final AreaChooseViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(view -> {
            switch (mAreaLevel) {
                case PROVINCE:
                    final Province province = mDataList.get(position).getArea();
                    final int previousProvincePosition = mCurProvincePosition;

                    mCurProvincePosition = position;
                    mCurCityPosition = -1;
                    mCurCountyPosition = -1;

                    if (previousProvincePosition != mCurProvincePosition) {
                        notifyItemChanged(mCurProvincePosition);
                        if (previousProvincePosition != -1) {
                            notifyItemChanged(previousProvincePosition);
                        }
                    }
                    if (mOnItemEventListener != null) {
                        mOnItemEventListener.onItemEvent(view, province, position, CLICK);
                    }
                    break;
                case CITY:  //市
                    final City city = mDataList.get(position).getArea();
                    final int previousCityPosition = mCurCityPosition;

                    mCurCityPosition = position;
                    mCurCountyPosition = -1;

                    if (previousCityPosition != mCurCityPosition) {
                        notifyItemChanged(mCurCityPosition);
                        if (previousCityPosition != -1) {
                            notifyItemChanged(previousCityPosition);
                        }
                    }
                    if (mOnItemEventListener != null) {
                        mOnItemEventListener.onItemEvent(view, city, position, CLICK);
                    }
                    break;
                case COUNTY:  //县
                    final County county = mDataList.get(position).getArea();
                    final int previousCountyPosition = mCurCountyPosition;

                    mCurCountyPosition = position;

                    if (previousCountyPosition != mCurCountyPosition) {
                        notifyItemChanged(mCurCountyPosition);
                        if (previousCountyPosition != -1) {
                            notifyItemChanged(previousCountyPosition);
                        }
                    }
                    if (mOnItemEventListener != null) {
                        mOnItemEventListener.onItemEvent(view, county, position, CLICK);
                    }
                    break;
                default:
                    break;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mDataList == null) {
            return 0;
        }
        return mDataList.size();
    }

    /**
     * 设置数据
     * @param areaData 区域数据
     * @param areaLevel 区域级别
     */
    public void setData(List<Area> areaData, int areaLevel) {
        this.mDataList = areaData;
        this.mAreaLevel = areaLevel;
    }

    /**
     * 设置 省市县 的位置
     * @param provincePosition 选中的 省 位置
     * @param cityPosition     选中的 市 位置
     * @param countyPosition   选中的 县 位置
     */
    public void setPosition(int provincePosition, int cityPosition, int countyPosition) {
        this.mCurProvincePosition = provincePosition;
        this.mCurCityPosition = cityPosition;
        this.mCurCountyPosition = countyPosition;
    }

    /**
     * 获取选中的省，市或县的位置
     * @param areaLevel  1 省的位置 2 市的位置 3 县的位置
     * @return 省，市或县的位置
     */
    public int getPosition(int areaLevel) {
        if (areaLevel == PROVINCE) {
            return mCurProvincePosition;
        } else if (areaLevel == CITY) {
            return mCurCityPosition;
        } else if (areaLevel == COUNTY) {
            return mCurCountyPosition;
        }
        return -1;
    }

    /**
     * 重置
     */
    public void reset() {
        mAreaLevel = PROVINCE;
        mCurProvincePosition = -1;
        mCurCityPosition = -1;
        mCurCountyPosition = -1;
    }

    /**
     * 设置事件回调监听器
     * @param listener 回调监听器
     */
    public void setOnItemEventListener(OnItemEventListener listener) {
        this.mOnItemEventListener = listener;
    }

    static class AreaChooseViewHolder extends RecyclerView.ViewHolder {

        private TextView tvAreaName;
        private ImageView ivAreaSelected;

        private AreaChooseViewHolder(View itemView) {
            super(itemView);
            tvAreaName = itemView.findViewById(R.id.tv_area_name);
            ivAreaSelected = itemView.findViewById(R.id.iv_area_selected);
        }

    }

}
