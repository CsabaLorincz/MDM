# MDM
MDM project for the Final Exam

Before running make sure to use the following adb command:
adb shell dpm set-device-owner com.mdm.app/.receivers.AdminManager

To remove the device:
adb shell dpm remove-active-admin com.mdm.app/.receivers.AdminManager
