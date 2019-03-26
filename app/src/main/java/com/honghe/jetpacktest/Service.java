package com.honghe.jetpacktest;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

class Service {
    private static final String HOST = "ssl://129.211.42.83:8883";
    private static final String TOPIC = "testtopic";//发布到那个主题，所有订阅该主题的客户端都能收到
    private String CLIENT_ID;

    private MqttMessage message;

    private MqttClient client;
    private MqttTopic topic11;
    private Service service = null;


    Service(String clientId) throws MqttException {
        this.CLIENT_ID = clientId;
        client = new MqttClient(HOST, clientId, new MemoryPersistence());
        connect();
    }

    private void connect() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(false);
//        String username = "admin";
//        options.setUserName(username);
//        String password = "123456";
//        options.setPassword(password.toCharArray());
        // 设置超时时间
        options.setConnectionTimeout(20);
        // 设置会话心跳时间
        options.setKeepAliveInterval(10);
        try {
            client.setCallback(new MyMqttCallback());
            client.connect(options);
            //创建MQTT相关的主题
            topic11 = client.getTopic(TOPIC);
            Log.e("xxx", "topic11=" + topic11);
        } catch (Exception e) {
            Log.e("xxx", e.getMessage());
            e.printStackTrace();
        }
    }

    private void publish(MqttTopic topic, MqttMessage mqttMessage) throws MqttException {
        Log.e("xxx", "topic=" + topic);
        MqttDeliveryToken token = topic.publish(mqttMessage);
        token.waitForCompletion();
        Log.e("xxx", "消息发布成功" + token.isComplete());
    }

    void startService() {
        new Thread(() -> {
            Service service;
            try {
                service = new Service(CLIENT_ID);
                service.message = new MqttMessage();
                service.message.setQos(2);
                //设置是否在服务器中保存消息体
                service.message.setRetained(true);
                int cc = 0;
                //noinspection InfiniteLoopStatement
                while (true) {
                    cc++;
                    String s = "这是推送消息的内容" + cc;
                    Log.e("xxx", "准备发送消息" + s);
                    service.message.setPayload(s.getBytes());
                    Log.e("xxx", "service=" + service + "message=" + service.message);
                    service.publish(service.topic11, service.message);
                    Log.e("xxx", "Retained状态=" + service.message.isRetained());
                    Thread.sleep(3000);
                }
            } catch (MqttException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
