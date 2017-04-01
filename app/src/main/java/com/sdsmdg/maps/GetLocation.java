package com.sdsmdg.maps;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by rahul on 24/11/16.
 */


public class GetLocation extends Service {
    public String TAG = "getlocation";
    private String city = "";
    String uid;
//    public int count = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        Log.d(TAG, "onCreate() called with: " + "");

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Toast.makeText(GetLocation.this, "Please Enable Location first", Toast.LENGTH_LONG).show();
            showSettings(GetLocation.this);
        } else {

        }


// Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                //Called when a new location is found by the network location provider.
                //makeUseOfNewLocation(location);
                //sendSMS(location.getLatitude(),location.getLongitude());
                Log.d(TAG, "onLocationChanged() called with: " + "location = [" + location + "]");
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String emailfb = "";
                String uidfb = "";
                if (user != null) {
                    // Name, email address, and profile photo Url
                    //name = user.getDisplayName();
                    emailfb = user.getEmail();
                    //Uri photoUrl = user.getPhotoUrl();

                    // The user's ID, unique to the Firebase project. Do NOT use this value to
                    // authenticate with your backend server, if you have one. Use
                    // FirebaseUser.getToken() instead.
                    uid=user.getUid();
                    uidfb = user.getUid();
                    setNotification("Location Service","You are currently active");
                    getData("cityData/" + uidfb);
                    if ((!city.equals(""))) {
                        sendData("driver/" + city + "/" + uidfb + "/latitude", Double.toString(location.getLatitude()));
                        sendData("driver/" + city + "/" + uidfb + "/longitude", Double.toString(location.getLongitude()));
                        sendData("driver/" + city + "/" + uidfb + "/accuracy", Double.toString(location.getAccuracy()));
                    }
                }

                //Toast.makeText(GetLocation.this, "onLocationChanged() called with: " + "location [ longitude = " + location.getLongitude() + " latitude = " + location.getLatitude() + "]",Toast.LENGTH_LONG).show();
//                Log.d(TAG, "onLocationChanged() called with: " + "location [ longitude = " + location.getLongitude() + " latitude = " + location.getLatitude() + " type: " + location.getProvider() + " accuracy : " + location.getAccuracy() + "]");
//                count++;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

// Register the listener with the Location Manager to receive location updates
//        Log.d(TAG, "onCreate() permission called with: " + ContextCompat.checkSelfPermission(GetLocation.this,
//                Manifest.permission.ACCESS_FINE_LOCATION) + "");
        if ((ContextCompat.checkSelfPermission(GetLocation.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)) {
//            Log.d(TAG, "onCreate() inside if called with: " + "");
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

    }

    @Override
    public void onStart(Intent intent, int startid) {
        //Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        //Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        //Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
    }


    public static void showSettings(Context mContext) {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
//
//        // Setting Dialog Title
//        alertDialog.setTitle("GPS is settings");
//
//        // Setting Dialog Message
//        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
//
//        // On pressing Settings button
//        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                mContext.startActivity(intent);
//            }
//        });
//        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        // on pressing cancel button
//
//        // Showing Alert Message
//        alertDialog.show();]

        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

//    public void sendSMS(final double latitude, final double longitude){
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                // do the thing that takes a long time
//                Log.d(TAG, "sendSMS run() called with: " + "");
//                String smsBody = "Latiitude: [" + latitude + "] " +  "Longitude: [" + longitude + "] ";
//                SmsManager smsManager = SmsManager.getDefault();
//                smsManager.sendTextMessage(Constants.serverNumber, null, smsBody, null, null);
//            }
//        }).start();ice
    //    }


    public void getData(String location) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(location);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange() called with: dataSnapshot = [" + dataSnapshot.toString() + "]");
                city = dataSnapshot.child("city").getValue().toString();
                Log.d(TAG, "get data onDataChange() called with: city = [" + city + "]");
            }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    public void sendData(String location, String value) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(location);

        myRef.setValue(value);
    }



    public void setNotification(String title, String message){
        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.putExtra("uid",uid);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(HomeActivity.class);
        stackBuilder.addNextIntent(homeIntent);
        Log.d(TAG, "setNotification() called with: title = [" + title + "], uid = [" + uid + "]");
        PendingIntent contentIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)  // the status icon
                .setTicker(title)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(title)  // the label
                .setContentText(message)  // the contents of the entry
                .setContentIntent(contentIntent)// The intent to send when clicked
                .setAutoCancel(false)
                .build();
        startForeground(1, notification);
    }
}
