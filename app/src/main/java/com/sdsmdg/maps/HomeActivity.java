package com.sdsmdg.maps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.carpediem.homer.funswitch.FunSwitch;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity implements FunSwitch.fun {

    FunSwitch fun;
    TextView textView;
    String uid;
    private String TAG="HomeActivity";
    String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fun=(FunSwitch) findViewById(R.id.switchButton);
        fun.setInterface(this);
        textView = (TextView) findViewById(R.id.textView);
        Intent data = getIntent();
        uid = data.getStringExtra("uid");
        getCity("cityData/" + uid);
    }

    @Override
    public void onToggle() {
        Log.d("onTogle", "onToggle() called "+fun.mIsOpen);
        if(fun.mIsOpen){
            textView.setText("Active");
        }
        else {
            textView.setText("Inactive");
        }
        changeStatus(uid,fun.mIsOpen);
    }

    public void changeStatus(String uid, boolean isactive){
        String status ;
        if(isactive){
            status="active";
        }
        else {
            status="inactive";
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference requestRef = database.getReference("driver/"+city+"/"+uid+"/status");
        requestRef.setValue(status);
    }

    public void getData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("driver/"+city+"/"+uid);
        Log.d(TAG, "getData() called"+city+" "+uid);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String status = dataSnapshot.child("status").getValue().toString();
                if(status.equals("active")){
                    changeSwitchState(true);
                    textView.setText("Active");
                }
                else{
                    changeSwitchState(false);
                    textView.setText("Inactive");
                }
                Log.d(TAG, "onDataChange() called with: dataSnapshot = [" + dataSnapshot.toString() + "]");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void changeSwitchState(boolean isactive){
        fun.setState(isactive);
    }

    public void getCity(String location) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(location);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange() called with: dataSnapshot = [" + dataSnapshot.toString() + "]");
                city = dataSnapshot.child("city").getValue().toString();
                getData();
                Log.d(TAG, "get data onDataChange() called with: city = [" + city + "]");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

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


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout(){
        FirebaseAuth.getInstance().signOut();
        Intent loginIntent=new Intent(this,LoginActivity.class);
        startActivity(loginIntent);
    }

    // Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }
}
