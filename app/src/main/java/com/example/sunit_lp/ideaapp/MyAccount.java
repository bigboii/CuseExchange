package com.example.sunit_lp.ideaapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sunit-LP on 4/14/2016.
 */
public class MyAccount extends AppCompatActivity {
    Toolbar mToolBar;
    private static final int CAMERA_REQUEST = 1888;
    private static final int CAMERA_CROP=2;
    private static final int GALLERY_PICTURE = 1;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    android.support.v7.app.ActionBar actionBar;
    MenuItem action_can;
    MenuItem action_cre;
    Menu m;
    EditText address;
    EditText mobile;
    EditText name;
    CheckBox check_email;
    CheckBox check_notifications;
    Button button_save;
    CircleImageView profile_img;
    ImageButton change_img;
    String temp_name;
    String temp_address;
    String temp_mobile;
    boolean status_email;
    boolean status_notifications;
    Uri picUri;
    Bitmap bitmap;
    String selectedImagePath;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myaccount);
        /*Firebase.setAndroidContext(this);
        MainActivity.firebaseRef = new Firebase(MainActivity.FIREBASEREF);*/
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        mToolBar=(android.support.v7.widget.Toolbar)findViewById(R.id.toolbar1);
        setSupportActionBar(mToolBar);
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        final Drawable upArrow = getResources().getDrawable(R.mipmap.ic_arrow_back_white_24dp);
        upArrow.setColorFilter(getResources().getColor(R.color.white80), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        name=(EditText)findViewById(R.id.name);
        address=(EditText)findViewById(R.id.address);
        mobile=(EditText)findViewById(R.id.mobile);
        profile_img=(CircleImageView)findViewById(R.id.imgV);
        change_img=(ImageButton)findViewById(R.id.change_pic);
        check_email=(CheckBox)findViewById(R.id.check_email);
        check_notifications=(CheckBox)findViewById(R.id.check_notifications);
        // Firebase connection to retrieve user information
        final FirebaseAuth authData = FirebaseAuth.getInstance();
        System.out.println(authData.getCurrentUser().getEmail());
        address.setText((authData.getCurrentUser().getEmail()).toString());
        String temp_mod=address.getText().toString();
        final String temp_mod1=temp_mod.replace('.', ',');
        System.out.println(temp_mod1);

        MainActivity.firebaseRef.child(temp_mod1).child("Personal_Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserReg userReg=dataSnapshot.getValue(UserReg.class);
                //Setting up values for account page
                name.setText(userReg.getName());
                mobile.setText(userReg.getMobile());
                if(!userReg.getProfile_pic().isEmpty()) {
                    byte[] imageAsBytes = Base64.decode(userReg.getProfile_pic(), 0);
                    Bitmap bmp_pic = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                    Bitmap bt = Bitmap.createScaledBitmap(bmp_pic, bmp_pic.getWidth(), bmp_pic.getHeight(), false);
                    profile_img.setImageBitmap(bt);
                }
                temp_mobile = mobile.getText().toString();
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Toast.makeText(MyAccount.this, "Seems to be some error! Please check your internet Connectivity", Toast.LENGTH_SHORT).show();
            }
        });
       /* mobile.setText(MainActivity.firebaseRef.child(temp_mod1).child("Personal_Info").child("mobile").toString());
        temp_mobile = mobile.getText().toString();*/
        /*MainActivity.firebaseRef.child(temp_mod1).child("Personal_Info").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    UserReg userReg = postSnapshot.getValue(UserReg.class);
                    if ((userReg.getEmail()).matches(temp_mod1)) {
                        System.out.println(userReg.getEmail() + " - " + userReg.getName() + " - " + userReg.getMobile());
                        name.setText(userReg.getName());
                        mobile.setText(userReg.getMobile());
                   *//* temp_name = name.getText().toString();
                    temp_address = address.getText().toString();*//*
                        temp_mobile = mobile.getText().toString();
                    }

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });*/
        name.setEnabled(false);
        address.setEnabled(false);
        mobile.setEnabled(false);
        check_email.setClickable(false);
        check_notifications.setClickable(false);
        change_img.setClickable(false);
        if(check_notifications.isChecked())
            status_notifications=true;
        else
            status_notifications=false;
        if(check_email.isChecked())
            status_email=true;
        else
            status_email=false;


        change_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*int hasCameraPermission = ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.CAMERA);
                if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[] {Manifest.permission.CAMERA},
                            REQUEST_CODE_ASK_PERMISSIONS);
                    return;
                }*/
                /*Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);*/
                startDialog();
            }
        });
        //actionBar.setTitle("Profile");
        button_save=(Button)findViewById(R.id.button_save);
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.firebaseRef.child(temp_mod1).child("Personal_Info").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        UserReg userReg=dataSnapshot.getValue(UserReg.class);
                        userReg.setMobile(mobile.getText().toString());
                        //Added for saving images in Firebase
                        //Bitmap bmp =  BitmapFactory.decodeResource(getResources(), profile_img.getImageAlpha());//your image
                        Bitmap bmp = ((BitmapDrawable) profile_img.getDrawable()).getBitmap();
                        ByteArrayOutputStream bYtE = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bYtE);
                        bmp.recycle();
                        byte[] byteArray = bYtE.toByteArray();
                        String profileImageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        userReg.setProfile_pic(profileImageFile);
                        MainActivity.firebaseRef.child(temp_mod1).child("Personal_Info").setValue(userReg);
                        Toast.makeText(MyAccount.this, "Profile Information Updated Successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {
                        Toast.makeText(MyAccount.this, "Seems to be some error! Please check your internet Connectivity", Toast.LENGTH_SHORT).show();
                    }
                });


               /* MainActivity.firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            UserReg userReg = snapshot.getValue(UserReg.class);
                            userReg.setMobile(mobile.getText().toString());
                            if ((userReg.getEmail()).matches(address.getText().toString())) {
                                String key_user = snapshot.getKey();
                                //System.out.println(key_user + ", " + name.getText().toString() + "," + address.getText().toString() + "," + mobile.getText().toString());
                                MainActivity.firebaseRef.child(key_user).setValue(userReg);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });*/

            }
        });
    }


    private void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
                this);
        myAlertDialog.setTitle("Upload Profile Picture");
        myAlertDialog.setMessage("Please select the option from below?");
        myAlertDialog.setPositiveButton("Gallery",
                new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onClick(DialogInterface arg0, int arg1) {
                        int hasGalleryPermission = ActivityCompat.checkSelfPermission(getApplicationContext(), String.valueOf(PERMISSIONS_STORAGE));
                        if (hasGalleryPermission != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MyAccount.this, PERMISSIONS_STORAGE, GALLERY_PICTURE);
                            //requestPermissions(new String[]{Manifest.permission.CAMERA}, GALLERY_PICTURE);
                            return;
                        }
                        Intent pictureActionIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pictureActionIntent,GALLERY_PICTURE);
                    }
                });
        myAlertDialog.setNegativeButton("Camera",
                new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onClick(DialogInterface arg0, int arg1) {
                        int hasCameraPermission = ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.CAMERA);
                        if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[] {Manifest.permission.CAMERA},REQUEST_CODE_ASK_PERMISSIONS);
                    return;
                }
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                      /*  File f = new File(android.os.Environment
                                .getExternalStorageDirectory(), "temp.jpg");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));*/
                        startActivityForResult(intent,CAMERA_REQUEST);
                    }
                });
        myAlertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    /*Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);*/
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent,CAMERA_REQUEST);
                } else {
                    // Permission Denied
                    Toast.makeText(MyAccount.this, "Camera Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case GALLERY_PICTURE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent pictureActionIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pictureActionIntent, GALLERY_PICTURE);
                } else {
                    // Permission Denied
                    Toast.makeText(MyAccount.this, "Gallery Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            profile_img.setImageBitmap(photo);
        }
        else if (resultCode == RESULT_OK && requestCode == GALLERY_PICTURE) {
            if (data != null) {
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                selectedImagePath = c.getString(columnIndex);
                c.close();
                if (selectedImagePath != null) {
                    //txt_image_path.setText(selectedImagePath);
                }
                bitmap = BitmapFactory.decodeFile(selectedImagePath);
                // load preview image
                //bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, false);
                profile_img.setImageBitmap(bitmap);
            } else {
                Toast.makeText(getApplicationContext(), "Cancelled",
                        Toast.LENGTH_SHORT).show();
            }
        }
       /* if (resultCode == RESULT_OK) {
            if(requestCode == CAMERA_REQUEST) {
                picUri = data.getData();
                Intent cropIntent = new Intent("com.android.camera.action.CROP");
                cropIntent.setDataAndType(picUri, "image");
                cropIntent.putExtra("crop", "true");
                cropIntent.putExtra("aspectX", 1);
                cropIntent.putExtra("aspectY", 1);
                cropIntent.putExtra("outputX", 256);
                cropIntent.putExtra("outputY", 256);
                cropIntent.putExtra("return-data", true);
                startActivityForResult(cropIntent, CAMERA_CROP);
            }
            else if (requestCode == CAMERA_CROP) {
                Bundle extras = data.getExtras();
                //Bitmap photo = (Bitmap) data.getExtras().get("data");
                Bitmap photo=extras.getParcelable("data");
                profile_img.setImageBitmap(photo);
            }
        }*/


    }
    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        bitmap = null;
        selectedImagePath = null;

        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {

            File f = new File(Environment.getExternalStorageDirectory()
                    .toString());
            for (File temp : f.listFiles()) {
                if (temp.getName().equals("temp.jpg")) {
                    f = temp;
                    break;
                }
            }

            if (!f.exists()) {

                Toast.makeText(getBaseContext(),
                        "Error while capturing image", Toast.LENGTH_LONG)
                        .show();
                return;
            }
            try {
                bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
                bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true);

                int rotate = 0;
                try {
                    ExifInterface exif = new ExifInterface(f.getAbsolutePath());
                    int orientation = exif.getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL);

                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotate = 270;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotate = 180;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotate = 90;
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Matrix matrix = new Matrix();
                matrix.postRotate(rotate);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                        bitmap.getHeight(), matrix, true);



                profile_img.setImageBitmap(bitmap);
                //storeImageTosdCard(bitmap);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else if (resultCode == RESULT_OK && requestCode == GALLERY_PICTURE) {
            if (data != null) {

                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage, filePath,
                        null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                selectedImagePath = c.getString(columnIndex);
                c.close();

                if (selectedImagePath != null) {
                    //txt_image_path.setText(selectedImagePath);
                }

                bitmap = BitmapFactory.decodeFile(selectedImagePath); // load
                // preview image
                bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, false);



                profile_img.setImageBitmap(bitmap);

            } else {
                Toast.makeText(getApplicationContext(), "Cancelled",
                        Toast.LENGTH_SHORT).show();
            }
        }

    }
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //inflater.inflate(R.menu.menu_load, menu);
        if(menu.findItem(R.id.action_cancel)==null || menu.findItem(R.id.action_create)==null)
             getMenuInflater().inflate(R.menu.menu_edit, menu);
        m=menu;
        action_can=m.findItem(R.id.action_cancel);
        action_cre=m.findItem(R.id.action_create);
        action_can.setVisible(false);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id=item.getItemId();
        switch (id){
            case R.id.action_create:
                action_cre=m.findItem(R.id.action_create);
                action_cre.setVisible(false);
                action_can=m.findItem(R.id.action_cancel);
                action_can.setVisible(true);
                change_img.setVisibility(View.VISIBLE);
                mobile.setEnabled(true);
                check_email.setClickable(true);
                check_notifications.setClickable(true);
                change_img.setClickable(true);
                return false;
            case R.id.action_cancel:
                action_can=m.findItem(R.id.action_cancel);
                action_can.setVisible(false);
                action_cre = m.findItem(R.id.action_create);
                action_cre.setVisible(true);
                change_img.setVisibility(View.INVISIBLE);
                mobile.setText(temp_mobile);
                mobile.setEnabled(false);
                check_email.setChecked(status_email);
                check_notifications.setChecked(status_notifications);
                check_email.setClickable(false);
                check_notifications.setClickable(false);
                change_img.setClickable(false);
                return false;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
