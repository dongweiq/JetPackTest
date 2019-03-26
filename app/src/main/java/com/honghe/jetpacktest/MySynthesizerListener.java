package com.honghe.jetpacktest;

import com.iflytek.cloud.SpeechError;

/**
 * Created by wanghh on 2017/7/13.
 */

public interface MySynthesizerListener {
    void onSpeakBegin();

    void onSpeakPaused();

    void onSpeakResumed();

    void onCompleted(SpeechError var1);
}
