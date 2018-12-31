package com.github.crotalustigris.cal_alert.u;

/**
 * Holds static common constants and variables
 */
public class C {

    // Milliseconds to delay before starting an action
    public static final long defaultStartDelayMs = 3000;
    
    // If false, we ignore notifications for Android reminders.
    // !!!NOTE: These are different from the "reminder" notifications
    // ...for our calendar events. Those are probably also for
    // ...calendar notifications, but I couldn't find them anywhere
    // ...using the Calendar content resolver.
    public static final boolean isReminderNotificationsEnabled = false; 
}
