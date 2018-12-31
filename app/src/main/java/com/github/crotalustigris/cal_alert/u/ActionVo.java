package com.github.crotalustigris.cal_alert.u;

/**
 * Contains variables to determine required actions after receiving an event
 * ... this is a superclass. Each subclass is for one kind of event.
 * ... subclasses are below also.
 */
public class ActionVo {
    public final long delayMillis; //How long to delay before starting action

    ActionVo(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    /**
     * Class for actions which play a sound resource
     */
    public static class SoundVo extends ActionVo {
        public final int soundResId;
        public SoundVo(long delayMillis,int soundResId) {
            super(delayMillis);
            this.soundResId = soundResId;
        }
    }

    /**
     * Class for actions which speak text
     */
    public static class SpeakVo extends ActionVo {
        public final String text2Speak;
        public SpeakVo(long delayMillis, String text2Speak) {
            super(delayMillis);
            this.text2Speak = text2Speak;
        }
    }
}
