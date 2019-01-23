package com.honghe.jetpacktest;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.honghe.jetpacktest.Room.AppDatabase;
import com.honghe.jetpacktest.Room.User;
import com.honghe.jetpacktest.dagger.Car;
import com.honghe.jetpacktest.dagger.DaggerMainActivityComponent;
import com.honghe.jetpacktest.dagger.Man;
import com.honghe.jetpacktest.lifecycle.Java8Observer;
import com.honghe.jetpacktest.livedata.MyLiveData;
import com.honghe.jetpacktest.livedata.NameViewModel;

import java.util.List;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private NameViewModel mModel;
    private TextView tv;
    @Inject
    Car car;
    @Inject
    Man man;

    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tv);
        getLifecycle().addObserver(new Java8Observer());
        mModel = ViewModelProviders.of(this).get(NameViewModel.class);
        final Observer<String> nameObserver = new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                tv.setText(s);
            }
        };
        mModel.getCurrentName().observe(this, nameObserver);
        final Observer<Integer> wifiObserver = new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                tv.setText(integer + "");
            }
        };
        MyLiveData.getInstance(getApplicationContext()).observe(this, wifiObserver);
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "database-name").build();
        DaggerMainActivityComponent.create().inject(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                User user = new User();
                user.setUid(111);
                user.setFirstName("testFirstName");
                user.setLastName("testLastName");
                db.userDao().insertAll(user);
            }
        }).start();

    }

    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.btn:
                mModel.getCurrentName().setValue(man.getDefaultMan());//setValue用于主线程更新，postValue用于工作线程更新
                break;
            case R.id.getData:
                getData();
                break;
        }
    }

    public void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<User> userList = db.userDao().getAll();
                for (User data : userList) {
                    Log.e(TAG, "onCreate: " + data.getFirstName());
                }
            }
        }).start();
    }

}
