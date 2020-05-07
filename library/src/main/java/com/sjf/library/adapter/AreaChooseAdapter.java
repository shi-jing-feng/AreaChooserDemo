package com.sjf.library.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sjf.library.R;
import com.sjf.library.entity.Area;
import com.sjf.library.entity.City;
import com.sjf.library.entity.County;
import com.sjf.library.entity.Province;
import com.sjf.library.listener.OnItemEventListener;

import java.util.List;

import static com.sjf.library.constant.Constant.CLICK;
import static com.sjf.library.constant.Constant.COUNTY;
import static com.sjf.library.constant.Constant.CITY;
import static com.sjf.library.constant.Constant.PROVINCE;

/**
 * Function: 省市县区域选择适配器
 * Created by shijingfeng on 19-1-17.
 */
public class AreaChooseAdapter extends RecyclerView.Adapter<AreaChooseAdapter.AreaChooseViewHolder> {

    private Context mContext;
    private List<Area> mDataList;
    private OnItemEventListener mOnItemEventListener;

    /** 地区级别 */
    private int mAreaLevel = PROVINCE;
    /** 选中的省的位置 */
    private int mProvincePosition = -1;
    /** 选中的市的位置 */
    private int mCityPosition = -1;
    /** 选中的县的位置 */
    private int mCountyPosition = -1;

    public AreaChooseAdapter(@NonNull Context context, List<Area> dataList) {
        this.mContext = context;
        this.mDataList = dataList;
    }

    @Override
    @NonNull
    public AreaChooseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View rootView = LayoutInflater.from(mContext).inflate(R.layout.adapter_item_area_name, parent, false);

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

                currentPosition = mProvincePosition;
                holder.tvAreaName.setText(province.getName());
                break;
            case CITY:
                final City city = mDataList.get(position).getArea();

                currentPosition = mCityPosition;
                holder.tvAreaName.setText(city.getName());
                break;
            case COUNTY:
                final County county = mDataList.get(position).getArea();

                currentPosition = mCountyPosition;
                holder.tvAreaName.setText(county.getName());
                break;
            default:
                break;
        }

        //判断当前显示的位置是否是点击的位置
        if (currentPosition == position) {
            holder.tvAreaName.setTextColor(mContext.getResources().getColor(R.color.red));
            holder.ivAreaSelected.setVisibility(View.VISIBLE);
        } else {
            holder.tvAreaName.setTextColor(mContext.getResources().getColor(R.color.black));
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
                    final int previousProvincePosition = mProvincePosition;

                    mProvincePosition = position;
                    mCityPosition = -1;
                    mCountyPosition = -1;

                    if (previousProvincePosition != mProvincePosition) {
                        notifyItemChanged(mProvincePosition);
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
                    final int previousCityPosition = mCityPosition;

                    mCityPosition = position;
                    mCountyPosition = -1;

                    if (previousCityPosition != mCityPosition) {
                        notifyItemChanged(mCityPosition);
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
                    final int previousCountyPosition = mCountyPosition;

                    mCountyPosition = position;

                    if (previousCountyPosition != mCountyPosition) {
                        notifyItemChanged(mCountyPosition);
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
     * 重置
     */
    public void reset() {
        mAreaLevel = PROVINCE;
        mProvincePosition = -1;
        mCityPosition = -1;
        mCountyPosition = -1;
    }

    /**
     * 获取选中的省，市或县的位置
     * @param areaLevel  1 省的位置 2 市的位置 3 县的位置
     * @return 省，市或县的位置
     */
    public int getPosition(int areaLevel) {
        if (areaLevel == PROVINCE) {
            return mProvincePosition;
        } else if (areaLevel == CITY) {
            return mCityPosition;
        } else if (areaLevel == COUNTY) {
            return mCountyPosition;
        }
        return -1;
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
