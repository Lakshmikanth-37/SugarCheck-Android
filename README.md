# SugarCheck Android app

Premium dark-mode reminder app built with native Kotlin. Package: `com.sugarcheck.app`.

## Build an APK
1. Open this folder in Android Studio.
2. Let Gradle sync.
3. Connect an Android phone or use an emulator.
4. Run the `app` configuration, or select **Build > Build APK(s)**.
5. APK output: `app/build/outputs/apk/debug/app-debug.apk`.

You can also upload this folder to a GitHub repository and use the included workflow to build an APK.

## Configure each phone
- Install the APK on both Android phones.
- Open SugarCheck and select the correct role for that phone.
- Allow notifications and exact alarms when Android asks.
- Set the phone's notification channel to sound/vibrate and allow full-screen notifications if your Android version offers that setting.
- Disable battery optimization for SugarCheck if the phone delays alarms.

## Schedule logic
The reminder schedule follows the role selected in the app:
- **Her phone · Minneapolis** uses `America/Chicago` and the Minneapolis times shown in the screenshot: 8:00 AM, 12:30 PM, 6:30 PM, 11:00 PM, plus 2:00 AM and 6:00 AM as the endpoints of the 2–6 AM window.
- **My phone · India** uses `Asia/Kolkata` and the corresponding India times from the screenshot: 6:30 PM, 11:00 PM, 5:00 AM, 9:30 AM, 12:30 PM and 4:30 PM.

Timezone rules are applied automatically, so the Minneapolis/India offset adjusts when Minneapolis changes between daylight and standard time. The screenshot's paired times match the daylight-saving period; after the US clock change, the Indian equivalents will shift by one hour.

## Important Android behavior
The app uses exact alarms, full-screen alarm UI and a looping system alarm tone. Android/OEM settings can still suppress full-screen launch or sound; allow notifications, exact alarms, full-screen alerts and background activity where available. A reminder remains on the alarm screen until **OK** is pressed while the app is running; notification remains ongoing until dismissed with OK. Some manufacturers can still limit background behavior.

## Scope
This build runs reminders locally on each phone. It does not send a check-in status to the other phone or remotely trigger the other phone's alarm. Remote pairing requires a shared backend (for example Firebase) and server-side push notifications.
