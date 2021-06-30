# MDM
MDM project for the Final Exam

Before running make sure to use the following adb command:
adb shell dpm set-device-owner com.mdm.app/.receivers.AdminManager

To remove the device:
adb shell dpm remove-active-admin com.mdm.app/.receivers.AdminManager

In order to set up the API, setup port forwarding to port 5000 on your device.
Depending on your URL, you may need to change the BASE_URL value in Api.kt. 
Make sure you have the right settings in order to be able to access the API.