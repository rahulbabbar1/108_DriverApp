package com.sdsmdg.maps;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rahul on 25/11/16.
 */
public class LoginActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String TAG="loginactivity";
    private boolean isNewUser=false;
    private String phoneNumber="";
    private String name="";
    private Activity SavedActivity;
    private int count=0;
    private Spinner spinner;
    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        spinner = (Spinner) findViewById(R.id.spinner);
        setSpinner();

        final EditText emailET= (EditText)findViewById(R.id.emailET);
        final EditText passET= (EditText)findViewById(R.id.passET);
        final TextView switchTV = (TextView)findViewById(R.id.signupTV);
        final TextView switchTVBack = (TextView)findViewById(R.id.signupTV2);
        final Button loginButton=(Button)findViewById(R.id.loginet);
        final EditText phoneET= (EditText)findViewById(R.id.phoneET);
        final EditText nameET= (EditText)findViewById(R.id.nameET);
        final Button signupButton=(Button)findViewById(R.id.signup);

        switchTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameET.setVisibility(View.VISIBLE);
                phoneET.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.INVISIBLE);
                signupButton.setVisibility(View.VISIBLE);
                switchTV.setVisibility(View.INVISIBLE);
                switchTVBack.setVisibility(View.VISIBLE);
                isNewUser=true;
            }
        });

        switchTVBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameET.setVisibility(View.INVISIBLE);
                phoneET.setVisibility(View.INVISIBLE);
                spinner.setVisibility(View.INVISIBLE);
                loginButton.setVisibility(View.VISIBLE);
                signupButton.setVisibility(View.INVISIBLE);
                switchTV.setVisibility(View.VISIBLE);
                switchTVBack.setVisibility(View.INVISIBLE);
                isNewUser=false;
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateEmail(emailET)&&validatePassword(passET)){
                    signin(emailET.getText().toString(),passET.getText().toString());
                }
            }
        });


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateName(nameET)&&validateEmail(emailET)&&validatePassword(passET)&&validatePhone(phoneET)){
                    phoneNumber=phoneET.getText().toString();
                    name=nameET.getText().toString();
                    createAccount(emailET.getText().toString(),passET.getText().toString());
                }
            }
        });

        SavedActivity=LoginActivity.this;
        myCheckPermission(SavedActivity);


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
                            sendData("cityData/"+user.getUid()+"/city",city);
                            sendData("driver/"+city+"/"+user.getUid()+"/phoneNumber",phoneNumber);
                            sendData("driver/"+city+"/"+user.getUid()+"/name",name);
                        }
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        Intent intentPrev = getIntent();
                        if((getIntent()!=null)&&(intentPrev.getBooleanExtra("isFromSmsReceiver",false))){
                            intent.fillIn(getIntent(),Intent.FILL_IN_DATA);
                        }
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
                myCheckPermission(SavedActivity);
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

    public void setSpinner(){


        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        city="Chennai";
        List<String> categories = new ArrayList <String>();
        categories.add("Chennai");
        categories.add("Kancheepuram");
        categories.add("Thiruvallur");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter <String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }

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

//    private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20})";
//
//    public boolean validatePassString(final String password){
//        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
//        Matcher matcher = pattern.matcher(password);
//        return matcher.matches();
//
//    }


}
