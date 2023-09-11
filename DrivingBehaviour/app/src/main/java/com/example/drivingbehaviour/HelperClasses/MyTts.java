package com.example.drivingbehaviour.HelperClasses;

import android.content.Context;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.util.Locale;

public class MyTts{

    private final TextToSpeech tts;

    public MyTts(Context context)
    {
        TextToSpeech.OnInitListener initListener = new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                tts.setLanguage(Locale.ENGLISH);
            }
        };
        tts = new TextToSpeech(context, initListener);
    }

    public void speak(String message)
    {
        tts.speak(message, TextToSpeech.QUEUE_ADD, null, null);

    }

    public void stop(){

        tts.stop();
    }
}