package com.example.sunit_lp.ideaapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity
{
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    EditText emailaddr;
    EditText passwordText;
    Button login;
    Button signup;
    Button forgotPassword;
   /* private static final String FIREBASEREF = "https://cusexchange.firebaseio.com/";
    static Firebase firebaseRef;*/
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Firebase.setAndroidContext(this);
        MainActivity.firebaseRef = FirebaseDatabase.getInstance().getReferenceFromUrl(MainActivity.FIREBASEREF);
        //setContentView(R.layout.activity_login);
        callLoginDialog();
    }
    public void callLoginDialog()
    {
        final Dialog myDialog = new Dialog(this);
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.activity_login);
        myDialog.show();
        myDialog.setCancelable(false);
        //myDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        login = (Button) myDialog.findViewById(R.id.a_login_btn);
        signup=(Button)myDialog.findViewById(R.id.a_signup_btn);
         emailaddr = (EditText) myDialog.findViewById(R.id.a_login_et_email);
        passwordText = (EditText) myDialog.findViewById(R.id.a_login_et_password);
        forgotPassword=(Button)myDialog.findViewById(R.id.a_login_tv_findpw);

        //Clicked outside the dialog
        myDialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    finish();
                    myDialog.dismiss();
                }
                return false;
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activity_signup = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(activity_signup);
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callForgotDiaplog();
            }
        });
    }

    public void callForgotDiaplog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
        alertDialog.setMessage("Provide your email address");
        final EditText input = new EditText(LoginActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("Submit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String forgotEmail = input.getText().toString();
                        FirebaseAuth.getInstance().sendPasswordResetEmail(forgotEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(Task<Void> task) {
                                if (task.isSuccessful())
                                    Toast.makeText(getBaseContext(), "Password reset email sent successfully!", Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(getBaseContext(), "The specified user account does not exist.", Toast.LENGTH_LONG).show();
                            }

                        });/*.resetPassword(forgotEmail, new Firebase.ResultHandler() {
                            @Override
                            public void onSuccess() {

                                Toast.makeText(getBaseContext(), "Password reset email sent successfully!", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onError(DatabaseError firebaseError) {
                                Toast.makeText(getBaseContext(), "The specified user account does not exist.", Toast.LENGTH_LONG).show();
                            }
                        });*/
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        login.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        final String email = emailaddr.getText().toString();
        final String password = passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess(email,password);
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }
    public boolean validate() {
        boolean valid = true;

        String email = emailaddr.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailaddr.setError("enter a valid email address");
            valid = false;
        } else {
            emailaddr.setError(null);
        }

        if (password.isEmpty() || password.length() < 4) {
            passwordText.setError("Password is too small");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }
    public void onLoginSuccess(String email, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    login.setEnabled(true);
                    finish();
                }
                else{
                    onLoginFailed();
                }
            }
        });/*.authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                login.setEnabled(true);
                *//*MainActivity.firebaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            UserReg userReg = postSnapshot.getValue(UserReg.class);
                            System.out.println(userReg.getEmail() + " - " + userReg.getName() + " - " + userReg.getMobile());
                            MainActivity.header_name.setText(userReg.getName());
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        System.out.println("The read failed: " + firebaseError.getMessage());
                    }
                });*//*
                finish();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                onLoginFailed();
            }
        });*/
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login Failed, Please check your login credentials!", Toast.LENGTH_LONG).show();

        login.setEnabled(true);
    }
}
