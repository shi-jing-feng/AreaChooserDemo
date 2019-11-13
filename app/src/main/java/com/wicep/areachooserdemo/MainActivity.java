package com.wicep.areachooserdemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.wicep.library.AreaChooser;
import com.wicep.library.entity.City;
import com.wicep.library.entity.County;
import com.wicep.library.entity.Province;
import com.wicep.library.listener.OnChooseCompleteListener;

import static com.wicep.library.constant.Constant.县;
import static com.wicep.library.constant.Constant.市;
import static com.wicep.library.constant.Constant.省;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_show).setOnClickListener(v -> {
            final AreaChooser areaChooser = new AreaChooser.Builder(this)
                    .setLevel(县)
                    .setOnChooseCompleteListener((@Nullable Province province, @Nullable City city, @Nullable County county) -> {
                        Log.e("测试", "省: " + (province == null ? "" : province.getName()));
                        Log.e("测试", "市: " + (city == null ? "" : city.getName()));
                        Log.e("测试", "县: " + (county == null ? "" : county.getName()));
                    })
                    .show();
        });
    }
}
