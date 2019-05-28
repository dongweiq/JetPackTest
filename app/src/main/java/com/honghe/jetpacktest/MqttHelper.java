package com.honghe.jetpacktest;

import android.content.Context;
import android.util.Log;


import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttHelper {
    private static final String TAG = "whh";
    private MqttAndroidClient mqttClient = null;
    private static MqttHelper instance = null;

    public static MqttHelper getInstance() {
        if (instance == null) {
            instance = new MqttHelper();
        }
        return instance;
    }

    public MqttHelper ConnectAndPublishMqtt(Context context, String host, String topic, String msg) {
        MqttAndroidClient client = new MqttAndroidClient(context,
                host, "wanghh");
        client.setCallback(new MqttCallbackExtended() {

            @Override
            public void connectComplete(boolean b, String s) {
                Log.e(TAG, "connectComplete: " + s);
                MqttMessage mqttMessage = new MqttMessage(msg.getBytes());
                try {
                    if (null != mqttClient) {
                        mqttClient.publish(topic, mqttMessage);
                    }
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void connectionLost(Throwable throwable) {
            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                Log.e(TAG, "messageArrived: " + s);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                try {
                    Log.e(TAG, "deliveryComplete: " + iMqttDeliveryToken.getMessage().toString());
                    if (mqttClient.isConnected()) {
                        mqttClient.disconnect();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(false);
        mqttConnectOptions.setCleanSession(false);
        try {
            client.connect(mqttConnectOptions);
        } catch (MqttException e) {
            e.printStackTrace();
        }
        mqttClient = client;
        return this;
    }

}
