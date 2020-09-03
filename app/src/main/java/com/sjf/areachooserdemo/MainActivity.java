package com.sjf.areachooserdemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.sjf.library.AreaChooser;
import com.sjf.library.entity.Area;
import com.sjf.library.entity.City;
import com.sjf.library.entity.County;
import com.sjf.library.entity.Province;
import com.sjf.library.listener.OnChooseListener;
import com.sjf.library.util.ResourceUtil;

import static com.sjf.library.constant.Constant.CITY;
import static com.sjf.library.constant.Constant.COUNTY;
import static com.sjf.library.constant.Constant.PROVINCE;

public class MainActivity extends AppCompatActivity {

    private AreaChooser mAreaChooser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_show).setOnClickListener(v -> {
            if (mAreaChooser != null) {
                mAreaChooser.show();
                return;
            }
            mAreaChooser = new AreaChooser.Builder(this)
                    .setLevel(CITY)
                    .setThemeColor(ResourceUtil.getColorById(R.color.red))
                    .setOnChooseListener((Area area) -> {
                        final Province province;
                        final City city;
                        final County county;

                        switch (area.getAreaLevel()) {
                            case PROVINCE:
                                province = area.getArea();
                                Log.e("测试","选择的区域: " + province.getName());
                                break;
                            case CITY:
                                city = area.getArea();
                                Log.e("测试","选择的区域: " + city.getName());
                                break;
                            case COUNTY:
                                county = area.getArea();
                                Log.e("测试","选择的区域: " + county.getName());
                                break;
                            default:
                                break;
                        }
                    })
                    .setOnChooseCompleteListener((AreaChooser areaChooser, @Nullable Province province, @Nullable City city, @Nullable County county) -> {
                        Log.e("测试", "省: " + (province == null ? "" : province.getName()));
                        Log.e("测试", "市: " + (city == null ? "" : city.getName()));
                        Log.e("测试", "县: " + (county == null ? "" : county.getName()));
                    })
                    .show();
        });
    }
}
