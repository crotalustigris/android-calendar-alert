package com.github.crotalustigris.cal_alert.u;

import android.app.Notification;
import android.os.Build;
import android.os.SystemClock;
import android.service.notification.StatusBarNotification;
import android.util.Log;

/*
 * See license in MainActivity.java
 */
/**
 * Various utilities - applications wide
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class U {
    // Tag used for logcat logging
    public static final String LOGTAG = "aca===n-";

    /*
     * Used to start a big log write. Delays a bit
     * to not overload logging subsystem
     */
    @SuppressWarnings("unused")
    public static void start(String head) {
        SystemClock.sleep(10);
        Log.i(LOGTAG, head);
    }

    /**
     * Log a string, with the class name of the specified object.
     */
    public static void SC(Object o, String s) {
        Log.i(LOGTAG, o.getClass().getSimpleName()+":  "+s);
    }

    /*
     * Log a string
     */
    public static void s(String s) {
        Log.i(LOGTAG, s);
    }
    /*
     * Log a string - as an error
     */
    public static void e(String s) {
        Log.e(LOGTAG, s);
    }

    /*
     * Log a string, with a descriptor, in two columns
     */
    public static void f(String descr, String val) {
        if ( val != null && val.length() > 0 ) {
            Log.i(LOGTAG, String.format("...%1$-14s: <%2$s>\n", descr, val));
        }
    }

    /*
     * Dump (to the log) a bunch of stuff from a notification
     */
    public static void dumpNotif(String note, StatusBarNotification sbn) {
        Log.i(U.LOGTAG, "----  " + note + "  ----");
        Notification notif = sbn.getNotification();
        U.f("_ID", "" + sbn.getId());
        U.f("pkg", sbn.getPackageName());
        U.f("tag", sbn.getTag());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            U.f("key", sbn.getKey());
        }
        U.f("isOnGoing", "" + sbn.isOngoing());
        U.f("postTime", "" + sbn.getPostTime());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            U.f("category", notif.category);
        }
        U.f("extrasTitle", getNotifExtraFieldAsString(notif, Notification.EXTRA_TITLE));
        U.f("flags", String.format("%04X", notif.flags));
        U.s("...");
        U.f("notif", ""+notif);
        U.f("SBN", sbn.toString());
        U.f("extras", "" + notif.extras);
    }


    private static String getNotifExtraFieldAsString(Notification notif, @SuppressWarnings("SameParameterValue") String key) {
        Object obj = notif.extras.get(key);
        return "" + (obj == null ? "" : obj);
    }
}
