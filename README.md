This app enhances notifications from calendar events and text messages.
Based on the source and content of notifications on the device:
  -ignore the notification, or
  -play a sound keyed off of the type or the type plus embedded signifiers, or
     ...the type is determined by the notification package name
  -speak the text of the notification, based on type and/or embedded signifiers
     ...signifiers are a letter or two followed by a colon, or a pair of these

It demonstrates:
  -how to listen to notifications using a notification listener
  -asking for and getting permissions from the user for notifications
  -use of MediaPlayer to play sound resources

*** NOTES AND WARNINGS ***

* This app relies on behavior of the calendar app and the Android messaging app
  that was discovered by examining notifications produced. 
  Hence is may cease to work properly over time, since these behaviors
  are not documented nor guaranteed

* The app produces quite a bit of log activity. You may want to cut it down.
  Logging is done via the class U - for "Utility"



