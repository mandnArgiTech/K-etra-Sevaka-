package com.ksetrasevakah.core.sms

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.util.Log

class SmsSentReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (resultCode) {
            Activity.RESULT_OK ->
                Log.d(TAG, "SMS sent successfully")
            SmsManager.RESULT_ERROR_GENERIC_FAILURE ->
                Log.e(TAG, "SMS send failed: generic failure")
            SmsManager.RESULT_ERROR_NO_SERVICE ->
                Log.e(TAG, "SMS send failed: no service")
            SmsManager.RESULT_ERROR_NULL_PDU ->
                Log.e(TAG, "SMS send failed: null PDU")
            SmsManager.RESULT_ERROR_RADIO_OFF ->
                Log.e(TAG, "SMS send failed: radio off")
            else ->
                Log.e(TAG, "SMS send failed: unknown error code $resultCode")
        }
    }

    companion object {
        private const val TAG = "SmsSentReceiver"
    }
}
