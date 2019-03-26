package com.honghe.jetpacktest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;

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
                break;
            case R.id.sendMsg:
                MqttTest.publish(client, "hello");
                break;
        }
    }
}
