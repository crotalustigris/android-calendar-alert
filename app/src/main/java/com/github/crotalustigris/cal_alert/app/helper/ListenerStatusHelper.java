package com.github.crotalustigris.cal_alert.app.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;

import com.github.crotalustigris.cal_alert.R;
import com.github.crotalustigris.cal_alert.u.GU;
import com.github.crotalustigris.cal_alert.u.U;
/*
 * See license in MainActivity.java
 */
/**
 * Miscellaneous helper functions for the notification listener
 */
public class ListenerStatusHelper {

    /**
     * Check if the listener is enabled in the Android settings
     *
     * @param ctx - the app context
     * @param myPackageName - the package name for the app
     * @return true if enabled, else false
     */
    public boolean isListenerEnabled(Context ctx, String myPackageName) {
        if ( NotificationManagerCompat.getEnabledListenerPackages(ctx).contains(myPackageName) ) {
            U.SC(this, "Notification listener IS ENABLED");
            return true;
        } else {
            U.SC(this, "Notification listener IS NOT ENABLED");
            return false;
        }
    }

    /**
     * Put up a dialog asking user to enable the listener.
     *
     * If the user refuses, ask again. On refusal again, give up and the app wil temrinate.
     *
     * @param act - an activity for context
     */
    public void askUserToEnableListener(final Activity act) {
        U.SC(this, "askUserToEnableListener()");
        GU.showYesNoDialog(
                this,
                act,
                R.string.app_name,
                R.string.notification_listener_service_explanation,
                (DialogInterface, id) -> {
                    U.SC(this, "User said yes - Show settings");
                    act.startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));

                },
                (DialogInterface, id) -> {
                    U.SC(this, "User did not give permission - try again");
                    askUserAgainToEnableListener(act);

                }
        );
    }

    /**
     * Again ask the user to enable the listener. On refusal, terminate.
     *
     * @param act and activity for context
     */
    private void askUserAgainToEnableListener(Activity act) {
        U.SC(this, "askUserAgainToEnableListener()");
        GU.showYesNoDialog(
                this,
                act,
                R.string.notification_listener_service,
                R.string.notification_listener_service_again_explanation,
                (DialogInterface, id) -> {
                    U.SC(this, "Start notification settings again dialog");
                    act.startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));

                },
                (DialogInterface, id) -> {
                    U.SC(this, "User declined notification settings - giving up and killing app");
                    act.finishAffinity();
                }
        );
    }
}
