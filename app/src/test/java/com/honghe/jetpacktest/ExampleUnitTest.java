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
        String thingUrl = "http://api.sp.yunjichina.com.cn/api/orders/new#a=b#list=[{\"good\" : \"5ca1bc914e9c4721b0002b21\",\"amount\" : 1}]&mobile=13521633047&positon=5b7e256ec63fc53416ee31af&positionTitle=101&store=5a56fb202b317855f59dd10d&user=5b190366220ded50ec560e92&isInvoices=false&payment=wechat#post";
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
            MqttHelper.getInstance().ConnectMqtt(MyApp.context, host).publish(topic, msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}