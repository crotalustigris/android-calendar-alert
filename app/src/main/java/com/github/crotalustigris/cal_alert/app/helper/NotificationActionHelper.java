package com.github.crotalustigris.cal_alert.app.helper;

import android.content.Context;
import android.os.Handler;

import com.github.crotalustigris.cal_alert.u.ActionVo;
import com.github.crotalustigris.cal_alert.u.U;

import java.util.LinkedList;

/**
 * This class executes actions for an event from a list of actions
 */
public class NotificationActionHelper implements Runnable, MyTextSpeaker.MySpeakerDoneListener {
    private final Context ctx;
    private final LinkedList<ActionVo> actionVos;
    private MyTextSpeaker mySpeaker = null;

    /**
     * @param ctx - a context
     * @param actionVos - a list of actions to be executed
     */
    public NotificationActionHelper(Context ctx, LinkedList<ActionVo> actionVos) {
        this.ctx = ctx;
        this.actionVos = actionVos;
    }

    /**
     * Executes the next action in the list. On completion, this is run again.
     */
    public void run() {
        ActionVo action = actionVos.poll();
        if (action != null) {
            /*
             * Decode type of action object
             * */
            if (action instanceof ActionVo.SoundVo) {
                /*
                 * Play a sound resource. On completion, this will be run again
                 */
                ActionVo.SoundVo soundVo = (ActionVo.SoundVo) action;
                U.SC(this, "processing SoundVoundVo - delay:" + soundVo.delayMillis);
                (new Handler()).postDelayed(() ->
                                (new SoundPlayer()).play(ctx, soundVo.soundResId, this),
                        soundVo.delayMillis);
            } else if (action instanceof ActionVo.SpeakVo) {
                /*
                * Speak a text string. On completion, this will be run again
                */
                if (this.mySpeaker == null) {
                    this.mySpeaker = new MyTextSpeaker(ctx, this);
                }
                ActionVo.SpeakVo speakVo = (ActionVo.SpeakVo) action;
                U.SC(this, "processing SpeakVo - text:" + speakVo.text2Speak);
                (new Handler()).post(() -> (this.mySpeaker).speak(speakVo.text2Speak));
            }
        } else {
            /*
            * All done, get rid of the speaker object if we have one
            */
            U.SC(this, "No more ActionVOs to speak/sound");
            if (mySpeaker != null) {
                mySpeaker.onDestroy();
            }
        }
    }

    /**
     * Called when speech is done; then, runs this runnable object again,
     * to pick up the next action if any
     * @param utteranceId - the id of the utterance that finished
     */
    @Override
    public void onSpeechFinished(String utteranceId) {
        (new Handler()).post(this);
    }
}