package com.sdsmdg.maps;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    //TODO Convert int to float
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        show = (Button) findViewById(R.id.show);
        lat = (EditText) findViewById(R.id.lat);
        lng = (EditText) findViewById(R.id.lng);



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
}
