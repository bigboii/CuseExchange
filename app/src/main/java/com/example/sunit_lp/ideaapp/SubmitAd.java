package com.example.sunit_lp.ideaapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Sunit-LP on 4/13/2016.
 */
public class SubmitAd extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
    private static final int CAMERA_REQUEST = 1888;
    private static final int LOGIN_REQUEST = 1425;
    private static final int GALLERY_PICTURE = 1;
    private static final int CAMERA_CROP=2;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    Toolbar mToolBar;
    android.support.v7.app.ActionBar actionBar;
    EditText editTitle;
    EditText editdesc;
    EditText editinfo;
    EditText expPrice;
    Button submitAd;
    //--- Added for Camera
    ImageView mImageView;
    ImageButton imgButton;
    Spinner spinner1;
    Spinner spinner2;
    ImageButton fab_back;
    //Added type for Firebase
    String ad_email;
    String temp_mod1;
    String ad_title;
    String ad_description;
    String ad_info;
    String ad_price;
    String ad_category;
    String ad_category1;
    String ad_image;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    // For Gallery
    Bitmap bitmap;
    String selectedImagePath;

    //ViewPager Variables
    ArrayList<Bitmap> bitmapList;
    ViewPager viewPager;
    ViewPagerAdapter pagerAdapter;
    int currentPage = 0;
    CirclePageIndicator indicator;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submitad);
        //Firebase.setAndroidContext(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        MainActivity.firebaseRef = FirebaseDatabase.getInstance().getReferenceFromUrl(MainActivity.FIREBASEREF);
        //MainActivity.firebaseRef = new Firebase(MainActivity.FIREBASEREF);
        //Setup Q's ViewPager
        bitmapList = new ArrayList<Bitmap>();   //Q's ViewPager

        viewPager = (ViewPager) findViewById(R.id.pager);
        indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        pagerAdapter = new ViewPagerAdapter(SubmitAd.this, bitmapList);
        viewPager.setAdapter(pagerAdapter);
        indicator.setViewPager(viewPager);

        final float density = getResources().getDisplayMetrics().density;                   //set indicator sizing
        indicator.setRadius(5 * density);

        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageSelected(int position)
            {
                currentPage = position;
            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2)
            {
            }

            @Override
            public void onPageScrollStateChanged(int pos) {
            }
        });


        //Other submit ad stuff
        /*
        mToolBar=(android.support.v7.widget.Toolbar)findViewById(R.id.toolbar9);
        setSupportActionBar(mToolBar);
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        //Rename Toolbar title
        getSupportActionBar().setTitle("Submit Ad");
        */
        fab_back=(ImageButton)findViewById(R.id.fab_back);
        fab_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mImageView=(ImageView)findViewById(R.id.imageView);
        imgButton=(ImageButton)findViewById(R.id.fab_camera);
        //actionBar.setTitle("Submit Ad");
        // Spinner element
        spinner1 = (Spinner) findViewById(R.id.categories);
        // Spinner click listener
        spinner1.setOnItemSelectedListener(this);
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, getResources().getStringArray(R.array.categories_array) );
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        // attaching data adapter to spinner
        spinner1.setAdapter(dataAdapter);
        // Spinner click listener
        spinner1.setOnItemSelectedListener(this);
        editTitle=(EditText)findViewById(R.id.addtitle);
        editdesc=(EditText)findViewById(R.id.describe);
        editinfo=(EditText)findViewById(R.id.addinfo);
        expPrice=(EditText)findViewById(R.id.expPrice);
        submitAd=(Button)findViewById(R.id.button_submitad);

        submitAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAdvertisement();
            }
        });

        imgButton.setOnClickListener(new View.OnClickListener()
        {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v)
            {
                startDialog();
            }
        });
    }

    private void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle("Upload Pictures");
        myAlertDialog.setMessage("Please select the option from below?");
        myAlertDialog.setPositiveButton("Gallery",
                new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onClick(DialogInterface arg0, int arg1) {
                        int hasGalleryPermission = ActivityCompat.checkSelfPermission(getApplicationContext(), String.valueOf(PERMISSIONS_STORAGE));
                        if (hasGalleryPermission != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(SubmitAd.this, PERMISSIONS_STORAGE, GALLERY_PICTURE);
                            //requestPermissions(new String[]{Manifest.permission.CAMERA}, GALLERY_PICTURE);
                            return;
                        }

                        //Intent pictureActionIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        //startActivityForResult(pictureActionIntent,GALLERY_PICTURE);

                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);
                    }
                });
        myAlertDialog.setNegativeButton("Camera",
                new DialogInterface.OnClickListener()
                {
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onClick(DialogInterface arg0, int arg1)
                    {
                        int hasCameraPermission = ContextCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.CAMERA);
                        if (hasCameraPermission != PackageManager.PERMISSION_GRANTED)
                        {
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_ASK_PERMISSIONS);
                            return;
                        }
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                      /*  File f = new File(android.os.Environment
                                .getExternalStorageDirectory(), "temp.jpg");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));*/
                        startActivityForResult(intent, CAMERA_REQUEST);
                    }
                });
        myAlertDialog.show();
    }

    public void submitAdvertisement()
    {
        Log.d("SubmitAd", "SubmitAd");

        if (!validate()) {
            onSubmitFailed();
            return;
        }

        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth authData) {
                if (authData == null) {
                    //user not logged in
                   /* LoginActivity obj=new LoginActivity();
                    obj.callLoginDialog();*/
                    Intent loginIntent = new Intent(SubmitAd.this, LoginActivity.class);
                    startActivityForResult(loginIntent, LOGIN_REQUEST);
                } else {
                    submitAd.setEnabled(false);

                    final ProgressDialog progressDialog = new ProgressDialog(SubmitAd.this,
                            R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Posting Ad..");
                    progressDialog.show();

                    System.out.println(authData.getCurrentUser().getEmail());
                    ad_email = authData.getCurrentUser().getEmail().toString();
                    temp_mod1 = ad_email.replace('.', ',');
                    System.out.println(temp_mod1);
                    ad_title = editTitle.getText().toString();
                    ad_description = editdesc.getText().toString();
                    ad_info = editinfo.getText().toString();
                    ad_price = expPrice.getText().toString();
                    ad_category = spinner1.getSelectedItem().toString();
                    ad_category1 = spinner2.getSelectedItem().toString();

                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    // Calling onSubmitSuccess to update ad to the firebase
                                    onSubmitSuccess(ad_info, ad_category, ad_description, ad_price, ad_category1, ad_title, temp_mod1);
                                    progressDialog.dismiss();
                                }
                            }, 3000);
                }
            }
        });

    }


    //Perform
    public void onSubmitSuccess(String Address,String Category,String Description,String Price, String SubCategory,String Title,String temp_mod1)
    {
        submitAd.setEnabled(true);
        Toast.makeText(getBaseContext(), "Ad Posted Successfully!", Toast.LENGTH_LONG).show();
        finish();
        setResult(RESULT_OK, null);
        final UserAd userAd=new UserAd(Address,Category,Description,Price,SubCategory,Title,temp_mod1);
        Calendar c = Calendar.getInstance();
        int seconds = c.get(Calendar.SECOND);
        int minutes=c.get(Calendar.MINUTE);
        int hours=c.get(Calendar.HOUR);
        int day=c.get(Calendar.DATE);
        int month=c.get(Calendar.MONTH);
        int year=c.get(Calendar.YEAR);
        int a = c.get(Calendar.AM_PM);
        String monthName = String.format(Locale.US,"%tB",c);
        String postDate;
        if(a == Calendar.AM)
            postDate=day+" "+monthName+" "+year+" "+hours+":"+minutes+":"+seconds+" AM";
        else
            postDate=day+" "+monthName+" "+year+" "+hours+":"+minutes+":"+seconds+" PM";
        /*if(mImageView.getDrawable()!=null)
        {
            Bitmap bmp = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
            ByteArrayOutputStream bYtE = new ByteArrayOutputStream();

            Log.d("ImageCompression","Before bmp" + bmp.getByteCount());
            bmp.compress(Bitmap.CompressFormat.JPEG, 10, bYtE);
            bmp.recycle();
            Log.d("ImageCompression", "AFTER bmp: " + bmp.getByteCount());
            Log.d("ImageCompression", "AFTER bYtE: " + bYtE.size());

            byte[] byteArray = bYtE.toByteArray();
            String profileImageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);
            userAd.setUrl_img(profileImageFile);
            //userAd.setDate();
        }*/
        userAd.setDate(postDate);
        while(bitmapList.size()<5)
        {
            bitmapList.add(null);
        }
        DatabaseReference tempref=MainActivity.firebaseRef.child(temp_mod1).child("IndividualAds").push()/*.setValue(userAd)*/;
        //Create Firebaase Storage Reference this url is unique
        StorageReference storageReference=MainActivity.storage.getReferenceFromUrl("gs://project-7354348151753711110.appspot.com");
        //Pointing to the testing folder
        StorageReference imagesRef=storageReference.child(userAd.getEmail());
        StorageReference imagesRef2=imagesRef.child("IndividualAds");
        StorageReference imagesRef1=imagesRef2.child(tempref.getKey());
        for(int i=0;i<4;i++) {
            //Pointing to the image
            String filename="Url_img"+i;
            if (bitmapList.get(i) != null) {
                //Bitmap bmp = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
                StorageReference spaceRef=imagesRef1.child(filename);
                String path=userAd.getEmail()+"/IndividualAds/"+tempref.getKey()+"/"+filename;

                Bitmap bmp = bitmapList.get(i);
                ByteArrayOutputStream bYtE = new ByteArrayOutputStream();

                Log.d("ImageCompression", "Before bmp" + bmp.getByteCount());
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, bYtE);
                bmp.recycle();
                Log.d("ImageCompression", "AFTER bmp: " + bmp.getByteCount());
                Log.d("ImageCompression", "AFTER bYtE: " + bYtE.size());

                byte[] byteArray = bYtE.toByteArray();
                //String profileImageFile = Base64.encodeToString(byteArray, Base64.DEFAULT);
                //Uploading the file to the FileStorage
                UploadTask uploadTask = spaceRef.putBytes(byteArray);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        System.out.println("The url for the image is "+downloadUrl);
                        //userReg.setProfile_pic(downloadUrl.toString());
                    }
                });
                switch(i)
                {
                    case 0:
                        userAd.setUrl_img(path);
                        break;
                    case 1:
                        userAd.setUrl_img1(path);
                        break;
                    case 2:
                        userAd.setUrl_img2(path);
                        break;
                    case 3:
                        userAd.setUrl_img3(path);
                        break;
                }
                //userAd.setDate();
            } else {
                switch(i)
                {
                    case 0:
                        userAd.setUrl_img("");
                        break;
                    case 1:
                        userAd.setUrl_img1("");
                        break;
                    case 2:
                        userAd.setUrl_img2("");
                        break;
                    case 3:
                        userAd.setUrl_img3("");
                        break;
                }
            }
        }
        //get key
        userAd.setKey(tempref.getKey());
        tempref.setValue(userAd);
    }
   /* @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                } else {
                    // Permission Denied
                    Toast.makeText(SubmitAd.this, "Camera Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
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
                    Toast.makeText(SubmitAd.this, "Camera Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case GALLERY_PICTURE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent pictureActionIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pictureActionIntent, GALLERY_PICTURE);
                } else {
                    // Permission Denied
                    Toast.makeText(SubmitAd.this, "Gallery Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            bitmapList.add(photo);                                           //Q's Viewpager
            mImageView.setImageBitmap(photo);
        }
        if (resultCode == RESULT_OK && requestCode == GALLERY_PICTURE)
        {
            if (data != null)
            {
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
                bitmapList.add(bitmap);                                           //Q's Viewpager
                // load preview image
                //bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, false);
                mImageView.setImageBitmap(bitmap);
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Cancelled",
                        Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == LOGIN_REQUEST && resultCode == RESULT_OK) {
            //finish();

            final ProgressDialog progressDialog = new ProgressDialog(SubmitAd.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Posting Ad..");
            progressDialog.show();

            final String ad_title = editTitle.getText().toString();
            final String ad_description = editdesc.getText().toString();
            final String ad_info = editinfo.getText().toString();
            final String ad_price=expPrice.getText().toString();

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            // Calling onSubmitSuccess to update ad to the firebase
                            onSubmitSuccess(ad_info,ad_category,ad_description,ad_price,ad_category1,ad_title,temp_mod1);
                            progressDialog.dismiss();
                        }
                    }, 3000);
        }
        /*if (resultCode == RESULT_OK) {
            if(requestCode == CAMERA_REQUEST) {
                picUri = data.getData();
                Intent cropIntent = new Intent("com.android.camera.action.CROP");
                cropIntent.setDataAndType(picUri, "image*//*");
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

        //Q's Setup Viewpager
        pagerAdapter.notifyDataSetChanged();

    }
    public void onSubmitFailed() {
        Toast.makeText(getBaseContext(), "Posting an Ad failed!", Toast.LENGTH_LONG).show();
        submitAd.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String ad_title = editTitle.getText().toString();
        String ad_description = editdesc.getText().toString();
        String ad_info = editinfo.getText().toString();
        String ad_price=expPrice.getText().toString();
        String ad_category=spinner1.getSelectedItem().toString();

        if (ad_category.contains("Select Category")) {
            //Toast.makeText(getBaseContext(), "You need to select the Category", Toast.LENGTH_LONG).show();
            valid = false;
        }
        if (ad_title.isEmpty() || ad_title.length() < 3) {
            editTitle.setError("at least 3 characters");
            valid = false;
        } else {
            editTitle.setError(null);
        }

        if (ad_description.isEmpty()) {
            editdesc.setError("enter description of product");
            valid = false;
        } else {
            editdesc.setError(null);
        }
        if(editinfo.isShown())
        {
            if(ad_info.length()<6){
                editinfo.setError("Enter your complete address");
                valid = false;
            }
            else {
                editinfo.setError(null);
            }
        }
        if (ad_price.isEmpty()) {
            expPrice.setError("Price cannot be empty ");
            valid = false;
        } else {
            expPrice.setError(null);
        }
        return valid;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //inflater.inflate(R.menu.menu_load, menu);
        if(menu.findItem(R.id.action_delete)==null)
            getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id=item.getItemId();
        switch (id){
            case R.id.action_delete:
                return false;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinner2 = (Spinner) findViewById(R.id.subcategories);
        ArrayAdapter<String> sub1 = new ArrayAdapter<String>(this, R.layout.item_spinner_dropdown, getResources().getStringArray(R.array.subcategories_Books) );
        ArrayAdapter<String> sub2 = new ArrayAdapter<String>(this, R.layout.item_spinner_dropdown, getResources().getStringArray(R.array.subcategories_Housing) );
        ArrayAdapter<String> sub3 = new ArrayAdapter<String>(this, R.layout.item_spinner_dropdown, getResources().getStringArray(R.array.subcategories_HomeAccessories) );
        ArrayAdapter<String> sub4 = new ArrayAdapter<String>(this, R.layout.item_spinner_dropdown, getResources().getStringArray(R.array.subcategories_Valuables) );
        switch (position){
            case 1:
                sub1.setDropDownViewResource(R.layout.item_spinner_dropdown);
                spinner2.setAdapter(sub1);
                editinfo.setVisibility(View.GONE);
                break;
            case 2:
                sub1.setDropDownViewResource(R.layout.item_spinner_dropdown);
                spinner2.setAdapter(sub2);
                editinfo.setVisibility(View.VISIBLE);
                break;
            case 3:
                sub1.setDropDownViewResource(R.layout.item_spinner_dropdown);
                spinner2.setAdapter(sub3);
                editinfo.setVisibility(View.GONE);

                break;
            case 4:
                sub1.setDropDownViewResource(R.layout.item_spinner_dropdown);
                spinner2.setAdapter(sub4);
                editinfo.setVisibility(View.GONE);
                break;
            default:
                List<String> categories = new ArrayList<String>();
                ArrayAdapter<String> sub5 = new ArrayAdapter<String>(this, R.layout.item_spinner_dropdown, categories );
                spinner2.setAdapter(sub5);
                break;
        }
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }


    /**
     *  Custom adapter for SubmitAd activity's ViewPager
     * */
    public class ViewPagerAdapter extends PagerAdapter
    {
        int count;

        private ArrayList<Bitmap> bitmapList;
        private LayoutInflater inflater;
        private Context mContext;

        //Constructor
        public ViewPagerAdapter(Context context, ArrayList<Bitmap> imageList)
        {
            mContext = context;
            bitmapList = imageList;
            inflater = getLayoutInflater();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            container.removeView((View) object);
        }

        @Override
        public int getCount()
        {
            return bitmapList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position)
        {
            View imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false);

            assert imageLayout != null;
            final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);

            imageView.setImageBitmap(bitmapList.get(position));
            view.addView(imageLayout, 0);

            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

    }//End of Viewpager
}
