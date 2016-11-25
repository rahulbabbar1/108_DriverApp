package com.sdsmdg.maps;

import android.*;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import static android.R.attr.imeFullscreenBackground;
import static android.R.attr.value;
import static android.R.attr.width;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap m_map;
    Button show;
    EditText lat, lng;
    MarkerOptions clientPosition;
    boolean mapReady = false;
    boolean shouldShow = false;
    public Activity mainActivity;
    public String TAG="MainActivity";

    //TODO Convert int to float
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        show = (Button) findViewById(R.id.show);
        lat = (EditText) findViewById(R.id.lat);
        lng = (EditText) findViewById(R.id.lng);

        mainActivity=MainActivity.this;
        myCheckPermission(mainActivity);
        Intent i= new Intent(MainActivity.this,GetLocation.class);
        startService(i);



        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mapReady) {
                    if (lat.getText().toString().trim().length() != 0 && lng.getText().toString().trim().length() != 0) {
                        LatLng latLng = new LatLng(Float.parseFloat(lat.getText().toString()), Float.parseFloat(lng.getText().toString()));
                        CameraPosition target = CameraPosition.builder().target(latLng).zoom(15).build();

                        clientPosition = new MarkerOptions().position(latLng).title("Emergency");
                        m_map.moveCamera(CameraUpdateFactory.newCameraPosition(target));
                        m_map.addMarker(clientPosition);
                    }
                }
            }
        });

        Intent intent = getIntent();
        if(intent.getBooleanExtra("isFromSmsReceiver",false)){
            lat.setText(intent.getStringExtra("latitude"));
            lng.setText(intent.getStringExtra("longitude"));
            shouldShow=true;
        }
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mapReady = true;
        m_map = map;
        m_map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if(shouldShow){
            show.performClick();
        }
    }

    private void logout(){
        FirebaseAuth.getInstance().signOut();
    }

    // Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Event Handling for Individual menu item selected
     * Identify single menu item by it's id
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.menu_logout:
                // Single menu item is selected do something
                // Ex: launching new activity/screen or show alert message
                logout();
                finish();
                return true;

//            case R.id.menu_save:
//                Toast.makeText(AndroidMenusActivity.this, "Save is Selected", Toast.LENGTH_SHORT).show();
//                return true;
//
//            case R.id.menu_search:
//                Toast.makeText(AndroidMenusActivity.this, "Search is Selected", Toast.LENGTH_SHORT).show();
//                return true;
//
//            case R.id.menu_share:
//                Toast.makeText(AndroidMenusActivity.this, "Share is Selected", Toast.LENGTH_SHORT).show();
//                return true;
//
//            case R.id.menu_delete:
//                Toast.makeText(AndroidMenusActivity.this, "Delete is Selected", Toast.LENGTH_SHORT).show();
//                return true;
//
//            case R.id.menu_preferences:
//                Toast.makeText(AndroidMenusActivity.this, "Preferences is Selected", Toast.LENGTH_SHORT).show();
//                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    public void myCheckPermission(Activity thisActivity){
        // Here, thisActivity is the current activity
        if( ContextCompat.checkSelfPermission(thisActivity,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED||(ContextCompat.checkSelfPermission(thisActivity,
                android.Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED)||(ContextCompat.checkSelfPermission(thisActivity,
                android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED)||(ContextCompat.checkSelfPermission(thisActivity,
                android.Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) ){

//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
//                    Manifest.permission.READ_CONTACTS)) {
//
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//
//            } else {

            // No explanation needed, we can request the permission.
            Log.d(TAG, "myCheckPermission() if called with: " + "thisActivity = [" + thisActivity + "]");

            ActivityCompat.requestPermissions(thisActivity,
                    new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.INTERNET, android.Manifest.permission.SEND_SMS, android.Manifest.permission.RECEIVE_SMS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
            //}

        }
        else{
            Log.d(TAG, "myCheckPermission() else called with: " + "thisActivity = [" + thisActivity + "]");
            Intent i= new Intent(MainActivity.this,GetLocation.class);
            startService(i);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                myCheckPermission(mainActivity);
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
