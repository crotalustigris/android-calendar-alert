package com.github.crotalustigris.cal_alert.app.helper;

import android.content.Context;
import android.media.MediaPlayer;

import com.github.crotalustigris.cal_alert.u.U;

/*
 * See license in MainActivity.java
 */
/**
 * Helper to play the selected sound using Android MediaPlayer
 */
@SuppressWarnings("JavaDoc")
public class SoundPlayer implements MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnInfoListener {
    private MediaPlayer mediaPlayer;
    private Runnable runAfter;

    /**
     * Start playing the sound from the specified resource.
     * ...when done, run runAfter if it is not null
     * @param ctx - a context
     * @param rawResource - the resource
     * @param runAfter - if not null, run this after the sound finishes, or on error
     */
    public void play(Context ctx, int rawResource, Runnable runAfter) {
//        U.SC(this, "play");
        this.runAfter = runAfter;
        mediaPlayer = MediaPlayer.create(ctx, rawResource);
        mediaPlayer.setOnCompletionListener (this);
        mediaPlayer.setOnErrorListener (this);
        mediaPlayer.start();
    }

    /**
     * Called by media player on error, shuts it down,
     * then executes callback for what to do next
     * @param mp - the media player
     * @param what
     * @param extra
     * @return
     */
    @SuppressWarnings("SameReturnValue")
    @Override public boolean onError(MediaPlayer mp, int what, int extra) {
        U.SC(this, "onError()  what:"+what+"  extra:"+extra);
        shutDown(mp);
        if ( runAfter != null ) {
            runAfter.run();
        }
        return true;
    }

    /**
     * Called by media player when done,
     *   calls the after action if provided
     *   then shuts down the media player
     *
     * @param mp
     */
    @SuppressWarnings("unused")
    @Override
    public void onCompletion(MediaPlayer mp) {
//        U.SC(this, "onCompletion");
        if ( runAfter != null ) {
            runAfter.run();
        }
        shutDown(mp);
    }


    @SuppressWarnings("SameReturnValue")
    @Override public boolean onInfo (MediaPlayer mp, int what, int extra) {
        U.SC(this, "onInfo: what:"+what+"  extra:"+extra);
        return false;
    }

    /**
     * Called to release the media player
     * @param mp
     */
    private void shutDown(MediaPlayer mp) {
//        U.SC(this, "shutDown");
        if (mp != null ) {
            mp.release();
        }
        mediaPlayer = null;
    }
}
