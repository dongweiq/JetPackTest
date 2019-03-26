package com.honghe.jetpacktest;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

import java.io.IOException;

public class MqttTest {
    private static final String TAG = "whh";
    private static final String topic = "testtopic";

    public static MqttAndroidClient ConnectMqtt(Context context) {
        MqttAndroidClient client = new MqttAndroidClient(context,
                "ssl://172.81.231.145:8883", "wanghonghe");
//        MqttAndroidClient client = new MqttAndroidClient(context,
// "tcp://129.211.42.83:1883", "wanghonghe");
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {
                Log.e(TAG, "connectionLost: " + throwable.getMessage());
            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                Log.e(TAG, "messageArrived: " + s);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                try {
                    Log.e(TAG, "deliveryComplete: " + iMqttDeliveryToken.getMessage().toString());
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(false);
        mqttConnectOptions.setCleanSession(false);
        try {
            mqttConnectOptions.setSocketFactory(client.getSSLSocketFactory(
                    context.getAssets().open("client.bks"), "123456"));
        } catch (MqttSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            client.connect(mqttConnectOptions);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return client;
    }

    public static void publish(MqttAndroidClient client, String msg) {
        if (null != client) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MqttMessage mqttMessage = new MqttMessage(msg.getBytes());
                    try {
                        client.publish(topic, mqttMessage);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
