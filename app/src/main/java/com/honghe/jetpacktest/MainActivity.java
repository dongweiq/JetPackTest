package com.honghe.jetpacktest;

import android.app.Application;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.SystemClock;
import android.print.PrintManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
import com.tr.officelib.callback.OfficeLibInitOfficeCallBack;
import com.tr.officelib.callback.OfficeLibOpenFileCallBack;
import com.tr.officelib.util.OfficeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    String fileName = "1.pdf";
    String path = Environment.getExternalStorageDirectory().getPath() + "/" + fileName;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            OfficeUtil.getInstance().init((Application) getApplicationContext(), new OfficeLibInitOfficeCallBack() {
                @Override
                public void initOk() {
                    super.initOk();
                    Log.e(TAG, "initOk: ");
                }

                @Override
                public void initFail() {
                    super.initFail();
                    Log.e(TAG, "initFail: ");
                }

                @Override
                public void init(int progress) {
                    super.init(progress);
                    Log.e(TAG, "init: ");
                }

                @Override
                public void initDf() {
                    super.initDf();
                    Log.e(TAG, "initDf: ");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        //print
        webView = binding.webView;
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        webSettings.setAppCacheEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        binding.webView.loadUrl(binding.etUrl.getText().toString());
        binding.etUrl.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED || actionId ==
                        EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() ==
                        KeyEvent.KEYCODE_ENTER)) {
                    binding.webView.loadUrl(binding.etUrl.getText().toString());
                }
                return true;
            }
        });
        copyFile(fileName, path);
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
                PrintHelper.printAssetPDF(MainActivity.this, "1.pdf");
                break;
            case R.id.printBmp:
                PrintHelper.printBmp(MainActivity.this, getBitmap());
                break;
            case R.id.printWebView:
                PrintHelper.printWebView(MainActivity.this, webView);
                break;
            case R.id.printX5WebView:
                try {
                    OfficeUtil.getInstance().openLocalFile(MainActivity.this, path, "test", true, new OfficeLibOpenFileCallBack() {
                        @Override
                        public void canOpen() {
                            super.canOpen();
                        }

                        @Override
                        public void failOpen(String failMsg) {
                            super.failOpen(failMsg);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.startDraw:
                startActivity(new Intent(this, DrawActivity.class));
                break;
        }
    }


    private Bitmap getBitmap() {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getAssets().open("xingkong.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public void copyFile(String from, String to) {
        Log.e(TAG, "copyFile: Path\t" + to);
        try {
            int bytesum = 0;
            int byteread = 0;
            File toFile = new File(to);
            InputStream inStream = getResources().getAssets().open(from);//将assets中的内容以流的形式展示出来
            if (toFile.exists()) {
                toFile.delete();
            }
            OutputStream fs = new BufferedOutputStream(new FileOutputStream(toFile));//to为要写入sdcard中的文件名称
            byte[] buffer = new byte[1024];
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread;
                fs.write(buffer, 0, byteread);
            }
            inStream.close();
            fs.close();
        } catch (Exception e) {
            Log.e(TAG, "copyFile: failed");
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
