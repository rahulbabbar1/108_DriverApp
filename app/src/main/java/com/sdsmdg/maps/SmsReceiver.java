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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {

    static String recievedMSG = null;
    static String latitude = null;
    static String longitude = null;
    static String name, userMobile, requestId;
    static String TAG = "smsreceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage msg = null;
        if (null != bundle) {
            Object[] smsObj = (Object[]) bundle.get("pdus");
            for (Object object : smsObj) {
                msg = SmsMessage.createFromPdu((byte[]) object);
//                Date date = new Date(msg.getTimestampMillis());
//                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                String receiveTime = format.format(date);
                recievedMSG = msg.getDisplayMessageBody();
                if (recievedMSG.startsWith("Laterox")) {
                    String regex = "\\[(.+)\\]\\[(.+)\\]\\[(.+)\\]\\[(.+)\\]\\[(.+)\\]";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(recievedMSG);
                    if (matcher.find()) {
                        latitude = matcher.group(1);
                        longitude = matcher.group(2);
                        name = matcher.group(3);
                        userMobile = matcher.group(4);
                        requestId = matcher.group(5);
                    }
//                    int firstClosBrac = recievedMSG.indexOf(']');
//                    latitude = recievedMSG.substring(recievedMSG.indexOf('[')+1,firstClosBrac);
//                    longitude = recievedMSG.substring(recievedMSG.indexOf('[',firstClosBrac)+1,recievedMSG.indexOf(']',firstClosBrac+1));
                    Log.d(TAG, "onReceive() called with: " + "latitude = [" + latitude + "], longitude = [" + longitude + "]");
                    Intent i = new Intent(context, LoginActivity.class);
                    i.putExtra("isFromSmsReceiver", true);
                    i.putExtra("latitude", latitude);
                    i.putExtra("longitude", longitude);
                    i.putExtra("name", name);
                    i.putExtra("mobile", userMobile);
                    i.putExtra("requestId", requestId);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            }
        }
    }
}
