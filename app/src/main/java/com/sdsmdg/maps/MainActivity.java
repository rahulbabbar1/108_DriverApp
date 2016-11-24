package com.sdsmdg.maps;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.R.attr.imeFullscreenBackground;
import static android.R.attr.value;
import static android.R.attr.width;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap m_map;
    Button show;
    EditText lat, lng;
    MarkerOptions clientPosition;
    boolean mapReady = false;

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
                    m_map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
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
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mapReady = true;
        m_map = map;

    }


}
