package com.honghe.jetpacktest;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MyMqttCallback implements MqttCallback {
    @Override
    public void connectionLost(Throwable throwable) {
        Log.e("xxx", "connectionLost:" + throwable.getMessage());
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        Log.e("xxx", "消息主题=" + s + "=======接收消息Qos=" + mqttMessage.getQos() + "=======接受消息内容=" + new String(mqttMessage.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        Log.e("xxx", "deliveryComplete:" + iMqttDeliveryToken.isComplete());

    }
}
