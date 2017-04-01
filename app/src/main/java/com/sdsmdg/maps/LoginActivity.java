package com.sdsmdg.maps;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sdsmdg.maps.Constants.IS_ASSIGNED;
import static com.sdsmdg.maps.Constants.SHARED_PREFERENCES;

/**
 * Created by rahul on 25/11/16.
 */
public class LoginActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String TAG = "loginactivity";
    private boolean isNewUser = false;
    private String phoneNumber = "";
    private String name = "";
    private Activity SavedActivity;
    private int count = 0;
    private EditText cityET;
    private String city;
    public static String STATUS_ASSIGNED = "assigned";
    private Geocoder geocoder;
    private LocationListener locationListener;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        cityET = (EditText) findViewById(R.id.city_et);
        //setSpinner();

        final EditText emailET = (EditText) findViewById(R.id.emailET);
        final EditText passET = (EditText) findViewById(R.id.passET);
        final TextView switchTV = (TextView) findViewById(R.id.signupTV);
        final TextView switchTVBack = (TextView) findViewById(R.id.signupTV2);
        final Button loginButton = (Button) findViewById(R.id.loginet);
        final EditText phoneET = (EditText) findViewById(R.id.phoneET);
        final EditText nameET = (EditText) findViewById(R.id.nameET);
        final Button signupButton = (Button) findViewById(R.id.signup);

        switchTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameET.setVisibility(View.VISIBLE);
                phoneET.setVisibility(View.VISIBLE);
                cityET.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.INVISIBLE);
                signupButton.setVisibility(View.VISIBLE);
                switchTV.setVisibility(View.INVISIBLE);
                switchTVBack.setVisibility(View.VISIBLE);
                geocoder = new Geocoder(LoginActivity.this, Locale.getDefault());
                isNewUser = true;
                setCity();
            }
        });

        switchTVBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameET.setVisibility(View.INVISIBLE);
                phoneET.setVisibility(View.INVISIBLE);
                cityET.setVisibility(View.INVISIBLE);
                loginButton.setVisibility(View.VISIBLE);
                signupButton.setVisibility(View.INVISIBLE);
                switchTV.setVisibility(View.VISIBLE);
                switchTVBack.setVisibility(View.INVISIBLE);
                isNewUser = false;
            }
        });

        cityET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick() called with: v = [" + v + "]");
                setCity();
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateEmail(emailET) && validatePassword(passET)) {
                    signin(emailET.getText().toString(), passET.getText().toString());
                }
            }
        });


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateName(nameET) && validateCity(cityET) && validateEmail(emailET) && validatePassword(passET) && validatePhone(phoneET)) {
                    phoneNumber = phoneET.getText().toString();
                    name = nameET.getText().toString();
                    city = cityET.getText().toString();
                    createAccount(emailET.getText().toString(), passET.getText().toString());
                }
            }
        });

        SavedActivity = LoginActivity.this;
        myCheckPermission(SavedActivity);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                getCity(location.getLatitude() , location.getLongitude());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


    }

    private void getCity(double latitude, double longitude){
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String city = addresses.get(0).getLocality();
            cityET.setText(city);
            Log.d(TAG, "getCity() called with: latitude = [" + city + "], longitude = [" + longitude + "]");
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    private void setCity() {
        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Toast.makeText(LoginActivity.this, "Please Enable Location first", Toast.LENGTH_LONG).show();
            GetLocation.showSettings(LoginActivity.this);
        } else {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(LoginActivity.this,
                        new String[]{ android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_GET_CITY);
                return;
            }
            else {
                Toast.makeText(LoginActivity.this, "Fetching City", Toast.LENGTH_LONG).show();
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }

    }
    private void init(){
        mAuth = FirebaseAuth.getInstance();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    // User is signed in
                    if(count==0){
                        count++;
                        if(isNewUser){
                            uploadData(user.getUid());
                        }
                        Intent intentPrev = getIntent();

                        Intent intent;
                        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);
                        SharedPreferences.Editor spEditor = sharedPreferences.edit();
                        boolean isAssigned = sharedPreferences.getBoolean(IS_ASSIGNED, false);
                        if((getIntent()!=null)&&(intentPrev.getBooleanExtra("isFromSmsReceiver",false))){
                            intent = new Intent(LoginActivity.this,MainActivity.class);
                            Intent data = getIntent();
                            String reqItmString = data.getStringExtra("requestItem");
                            intent.putExtra("requestItem", reqItmString);
                            spEditor.putString("requestItem", reqItmString);
                            spEditor.putBoolean(IS_ASSIGNED, true);
                            spEditor.commit();
                            Log.d(TAG, "onAuthStateChanged() called with: firebaseAuth1 = [" + sharedPreferences.getBoolean(IS_ASSIGNED,false) + "]");
                        }
                        else if(isAssigned){
                            intent = new Intent(LoginActivity.this,MainActivity.class);
                            //intent.fillIn(getIntent(),Intent.FILL_IN_DATA);
                            String reqItmString = sharedPreferences.getString("requestItem","");
                            intent.putExtra("requestItem", reqItmString);
                            Log.d(TAG, "onAuthStateChanged() called with: firebaseAuth2 = [" + reqItmString + "]");
                        }
                        else{
                            intent = new Intent(LoginActivity.this,HomeActivity.class);
                            Log.d(TAG, "onAuthStateChanged() called with: firebaseAuth3 = [" + firebaseAuth + "]");
                        }

                        intent.putExtra("uid",user.getUid());
                        startActivity(intent);
                        finish();

                        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    }
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    private void createAccount(String email,String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,"Authentication Failed" ,
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    public void sendData(String location,String value){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(location);

        myRef.setValue(value);
    }

    private void signin(String email,String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication Failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    public final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    public final int MY_PERMISSIONS_REQUEST_GET_CITY = 11;

    public boolean myCheckPermission(Activity thisActivity){
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

            return false;

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
            //}

        }
        else{
            Log.d(TAG, "myCheckPermission() else called with: " + "thisActivity = [" + thisActivity + "]");
            return true;
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

                return;
            }

            case MY_PERMISSIONS_REQUEST_GET_CITY:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setCity();
                } else {
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private boolean validatePhone(EditText editText) {
        String temp = editText.getText().toString().trim();
        editText.setText(temp);
        if (TextUtils.isEmpty(temp)) {
            editText.setError("Required.");
            return false;
        }
        else if(temp.length()!=10){
            editText.setError("Required to be of 10 digits.");
            return false;
        }else {
            editText.setError(null);
            return true;
        }
    }

    private boolean validateName(EditText editText) {
        String temp = editText.getText().toString().trim();
        editText.setText(temp);
        if (TextUtils.isEmpty(temp)) {
            editText.setError("Required.");
            return false;
        } else {
            editText.setError(null);
            return true;
        }
    }
    
    private boolean validateCity(EditText editText){
        String temp = editText.getText().toString().trim();
        editText.setText(temp);
        if (TextUtils.isEmpty(temp)) {
            editText.setError("Required.");
            return false;
        } else {
            editText.setError(null);
            return true;
        }
    }

    private boolean validateEmail(EditText editText) {
        String temp = editText.getText().toString().trim();
        editText.setText(temp);
        if (TextUtils.isEmpty(temp)) {
            editText.setError("Required.");
            return false;
        }
        else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(temp).matches()){
            editText.setError("invalid Email.");
            return false;
        }
        else {
            editText.setError(null);
            return true;
        }
    }

    private boolean validatePassword(EditText editText) {
        String temp = editText.getText().toString();
        if (TextUtils.isEmpty(temp)) {
            editText.setError("Required.");
            return false;
        } else if(temp.length()<8){
            editText.setError("Password must contain atleast 8 characters..");
            return false;
        }
        else{
            editText.setError(null);
            return true;
        }
    }

//    public void setSpinner(){
//
//        // Spinner click listener
//        spinner.setOnItemSelectedListener(this);
//
//        // Spinner Drop down elements
//        city="Chennai";
//        List<String> categories = new ArrayList <String>();
//        categories.add("Chennai");
//        categories.add("Kancheepuram");
//        categories.add("Thiruvallur");
//
//        // Creating adapter for spinner
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter <String>(this, android.R.layout.simple_spinner_item, categories);
//
//        // Drop down layout style - list view with radio button
//        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        // attaching data adapter to spinner
//        spinner.setAdapter(dataAdapter);
//    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        city = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        //Toast.makeText(parent.getContext(), "Selected: " + city, Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    private void uploadData(String uid){
        sendData("cityData/"+uid+"/city",city);
        sendData("driver/"+city+"/"+uid+"/phone",phoneNumber);
        sendData("driver/"+city+"/"+uid+"/name",name);
        sendData("driver/"+city+"/"+uid+"/district",city);
        sendData("driver/"+city+"/"+uid+"/status","active");
    }

//    public void getStatus(final String uid) {
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference();
//        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.d(TAG, "onDataChange() called with: dataSnapshot = [" + dataSnapshot.toString() + "]");
//                String status = dataSnapshot.child("status").getValue().toString();
//                String requestId = dataSnapshot.child("currentRequestId").getValue().toString();
//                Log.d(TAG, "get data onDataChange() called with: city = [" + city + "]");
//                if(status==LoginActivity.STATUS_ASSIGNED){
//                    //start map activity
//                }
//                else {
//                    Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
//                    intent.putExtra("uid",uid);
//                    startActivity(intent);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }


}
