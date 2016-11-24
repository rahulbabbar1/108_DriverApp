package com.sdsmdg.maps;

/**
 * Created by rahul on 24/11/16.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SmsReceiver extends BroadcastReceiver {

    static String recievedMSG = null;
    static String latitude = null;
    static String longitude = null;
    static String TAG = "smsreceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage msg = null;
        if (null != bundle) {
            Object[] smsObj = (Object[]) bundle.get("pdus");
            for (Object object : smsObj) {
                msg = SmsMessage.createFromPdu((byte[]) object);
                Date date = new Date(msg.getTimestampMillis());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String receiveTime = format.format(date);
                if (msg.getOriginatingAddress().equals(Constants.serverNumber)) {
                    recievedMSG = msg.getDisplayMessageBody();
                    int firstClosBrac = recievedMSG.indexOf(']');
                    latitude = recievedMSG.substring(recievedMSG.indexOf('[')+1,firstClosBrac);
                    longitude = recievedMSG.substring(recievedMSG.indexOf('[',firstClosBrac)+1,recievedMSG.indexOf(']',firstClosBrac+1));
                    Log.d(TAG, "onReceive() called with: " + "latitude = [" + latitude + "], longitude = [" + longitude + "]");

                }
            }
        }
    }
}
