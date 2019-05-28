package com.honghe.jetpacktest;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class A2dpConnectActivity extends AppCompatActivity {
    private static final String TAG = "A2dpConnectActivity";
    private RecyclerView recyclerView;
    private BluetoothDevicesAdapter bluetoothDevicesAdapter;
    private TextView tv_state;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a2dp_connect);
        tv_state = findViewById(R.id.tv_state);
        recyclerView = findViewById(R.id.rv_bluetooth_devices);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        bluetoothDevicesAdapter = new BluetoothDevicesAdapter(this);
        recyclerView.setAdapter(bluetoothDevicesAdapter);
        //注册广播接收者监听状态改变
        IntentFilter filter = new IntentFilter(BluetoothA2dp.
                ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
    }

    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.btn_open:
                A2dpConnectionHelper.getInstance().init(A2dpConnectActivity.this);
                break;
            case R.id.btn_scan:
                bluetoothDevicesAdapter.setData(A2dpConnectionHelper.getInstance().searchDevices());
                break;
            case R.id.btn_stop_scan:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "onReceive action=" + action);
            String statStr = "状态:";
            //A2DP连接状态改变
            if (action.equals(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                statStr += device.getName() + "\t" + device.getAddress();
                int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, BluetoothA2dp.STATE_DISCONNECTED);
                switch (state) {
                    case BluetoothA2dp.STATE_CONNECTED:
                        statStr += "已连接";
                        break;
                    case BluetoothA2dp.STATE_CONNECTING:
                        statStr += "连接中";
                        break;
                    case BluetoothA2dp.STATE_DISCONNECTED:
                        statStr += "断开连接";
                        break;
                    case BluetoothA2dp.STATE_DISCONNECTING:
                        statStr += "正在断开连接";
                        break;
                }
                tv_state.setText(statStr);
                Log.i(TAG, "connect state=" + state);
            } else if (action.equals(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED)) {
                //A2DP播放状态改变
                int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, BluetoothA2dp.STATE_NOT_PLAYING);
                Log.i(TAG, "play state=" + state);
            }
        }
    };
}
