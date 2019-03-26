package com.honghe.jetpacktest;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Arrays;

public class Client {
    private static final String HOST = "ssl://129.211.42.83:8883";//服务器地址，自己搭建
    private String TOPIC;//订阅的主题
    private String CLIENT_ID;

    public Client(String clientId, String topic) {
        this.CLIENT_ID = clientId;
        this.TOPIC = topic;
    }

    private String username = "admin";
    private String password = "123456";

    public void start() {
        try {
            //MqttAsyncClient
            MqttClient mqttClient = new MqttClient(HOST, CLIENT_ID, new MemoryPersistence());
            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，设置为true表示每次连接到服务器都以新的身份连接
            mqttConnectOptions.setCleanSession(false);
//            mqttConnectOptions.setUserName(username);
//            mqttConnectOptions.setPassword(password.toCharArray());
            mqttConnectOptions.setConnectionTimeout(10);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            mqttConnectOptions.setKeepAliveInterval(20);
            mqttClient.setCallback(new MyMqttCallback());
            MqttTopic topic = mqttClient.getTopic(TOPIC);
            //setWill方法，如果项目中需要知道客户端是否掉线可以调用该方法。设置最终端口的通知消息
            //遗嘱
            // options.setWill(topic, "close".getBytes(), 2, true);
            mqttClient.connect(mqttConnectOptions);
            int[] Qos = {2};//消息等级最高2最低1
            String[] topic1 = {TOPIC};
            Log.e("xxx", "topic1[0]=" + topic1[0] + "Qos=" + Arrays.toString(Qos));
            mqttClient.subscribe(topic1, Qos);
        } catch (MqttException e) {
            Log.e("xxx", "createClientError" + e.getMessage());
            e.printStackTrace();
        }
    }
}
