package com.github.crotalustigris.cal_alert.app.helper;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import com.github.crotalustigris.cal_alert.u.U;

/**
 * Handles requests for the Android Text-To-Speech facility
 */
class MyTextSpeaker extends UtteranceProgressListener implements TextToSpeech.OnInitListener {

    private final TextToSpeech tts;
    private final MySpeakerDoneListener listener;
    private volatile boolean isInitialized = false;
    private String textToSpeak;

    /**
     * An interface for listening for the speech finished event for an utterace ID
     */
    public interface MySpeakerDoneListener {
        void onSpeechFinished(@SuppressWarnings("unused") String utteranceId);
    }

    /**
     * @param ctx - a context
     * @param listener - null, or a listener for speech done events
     */
    public MyTextSpeaker(Context ctx, MySpeakerDoneListener listener) {
        U.SC(this, "MyTextSpeaker()");
        this.listener = listener;
        tts = new TextToSpeech(ctx, this);
    }

    /*
     * Speak some text. 
     * ...If the speaker is already busy, queue it for later.
     */
    public void speak(String textToSpeak) {
        U.SC(this, "queueSpeech(" + textToSpeak + ")");
        this.textToSpeak = textToSpeak;
        tryToSpeak();
    }

    /*
     * Speak something if things are initialized.
     * ...Note that this will add it to the speaker's queue
     * ......so if it is already busy, things will still work
     */
    private void tryToSpeak() {
        U.SC(this, "tryToSpeak isInitialized=" + isInitialized);
        if (isInitialized) {
            U.SC(this, "Speaking:<" + textToSpeak + ">");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts.speak(textToSpeak, TextToSpeech.QUEUE_ADD, null, textToSpeak);
            } else {
                tts.speak(textToSpeak, TextToSpeech.QUEUE_ADD, null);
            }
        }
    }

    /*
     * Handle callback for when the TTS system is (or is not) initialized
     */
    @Override
    public void onInit(int status) {
        if (status != TextToSpeech.SUCCESS) {
            // Bad news - initialization failed
            // ...a serious app would do someting more than log this
            U.e("Text to Speech Initialization Failed!");
            if ( listener != null ) { listener.onSpeechFinished("NONE"); }
        } else {
            // Initialization worked. 
            // ...mark it initialized
            // ...then speak anything if it is enqueued
            U.SC(this, "onInit status is success");
            this.isInitialized = true;
            U.SC(this, "Result of setting onUtteranceProgressListener:"+tts.setOnUtteranceProgressListener(this));
            tryToSpeak();
        }
    }

    public void onDestroy() {
        U.SC(this, "onDestroy()");
        this.isInitialized = false;
        tts.shutdown();
    }

    @Override
    public void onStart(String utteranceId) {
        U.SC(this, "onStart(" + utteranceId + ")");
    }

    @Override
    public void onDone(String utteranceId) {
        U.SC(this, "onDone(" + utteranceId + ")");
        if ( listener != null ) { listener.onSpeechFinished(utteranceId); }
    }

    @Override
    public void onError(String utteranceId) {
        U.SC(this, "onError(" + utteranceId + ")");
        if ( listener != null ) { listener.onSpeechFinished(utteranceId); }
    }

}
