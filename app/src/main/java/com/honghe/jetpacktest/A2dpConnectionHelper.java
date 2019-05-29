package com.honghe.jetpacktest;

import android.app.Activity;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class A2dpConnectionHelper implements BluetoothProfile.ServiceListener {
    private BluetoothA2dp mA2dp;
    private BluetoothAdapter mBtAdapter;
    public static A2dpConnectionHelper instance;
    private Activity activity;
    private BluetoothDevice lastBluetoothDevice;

    public static A2dpConnectionHelper getInstance() {
        if (null == instance) {
            instance = new A2dpConnectionHelper();
        }
        return instance;
    }


    public A2dpConnectionHelper init(Activity context) {
        if (null == this.activity) {
            this.activity = context;
            mBtAdapter = BluetoothAdapter.getDefaultAdapter();
            if (!mBtAdapter.isEnabled()) {
                //弹出对话框提示用户是后打开
                Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                context.startActivityForResult(enabler, 1);
            }
//获取A2DP代理对象
            mBtAdapter.getProfileProxy(context, this, BluetoothProfile.A2DP);
        }
        return this;
    }

    public void setLastBluetoothDevice(BluetoothDevice device) {
        this.lastBluetoothDevice = device;
    }

    public BluetoothDevice getLastBluetoothDevice() {
        return this.lastBluetoothDevice;
    }

    public void connectA2dp(BluetoothDevice device) {
        if (null != lastBluetoothDevice) {
            disConnectA2dp(lastBluetoothDevice);
        }
        setPriority(device, 100); //设置priority
        try {
            //通过反射获取BluetoothA2dp中connect方法（hide的），进行连接。
            Method connectMethod = BluetoothA2dp.class.getMethod("connect",
                    BluetoothDevice.class);
            connectMethod.invoke(mA2dp, device);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disConnectA2dp(BluetoothDevice device) {
        if (null != device) {
            setPriority(device, 0);
            try {
                //通过反射获取BluetoothA2dp中connect方法（hide的），断开连接。
                Method connectMethod = BluetoothA2dp.class.getMethod("disconnect",
                        BluetoothDevice.class);
                connectMethod.invoke(mA2dp, device);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setPriority(BluetoothDevice device, int priority) {
        if (mA2dp == null) return;
        try {//通过反射获取BluetoothA2dp中setPriority方法（hide的），设置优先级
            Method connectMethod = BluetoothA2dp.class.getMethod("setPriority",
                    BluetoothDevice.class, int.class);
            connectMethod.invoke(mA2dp, device, priority);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getPriority(BluetoothDevice device) {
        int priority = 0;
        if (mA2dp == null) return priority;
        try {//通过反射获取BluetoothA2dp中getPriority方法（hide的），获取优先级
            Method connectMethod = BluetoothA2dp.class.getMethod("getPriority",
                    BluetoothDevice.class);
            priority = (Integer) connectMethod.invoke(mA2dp, device);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return priority;
    }

    public ArrayList<BluetoothDevice> searchDevices() {
        if (mBtAdapter == null) {
            init(activity);
        }
        ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<>();
        Set<BluetoothDevice> bondDevices = mBtAdapter.getBondedDevices();
        for (BluetoothDevice device : bondDevices) {
//            if (device.getBluetoothClass().getMajorDeviceClass() == BluetoothClass.Device.Major.AUDIO_VIDEO) {
            bluetoothDevices.add(device);
//            }
        }
        return bluetoothDevices;
    }


    @Override
    public void onServiceConnected(int profile, BluetoothProfile proxy) {
        if (profile == BluetoothProfile.A2DP) {
            mA2dp = (BluetoothA2dp) proxy; //转换
        }
    }

    @Override
    public void onServiceDisconnected(int profile) {
        if (profile == BluetoothProfile.A2DP) {
            mA2dp = null;
        }
    }
}
