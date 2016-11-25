package com.sdsmdg.maps;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by rahul on 25/11/16.
 */
public class LoginActivity extends AppCompatActivity{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String TAG="loginactivity";
    private boolean isNewUser=false;
    private String phoneNumber="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        final EditText emailET= (EditText)findViewById(R.id.emailET);
        final EditText passET= (EditText)findViewById(R.id.passET);
        final TextView switchTV = (TextView)findViewById(R.id.signupTV);
        final TextView switchTVBack = (TextView)findViewById(R.id.signupTV2);
        final Button loginButton=(Button)findViewById(R.id.login);
        final EditText phoneET= (EditText)findViewById(R.id.phoneET);
        final Button signupButton=(Button)findViewById(R.id.signup);

        switchTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneET.setVisibility(View.VISIBLE);
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
                phoneET.setVisibility(View.INVISIBLE);
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
                signin(emailET.getText().toString(),passET.getText().toString());
            }
        });


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber=phoneET.getText().toString();
                createAccount(emailET.getText().toString(),passET.getText().toString());
            }
        });



    }

    private void init(){
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    if(isNewUser){
                        sendData("driver/"+user.getUid()+"/phoneNumber",phoneNumber);
                    }
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
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

}
