package com.honghe.jetpacktest;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.print.PrintManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.honghe.jetpacktest.Room.AppDatabase;
import com.honghe.jetpacktest.Room.User;
import com.honghe.jetpacktest.bean.UserBean;
import com.honghe.jetpacktest.dagger.Car;
import com.honghe.jetpacktest.dagger.DaggerMainActivityComponent;
import com.honghe.jetpacktest.dagger.Man;
import com.honghe.jetpacktest.databinding.ActivityMainBinding;
import com.honghe.jetpacktest.eventBus.MessageEvent;
import com.honghe.jetpacktest.lifecycle.Java8Observer;
import com.honghe.jetpacktest.livedata.MyLiveData;
import com.honghe.jetpacktest.livedata.NameViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
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
//dataBinding
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.tv.setText("init");
        UserBean userBean = new UserBean("zhangsan", 25);
        binding.setUser(userBean);
        List<String> list = new ArrayList<>();
        list.add("list1");
        list.add("list2");
        binding.setList(list);

        HashMap<String, Object> map = new HashMap<>();
        map.put("key0", "map_value0");
        map.put("key1", "map_value1");
        binding.setMap(map);

        String[] arrays = {"字符串1", "字符串2"};
        binding.setArray(arrays);
        tv = findViewById(R.id.tv);
//lifcycle
        getLifecycle().addObserver(new Java8Observer());
//viewModel
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
//                tv.setText(integer + "");
            }
        };
//livedata
        MyLiveData.getInstance(getApplicationContext()).observe(this, wifiObserver);

//room
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "database-name").build();
        new Thread(new Runnable() {
            @Override
            public void run() {
//                User user = new User();
//                user.setUid((int) SystemClock.currentThreadTimeMillis());
//                user.setFirstName("testFirstName");
//                user.setLastName("testLastName");
//                db.userDao().insertAll(user);
            }
        }).start();
//Dagger
        DaggerMainActivityComponent.create().inject(this);
    }

    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.btn:
                mModel.getCurrentName().setValue(man.getDefaultMan());//setValue用于主线程更新，postValue用于工作线程更新
                break;
            case R.id.getData:
                getData();
                break;
            case R.id.print:
                doPdfPrint("1.pdf");
                break;
        }
    }

    private void doPdfPrint(String filePath) {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        MyPrintPdfAdapter myPrintAdapter = new MyPrintPdfAdapter(MainActivity.this, filePath, true);
        printManager.print("jobName", myPrintAdapter, null);
    }


    public void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<User> userList = db.userDao().getAll();
                for (User data : userList) {
                    Log.e(TAG, "onCreate: " + data.getFirstName());
                }
                EventBus.getDefault().post(new MessageEvent("hello eventBus"));
            }
        }).start();
    }

    @Subscribe
    public void onMessageEvent(MessageEvent event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), event.message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}