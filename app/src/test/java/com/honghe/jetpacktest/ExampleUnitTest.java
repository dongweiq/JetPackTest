package com.honghe.jetpacktest;

import android.text.TextUtils;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void Testbool() {
        boolean bool = true;
        boolean bool2 = false;
        System.out.println(bool + "\t" + bool2);
    }

    @Test
    public void TestThingUrl() {
//        String thingUrl = "http://api.mp.yunjichina.com.cn/openapi/robots/actions/call#a=b#productId=WATER-C1Q5-00219&robotType=WATER&target=charge_port&type=guide&commander=YUNFAN-M2H3-00018#post";
//        String thingUrl = "http://wt31447-49375089.monitor.yunjichina.com.cn:9001/api/move#a=b#markers=w001,w002,w003,w004,w005,w006,w007,w008,w009&count=1#get";
        String thingUrl = "http://api.mp.yunjichina.com.cn/openapi/robots/actions/call#a=b#productId=WATER-C1Q5-00219&robotType=WATER&target=w102&type=guide&commander=YUNFAN-M2H3-00018#post";
        httpThingConrol(thingUrl);

    }

    public void httpThingConrol(String thingUrl) {
        String[] splitUrl = thingUrl.split("#");
        String baseUrl = null;
        String funUrl = null;
        String httpMethod = null;
        String headerurl = null;
        Map<String, String> headerParamsMaps = null;
        Map<String, String> bodyParamsMaps = null;

        try {
            baseUrl = splitUrl[0];
            headerurl = splitUrl[1];
            funUrl = splitUrl[2].replace("\\", "");
            httpMethod = splitUrl[3];

            bodyParamsMaps = new HashMap<String, String>();
            headerParamsMaps = new HashMap<String, String>();

//            if (!TextUtils.isEmpty(funUrl)) {
            String[] bodyParms = funUrl.split("&");
            String[] headerParms = headerurl.split("&");
            for (int i = 0; i < bodyParms.length; i++) {
                String[] param = bodyParms[i].split("=");
                if (param.length >= 2) {
                    bodyParamsMaps.put(param[0], param[1]);
                }
            }
            for (int i = 0; i < headerParms.length; i++) {
                String[] param = headerParms[i].split("=");
                if (param.length >= 2) {
                    headerParamsMaps.put(param[0], param[1]);
                }
            }
//            }

            if (httpMethod.equals("post")) {
                System.out.println("baseUrl\t" + baseUrl + "\nheaderParamsMaps\t" + headerParamsMaps + "\n bodyParamsMaps" + bodyParamsMaps);
                System.out.println("headerParamsMaps \t");
                for (String key : headerParamsMaps.keySet()) {
                    String value = headerParamsMaps.get(key);
                    System.out.println(key + ":" + value);
                }
                System.out.println("bodyParamsMaps \t");
                for (String key : bodyParamsMaps.keySet()) {
                    String value = bodyParamsMaps.get(key);
                    System.out.println(key + ":" + value);
                }


            } else {
                String url = baseUrl + "?" + funUrl;
                System.out.println("get url\t" + url + headerParamsMaps);
                System.out.println("headerParamsMaps \t");
                for (String key : headerParamsMaps.keySet()) {
                    String value = headerParamsMaps.get(key);
                    System.out.println(key + ":" + value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("错误的素材的物联网址：" + thingUrl);
        }
    }

    @Test
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
            MqttHelper.getInstance().ConnectAndPublishMqtt(MyApp.context, host, topic, msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}