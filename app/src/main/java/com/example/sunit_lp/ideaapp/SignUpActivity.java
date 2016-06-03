package com.example.sunit_lp.ideaapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

/**
 * Created by Sunit-LP on 4/17/2016.
 */
public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    /*static Firebase firebaseRef;
    private static final String FIREBASEREF = "https://cusexchange.firebaseio.com/";*/
    EditText sign_name;
    EditText sign_password;
    EditText sign_mobile;
    EditText sign_email;
    Button signUp;
    TextView already_login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //Firebase.setAndroidContext(this);
        MainActivity.firebaseRef = FirebaseDatabase.getInstance().getReferenceFromUrl(MainActivity.FIREBASEREF);
        sign_email =(EditText)findViewById(R.id.input_email);
        sign_mobile=(EditText)findViewById(R.id.input_mobile);
        sign_name=(EditText)findViewById(R.id.input_name);
        sign_password=(EditText)findViewById(R.id.input_password);
        signUp=(Button)findViewById(R.id.btn_signup);
        already_login=(TextView)findViewById(R.id.link_login);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
        already_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }
    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        signUp.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        final String name = sign_name.getText().toString();
        final String email = sign_email.getText().toString();
        final String password = sign_password.getText().toString();
        final String mobile=sign_mobile.getText().toString();
        // TODO: Implement your own signup logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess(name, email, password, mobile);
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    public void onSignupSuccess(String name, final String email, final String password, String mobile) {
        signUp.setEnabled(true);
        setResult(RESULT_OK, null);
        String img_url="";
        final UserReg userReg=new UserReg(name,email,mobile,img_url);
        String temp_mod=userReg.getEmail().toString();
        String temp_mod1=temp_mod.replace('.',',');
        System.out.println(temp_mod1);
        userReg.setEmail(temp_mod1);
        MainActivity.firebaseRef.child(userReg.getEmail()).child("Personal_Info").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Toast.makeText(getBaseContext(), "User already exists! Click Forgot Password to get your password", Toast.LENGTH_LONG).show();
                    finish();
                }/*else if(dataSnapshot.child(userReg)!=null){
                    Toast.makeText(getBaseContext(), "User with email already exists! Click Forgot Password to get your password", Toast.LENGTH_LONG).show();
                }*/ else {
                    String temp_mod = userReg.getEmail().toString();
                    String temp_mod1 = temp_mod.replace('.', ',');
                    System.out.println(temp_mod1);
                    userReg.setEmail(temp_mod1);
                    MainActivity.firebaseRef.child(userReg.getEmail()).child("Personal_Info").setValue(userReg);
                    createUser(email, password);
                }
                //
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Toast.makeText(SignUpActivity.this, "Seems to be some error! Please check your internet Connectivity", Toast.LENGTH_SHORT).show();
            }
        });
        //finish();
    }
    // create a new user in Firebase
    public void createUser(final String email, String password) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if(task.isSuccessful())
                    finish();
                else
                    Toast.makeText(getBaseContext(), "SignUp failed", Toast.LENGTH_LONG).show();
            }
        });/*
                new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> result) {
                        finish();
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        finish();
                    }
                });*/
    }
    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        signUp.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = sign_name.getText().toString();
        String email = sign_email.getText().toString();
        String password = sign_password.getText().toString();
        String mobile=sign_mobile.getText().toString();
        if (name.isEmpty() || name.length() < 3) {
            sign_name.setError("at least 3 characters");
            valid = false;
        } else {
            sign_name.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            sign_email.setError("enter a valid email address");
            valid = false;
        } else {
            sign_email.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            sign_password.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            sign_password.setError(null);
        }

        if (mobile.isEmpty() || mobile.length() != 10) {
            sign_mobile.setError("Mobile number is not correct");
            valid = false;
        } else {
            sign_mobile.setError(null);
        }

        return valid;
    }
}
