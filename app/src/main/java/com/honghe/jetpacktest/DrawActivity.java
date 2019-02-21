package com.honghe.jetpacktest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.honghe.jetpacktest.DoodleView.DoodleView;

public class DrawActivity extends AppCompatActivity {
    private static final String TAG = DrawActivity.class.getName();
    private DoodleView mDoodleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        mDoodleView = findViewById(R.id.doodleView);
        mDoodleView.setSize(dip2px(5));
    }

    private int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.clear:
                mDoodleView.reset();
                break;
            case R.id.savebmp:
                String path = mDoodleView.saveBitmap(mDoodleView);// 可以用mDoodleView.getBitmap()来获取图片
                Log.e(TAG, "saveBitmap: " + path);
                break;
        }
    }
}
