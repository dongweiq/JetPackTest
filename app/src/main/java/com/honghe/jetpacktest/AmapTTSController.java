package com.honghe.jetpacktest;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;

import java.util.LinkedList;

/**
 * 当前DEMO的播报方式是队列模式。其原理就是依次将需要播报的语音放入链表中，播报过程是从头开始依次往后播报。
 * <p>
 * 导航SDK原则上是不提供语音播报模块的，如果您觉得此种播报方式不能满足你的需求，请自行优化或改进。
 */
public class AmapTTSController implements AudioManager.OnAudioFocusChangeListener {
    private static final String TAG = AmapTTSController.class.getName();
    public static AmapTTSController ttsManager;
    private Context mContext;
    private SpeechSynthesizer mTts;
    private boolean isPlaying = false;
    private String voicer = "xiaoyan";
    private LinkedList<String> wordList = new LinkedList<String>();
    private final int TTS_PLAY = 1;
    private final int CHECK_TTS_PLAY = 2;
    private boolean isNeedStartTimer = false;
    private MySynthesizerListener mMySynthesizerListener;
    private AudioManager mAm = null;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TTS_PLAY:
                    synchronized (mTts) {
                        if (!isPlaying && mTts != null && wordList.size() > 0) {
                            isPlaying = true;
                            String playtts = wordList.removeFirst();
                            if (mTts == null) {
                                createSynthesizer();
                            }
                            playtts = playtts.replace("长按", "长[=chang2]按").replace("京藏", "京藏[=zang4]").replace("上地", "上地[=di4]").replace("目的地", "目的地[=di4]").replace("事故多发地", "事故多发地[=di4]");
                            mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "false");
                            mAm.requestAudioFocus(AmapTTSController.this,
                                    AudioManager.STREAM_MUSIC,
                                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
                            mTts.startSpeaking(playtts, new SynthesizerListener() {
                                @Override
                                public void onCompleted(SpeechError speechError) {
                                    isPlaying = false;
//                                    AMapNavi.setTtsPlaying(isPlaying );
//                                    if (wordList.size() < 1) {
                                    if (mAm != null) {
                                        mAm.abandonAudioFocus(AmapTTSController.this);
                                    }
                                    if (null != mMySynthesizerListener) {
                                        mMySynthesizerListener.onCompleted(speechError);
                                    }
//                                    }
                                    handler.obtainMessage(1).sendToTarget();
                                }

                                @Override
                                public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
                                }

                                @Override
                                public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
                                    // 合成进度
                                    isPlaying = true;
                                }

                                @Override
                                public void onSpeakBegin() {
                                    //开始播放
                                    isPlaying = true;
//                                    AMapNavi.setTtsPlaying(isPlaying );
                                    if (null != mMySynthesizerListener) {
                                        mMySynthesizerListener.onSpeakBegin();
                                    }
                                }

                                @Override
                                public void onSpeakPaused() {
                                    if (null != mMySynthesizerListener) {
                                        mMySynthesizerListener.onSpeakPaused();
                                    }
                                }

                                @Override
                                public void onSpeakProgress(int arg0, int arg1, int arg2) {
                                    //播放进度
                                    isPlaying = true;
                                }

                                @Override
                                public void onSpeakResumed() {
                                    //继续播放
                                    isPlaying = true;
                                    if (null != mMySynthesizerListener) {
                                        mMySynthesizerListener.onSpeakResumed();
                                    }
                                }
                            });
                        }
                    }
                    break;
                case CHECK_TTS_PLAY:
                    if (!isPlaying) {
                        handler.obtainMessage(1).sendToTarget();
                    }
                    break;
            }

        }
    };

    private AmapTTSController(Context context) {
        this.mContext = context.getApplicationContext();
        this.mAm = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    private void createSynthesizer() {
        mTts = SpeechSynthesizer.createSynthesizer(mContext,
                new InitListener() {
                    @Override
                    public void onInit(int errorcode) {
                        if (ErrorCode.SUCCESS == errorcode) {
                        } else {
                            Toast.makeText(mContext, "语音合成初始化失败!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void init() {
        if (mTts == null) {
            createSynthesizer();
        }
        voicer = "xiaoyan";
        if (voicer.equals("xiaoyan") || voicer.equals("xiaofeng")) {
            //离线发音人，使用本地引擎
            //设置使用本地引擎
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            //设置发音人资源路径
            mTts.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath(voicer));
            //设置发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
        } else {
            //在线发音人，设置使用云端引擎
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            // 设置发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME,
                    voicer);
        }
//        // 设置语速
//        mTts.setParameter(SpeechConstant.SPEED,
//                SharedPreferencesUtil.getInstance(mContext).getTtsSpeed());
//        // 设置音量
//        mTts.setParameter(SpeechConstant.VOLUME,
//                SharedPreferencesUtil.getInstance(mContext).getTtsVolume());
//        //设置情感
//        mTts.setParameter(SpeechConstant.EMOT, "happy");
//        // 设置语调
//        mTts.setParameter(SpeechConstant.PITCH,
//                SharedPreferencesUtil.getInstance(mContext).getTtsPitch());
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
    }

    public static AmapTTSController getInstance(Context context) {
        if (ttsManager == null) {
            ttsManager = new AmapTTSController(context);
        }
        return ttsManager;
    }

    public void stopSpeaking() {
        if (wordList != null) {
            wordList.clear();
        }
        if (mTts != null) {
            mTts.stopSpeaking();
        }
        isPlaying = false;
    }

    public void destroy() {
        if (wordList != null) {
            wordList.clear();
        }
        if (mTts != null) {
            mTts.destroy();
        }
    }

    public boolean isSpeaking() {
        Log.d(TAG, "isSpeaking() called");
        return isPlaying;
    }

    public void playText(String text, boolean isSingleNumber) {
        setNumberSingleRead(isSingleNumber);
        if (wordList != null)
            wordList.addLast(text);
        handler.obtainMessage(CHECK_TTS_PLAY).sendToTarget();
    }

    public void playText(String text) {
        Log.d(TAG, "playText() called with: text = [" + text + "]");
        playText(text, false);
    }

    public void orderPlayText(String text) {
        Log.e(TAG, "orderPlayText() called with: text = [" + text + "]");
        playText(text);
    }

    public void weakPlayText(String text) {
        Log.e(TAG, "weakPlayText() called with: text = [" + text + "]");
        if (!isPlaying) {
            playText(text);
        }
    }

    public void weakPlayText(String text, boolean isSingleNumber) {
        Log.e(TAG, "weakPlayText() called with: text = [" + text + "], isSingleNumber = [" + isSingleNumber + "]");
        if (!isPlaying) {
            playText(text, isSingleNumber);
        }
    }

    public void setNumberSingleRead(boolean bool) {
        Log.d(TAG, "setNumberSingleRead() called with: bool = [" + bool + "]");
        if (null != mTts) {
            if (bool) {
                mTts.setParameter(SpeechConstant.PARAMS, "rdn=2");
            } else {
                mTts.setParameter(SpeechConstant.PARAMS, "rdn=1");
            }
        }
    }

    public void setFinish() {
        Log.d(TAG, "setFinish() called");
        isPlaying = false;
    }

    public boolean isNeedStartTimer() {
        Log.e(TAG, "isNeedStartTimer() called");
        return isNeedStartTimer;
    }

    public void setNeedStartTimer(boolean bool) {
        Log.e(TAG, "setNeedStartTimer() called with: bool = [" + bool + "]");
        this.isNeedStartTimer = bool;
    }

    public void setMySynthesizerListener(MySynthesizerListener listener) {
        this.mMySynthesizerListener = listener;
    }

    //获取发音人资源路径
    private String getResourcePath(String voicer) {
        Log.d(TAG, "getResourcePath() called with: voicer = [" + voicer + "]");
        StringBuffer tempBuffer = new StringBuffer();
        //合成通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, "tts/common.jet"));
        tempBuffer.append(";");
        //发音人资源
        tempBuffer.append(ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, "tts/" + voicer + ".jet"));
        return tempBuffer.toString();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

    }
}
