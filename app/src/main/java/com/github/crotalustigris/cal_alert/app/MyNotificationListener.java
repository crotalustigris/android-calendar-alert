package com.github.crotalustigris.cal_alert.app;

import android.app.Notification;
import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.github.crotalustigris.cal_alert.R;
import com.github.crotalustigris.cal_alert.app.helper.NotificationActionHelper;
import com.github.crotalustigris.cal_alert.app.helper.PreferencesHelper;
import com.github.crotalustigris.cal_alert.u.ActionVo;
import com.github.crotalustigris.cal_alert.u.C;
import com.github.crotalustigris.cal_alert.u.U;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/*
 * See license in MainActivity.java
 */

/**
 * This class receives the notifications. It is registered with the Android
 * system when the user grants listening permission during the initial
 * setup initiated by the main activity.
 *
 * Note that the only way to disable this is to either turn off the notification
 * listening permission, or to set as disabled the (persistent) shared preference
 * (by using the button on the main activity)
 * <p>
 * See #onNotificationPosted for action taken on receipt of a notification.
 */
public class MyNotificationListener extends NotificationListenerService {
    private static final String GOOGLE_CALENDAR_PKG = "com.google.android.calendar";
    private static final String ANDROID_REMINDER_PACK_NAME = "com.google.android.googlequicksearchbox";
    private static final String ANDROID_MESSENGER_PACK_NAME = "com.google.android.apps.messaging";
    private static final String FACEBOOK_PACK_NAME = "com.facebook.katana";
    private static final String FACEBOOK_MESSENGER_PACK_NAME = "com.facebook.orca";
    private static final String WHATSAPP_PACK_NAME = "com.whatsapp";
    private static final String INSTAGRAM_PACK_NAME = "com.instagram.android";

    /**
     * This defines the notification package names that may be processed
     * by this app.
     */
    private final Set<String> packageWhiteList = new HashSet<>(Arrays.asList(
            GOOGLE_CALENDAR_PKG,
            ANDROID_REMINDER_PACK_NAME,
            ANDROID_MESSENGER_PACK_NAME,
            FACEBOOK_PACK_NAME,
            FACEBOOK_MESSENGER_PACK_NAME,
            WHATSAPP_PACK_NAME,
            INSTAGRAM_PACK_NAME
    ));
    /**
     * Notifications with packages matching any in this list will be immediately ignored
     */
    @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
    private final Set<String> packageBlackList = new HashSet<>(Arrays.asList(
            "com.life360.android.safetymapd"
    ));

    /**
     * Process a notification. If it is from a package we care about,
     * figure out what sounds to play for it, and play it. Otherwise,
     * just ignore it.
     *
     * @param sbn - the notification as posted to the status bar
     */
    @Override
    public void onNotificationPosted(final StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        //U.SC(this, "onNotificationPosted() pkg:"+packageName);
        if (packageName == null || packageBlackList.contains(packageName)) {
            U.SC(this, "Ignoring blacklisted notif - pkg:" + packageName);
            return; // for black list, just be quiet
        }
        U.dumpNotif("Raw notif", sbn);
        
        //If application is disabled, just return
        if (!PreferencesHelper.getEnabledState(this.getApplicationContext())) {
            U.SC(this, "Notifications DISABLED - returing");
            return;
        }
        // If notification is in whitelist:
        // ...select actions to perform,
        // ...then hand it off to the helper for processing
        if (packageWhiteList.contains(packageName)) {
            (new NotificationActionHelper(this, createListOfActions(sbn))).run();
        }
    }

    /**
     * Select sound resource based on one or more of the arguments
     *
     * @param sbn - the StatusBarNotification itself
     * @return the resource ID for the sound
     */
    private LinkedList<ActionVo> createListOfActions(StatusBarNotification sbn) {
//        U.SC(this, "createListOfActions");
        String packageName = sbn.getPackageName();
//        Object titleObj = sbn.getNotification().extras.get(Notification.EXTRA_TITLE);
//        final String title = titleObj != null ? titleObj.toString() : "";
        LinkedList<ActionVo> results = new LinkedList<>();
        String toSpeak = null;
        Object titleObj;
        String title;
        Notification notif = sbn.getNotification();

        /*
         * Package Name is used to decide what class of event it is
         * ...i.e. who generated it
         */
        switch (packageName) {

            // Event from Google Calendar - we think
            case GOOGLE_CALENDAR_PKG:
                U.s("Selected Google Calendar behavior");
                if (notif.category == null || !notif.category.equals("event")) {
                    toSpeak = "no category"; //just for diagnositcs
                } else {
                    // Select behavior based on contents of the title, but we don't for now
                    titleObj = notif.extras.get(Notification.EXTRA_TITLE);
                    title = titleObj != null ? titleObj.toString() : null;
                    if (title == null) {
                        // No title, just make raw event, event sound
                        addSound(results, R.raw.event_event);
                    } else {
                        // Look for action signifiers in title
                        String tlc = title.replaceAll("[^\\p{ASCII}]", "").toLowerCase();
                        if (tlc.startsWith("a:sp:")) {
                            // An appointment, to be spoken
                            toSpeak = tlc.substring(5);
                            addSound(results, R.raw.appt_appt);
                        } else if (tlc.startsWith("a:")) {
                            // An appointment - speak it anyway, 'cause that's what we do
                            toSpeak = tlc.substring(2);
                            addSound(results, R.raw.appt_appt);
                        } else if (tlc.startsWith("r:sp:")) {
                            // An event reminder to be spoken. 
                            // !!!Not to be confused with an Android Calendar reminder!!!
                            toSpeak = tlc.substring(5);
                            addSound(results, R.raw.repeated);
                        } else if (tlc.startsWith("r:")) {
                            // An event reminder not to be spoken
                            // !!!Not to be confused with an Android Calendar reminder!!!
                            addSound(results, R.raw.repeated);
                        } else if (tlc.startsWith("sp:")) {
                            // An event to be spoken 
                            toSpeak = tlc.substring(3);
                            addSound(results, R.raw.event_event);
                        } else {
                            // No signifier, no speach, just "event, event" resource
                            addSound(results, R.raw.event_event);
                        }
                    }
                    break;
                }

            // An Android reminder event. We can't handle this well, because if any have 
            // already gone off, the title will just indicate how many exist.
            //
            // This is structured to allow multiple actions and one spoken text per notification.
            // ...at the moment, we only do one action and one spoken text.
            case ANDROID_REMINDER_PACK_NAME:
                if ( C.isReminderNotificationsEnabled ) {
                    U.s("Selected Reminder behavior");
                    // here we could select behavior based on contents of the title, but we don't for now
                    titleObj = notif.extras.get(Notification.EXTRA_TITLE);
                    title = titleObj != null ? titleObj.toString() : null;
                    addSound(results, R.raw.reminder);
                    if (title != null) {
                        String tlc = title.replaceAll("[^\\p{ASCII}]", "").toLowerCase();
                        if (tlc.length() > 0) {
                            toSpeak = tlc;
                        }
                    }
                } else {
                    U.SC(this, "Reminder notifications are IGNORED");
                }
                break;

            // A notification from the messenger app. Speak the title after stripping junk from it
            case ANDROID_MESSENGER_PACK_NAME:
                if (0 == (notif.flags & Notification.FLAG_ONLY_ALERT_ONCE)) {
                    // The kind we want - speak the title after stripping junk from it
                    U.SC(this, "Selected Text Message Behavior");
                    titleObj = notif.extras.get(Notification.EXTRA_TITLE);
                    title = titleObj != null ? titleObj.toString().replaceAll("[^\\p{ASCII}]", "") : "";
                    Object extraTextObj = notif.extras.get(Notification.EXTRA_TEXT);
                    String extraText = extraTextObj != null ? extraTextObj.toString().replaceAll("[^\\p{ASCII}]", "") : "";
                    title += " " + extraText;
                    U.SC(this, "Temp - msg title:" + title + " extraTEXT:" + extraText);
                    if (title.length() > 1) {
                        toSpeak = title;
                    }
                    addSound(results, R.raw.messages_with_effects);
                } else {
                    // The kind we don't want - ignore it
                    U.SC(this, "Bad text - notif flags:" + String.format("%04x", notif.flags) + " should have:" + String.format("%04x", Notification.FLAG_ONLY_ALERT_ONCE));
                }

            // White list package, but we are ignoring it, so ignore it
            default:
        }

        // If we are to speak text, add a speech action as the last action of the list
        if (toSpeak != null) {
            U.SC(this, "Speaking:" + toSpeak);
            results.add(new ActionVo.SpeakVo(0, toSpeak));
        }
        // Return the (possibly empty) list of actions
        return results;
    }

    /*
     * utility method to add a sound action to the list
     */
    private void addSound(LinkedList<ActionVo> list, int resId) {
        list.add(new ActionVo.SoundVo(list.isEmpty() ? C.defaultStartDelayMs : 0, resId));
    }


    /**
     * onCreate - here just to demonstrate that this object is created when permissions enabled
     */
    @Override
    public void onCreate() {
        U.SC(this, "onCreate");
        super.onCreate();
    }

    /**
     * onBinnd - here just to demonstrate that this listener is bound when permissions enabled
     *
     * @param intent
     * @return
     */
    @SuppressWarnings("JavaDoc")
    @Override
    public IBinder onBind(Intent intent) {
        U.SC(this, "onBind()");
        return super.onBind(intent);
    }

    /**
     * Here for destroy event for this object. 
     * ... we don't release any resources. Hopefully we don't need to
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        U.SC(this, "onDestroy()");
    }

    /**
     * This can be called, so we have to put it in, but it does nada
     */
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        //U.SC(this, "onNotificationRemoved()");
    }

    /**
     * Utility diagnostic method to take a string apart
     * to show non-ASCII characters
     */
    @SuppressWarnings({"StringConcatenationInLoop", "unused"})
    private void takeApart(String s) {
        String foo = "";
        for (int i = 0; i < s.length(); i++) {
            foo += "<" + s.charAt(i) + "(" + (int) s.charAt(i) + ")>";
        }
        U.SC(this, foo + "  from [" + s + "]");
    }
}
