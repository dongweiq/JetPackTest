package com.honghe.jetpacktest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.eclipse.paho.android.service.MqttAndroidClient;

public class MqttActivity extends AppCompatActivity {

    MqttAndroidClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mqtt);
    }

    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.connect:
                client = MqttTest.ConnectMqtt(MqttActivity.this);
                break;
            case R.id.subscribe:
                mqttTest();
                break;
            case R.id.sendMsg:
                MqttTest.publish("test", "hello");
                break;
        }
    }

    public void mqttTest() {
        String mqttString = "mqtt#ssl://emqssl.yunjichina.com.cn:8883#tvplayer#start";
        mqttSend(mqttString);
    }

    public void mqttSend(String thingUrl) {//mqtt#ssl://emqssl.yunjichina.com.cn:8883#playTV#start
        String[] mqttUrl = thingUrl.split("#");
        try {
            String host = mqttUrl[1];
            String topic = mqttUrl[2];
            String msg = mqttUrl[3];
            Log.e("whh", "mqttSend: ");
            MqttHelper.getInstance().ConnectAndPublishMqtt(MyApp.context, host, topic, msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
