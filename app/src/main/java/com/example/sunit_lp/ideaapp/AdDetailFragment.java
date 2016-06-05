package com.example.sunit_lp.ideaapp;


import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdDetailFragment extends Fragment
{
    private static final String ARG_USERAD_DATA = "USERAD";
    private static final String ARG_AD_DATA = "FIREBASE_AD";
    public static final int LOGIN_REQUEST = 12345;
    ImageView ivDetail;
    ImageView ivProfileImage;
    TextView tvTitle;
    TextView tvPrice;
    TextView tvSeller;
    TextView tvDescription;
    TextView tvDate;                //Missing in firebase
    HashMap<String, ?> adInfo;
    UserAd userAd;
    UserReg userReg;
    Toolbar toolbarDetail;
    String toolbar_title;
    // To get mobile number of seller
    String mobile_seller;
    String email_seller;
    // For Circular FAB
    FloatingActionButton actionButton;
    FloatingActionMenu rightLowerMenu;
    ImageView rlIcon1;
    ImageView rlIcon2;
    ImageView rlIcon3;
    SubActionButton.Builder itemBuilder;
    ImageView iv_contact;

    //Firebase
    DatabaseReference firebaseRef1;
    DatabaseReference firebaseRef2;
    FirebaseAuth.AuthStateListener authStateListener;
    //Share Provider
    //ImageView item_iv_share;
    ShareActionProvider mShareActionProvider;

    public AdDetailFragment()
    {
        // Required empty public constructor
    }

    //Get all Ads from firebase
    public void getSellerInfo()
    {
        String userPath="https://cusexchange.firebaseio.com/"+userAd.getEmail()+"/Personal_Info";
        firebaseRef1 = FirebaseDatabase.getInstance().getReferenceFromUrl(userPath);

        firebaseRef1.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                userReg = dataSnapshot.getValue(UserReg.class);
                tvSeller.setText((String) userReg.getName());
                mobile_seller = userReg.getMobile().toString();
                email_seller = userReg.getEmail().toString();


                //Setting image
                if (!(userReg.getProfile_pic().isEmpty()))
                {
                    //byte[] imageAsBytes = Base64.decode(userReg.getProfile_pic(), 0);
                    //Bitmap bmp_pic = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);

                    //byte[] imageAsBytes = Base64.decode(userReg.getProfile_pic(), 0);
                    //Bitmap bmp_pic = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                    //Bitmap bt = Bitmap.createScaledBitmap(bmp_pic, bmp_pic.getWidth(), bmp_pic.getHeight(), false);
                    //ivProfileImage.setImageBitmap(bt);
                    try {
                        String encodedpath= URLEncoder.encode(userReg.getProfile_pic(), "utf-8");
                        System.out.println("Encoded String"+encodedpath);
                        // Create a reference from an HTTPS URL
                        // Note that in the URL, characters are URL escaped!
                        StorageReference httpsReference = MainActivity.storage.getReferenceFromUrl("https://firebasestorage.googleapis.com/v0/b/project-7354348151753711110.appspot.com/o/"+encodedpath);
                        //Download the file now
                        final long ONE_MEGABYTE=1024*1024;
                        httpsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                //Data for "image" is returns, use this as needed
                                Bitmap bmp_pic = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                Bitmap bt = Bitmap.createScaledBitmap(bmp_pic, bmp_pic.getWidth(), bmp_pic.getHeight(), false);
                                ivProfileImage.setImageBitmap(bt);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Handle any errors
                            }
                        });
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });

    }

    //Returns a new instance of this fragment
    public static AdDetailFragment newInstance(HashMap<String, ?> dummy)
    {
        AdDetailFragment fragment = new AdDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_AD_DATA, dummy);         //Allows fragments to send data to other fragments
        fragment.setArguments(args);
        return fragment;
    }

    public static AdDetailFragment newInstance(UserAd ad)
    {
        AdDetailFragment fragment = new AdDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USERAD_DATA, ad);         //Allows fragments to send data to other fragments
        fragment.setArguments(args);
        return fragment;
    }


    /* Used to receive movie data from inside this fragment */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Firebase.setAndroidContext(getContext());
        //setRetainInstance(true);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);                    //Dont detail fragment let rotate

        //Hide the drawer
        /*((MainActivity)getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);*/
        //allows MovieFragment to load MovieData when being swapped by MovieListFragment
        if (getArguments() != null) {
            //adInfo = (HashMap<String, ?>) getArguments().getSerializable(ARG_MOVIE_DATA);
            userAd = (UserAd) getArguments().getSerializable(ARG_USERAD_DATA);
        }

        getSellerInfo();

        //Initialize AuthStateListener

        authStateListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(FirebaseAuth authData)
            {
                if (authData == null)
                {
                    //user not logged in
//                    if (getActivity() == null)
//                        //Log.d("LOGGINGOUT", "I AM NULL");
//                    else
//                        //Log.d("LOGGINGOUT", "Not NULL");
                    Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                    //getActivity().finish();
                    startActivityForResult(loginIntent, LOGIN_REQUEST);
                }
                else
                {

                    // Build the menu with default options: light theme, 90 degrees, 72dp radius.
                    // Set 4 default SubActionButtons
                    rightLowerMenu = new FloatingActionMenu.Builder(getActivity())
                            .addSubActionView(itemBuilder.setContentView(rlIcon1).build())
                            .addSubActionView(itemBuilder.setContentView(rlIcon2).build())
                            .addSubActionView(itemBuilder.setContentView(rlIcon3).build())
                            .attachTo(actionButton)
                            .build();

                    // Listen menu open and close events to animate the button content view
                    rightLowerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
                        @Override
                        public void onMenuOpened(FloatingActionMenu menu) {
                            // Rotate the icon of rightLowerButton 45 degrees clockwise
                            //Toast.makeText(getActivity(), "Contact Seller", Toast.LENGTH_SHORT).show();
                            iv_contact.setRotation(0);
                            PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                            ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(iv_contact, pvhR);
                            animation.start();
                        }

                        @Override
                        public void onMenuClosed(FloatingActionMenu menu) {
                            // Rotate the icon of rightLowerButton 45 degrees counter-clockwise
                            iv_contact.setRotation(45);
                            PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                            ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(iv_contact, pvhR);
                            animation.start();
                        }
                    });

                }
            }
        };
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_ad_detail, container, false);                              //In Fragments, you have to inflate them

        //Hide Toolbar

       /* ((MainActivity)getActivity()).getSupportActionBar().hide();    */        //hide old toolbar
        toolbarDetail = (Toolbar) rootView.findViewById(R.id.toolbar_detail);  //use new toolbar
        ((MainActivity)getActivity()).setSupportActionBar(toolbarDetail);
        ((MainActivity)getActivity()).toolbar.setVisibility(View.GONE);
        //((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //((MainActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(Color.TRANSPARENT);

        setHasOptionsMenu(true);
        ivDetail = (ImageView) rootView.findViewById(R.id.iv_detail);
        ivProfileImage = (ImageView) rootView.findViewById(R.id.profile_image);
        tvTitle = (TextView) rootView.findViewById(R.id.tv_title);
        tvSeller = (TextView) rootView.findViewById(R.id.tv_seller);
        tvPrice = (TextView) rootView.findViewById(R.id.tv_price);
        tvDescription = (TextView) rootView.findViewById(R.id.tv_description);
        tvDate = (TextView) rootView.findViewById(R.id.tv_date);

        tvPrice.setTransitionName((String) userAd.getPrice() + "_tv2");           //for shared element transition
        tvTitle.setTransitionName((String) userAd.getTitle() + "_tv");            //for shared element transition
        //iv_contact=(ImageView)rootView.findViewById(R.id.iv_contact);
        //ivDetail.setImageResource((Integer) (adInfo.get("url_img")));
        // Adding FAB implementation
        iv_contact = new ImageView(getActivity());
        iv_contact.setImageDrawable(getResources().getDrawable(R.mipmap.ic_contacts_black_18dp));
        actionButton = new FloatingActionButton.Builder(getActivity())
                .setContentView(iv_contact).setBackgroundDrawable(R.drawable.oval)
                .build();
        //Setting parameters for CFAB
        FloatingActionButton.LayoutParams params= (FloatingActionButton.LayoutParams) actionButton.getLayoutParams();
        //params.setMargins(5,5,5,5);
        params.height=200;
        params.width=200;
       /* actionButton.removeAllViews();
        actionButton=new FloatingActionButton.Builder(getActivity())
                .setContentView(iv_contact).setBackgroundDrawable(R.drawable.oval).setLayoutParams(params)
                .build();*/
        itemBuilder = new SubActionButton.Builder(getActivity());
        // repeat many times:
        rlIcon1 = new ImageView(getActivity());
        rlIcon2 = new ImageView(getActivity());
        rlIcon3 = new ImageView(getActivity());

        rlIcon1.setImageDrawable(getResources().getDrawable(R.mipmap.ic_call_black_24dp));
        rlIcon2.setImageDrawable(getResources().getDrawable(R.mipmap.ic_email_black_24dp));
        rlIcon3.setImageDrawable(getResources().getDrawable(R.mipmap.ic_sms_black_24dp));

        itemBuilder.setLayoutParams(params);

        //firebaseRef2=FirebaseAuth.getInstance().getCurrentUser();//.getReferenceFromUrl("https://cusexchange.firebaseio.com/");

        //FAB contact button
        //user not logged in
        if (FirebaseAuth.getInstance().getCurrentUser() == null)                          //if not logged in  -> setListener
        {
            Log.d("LOGGINGOUT", "Auth NULL");

            actionButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //Toast.makeText(getActivity(), "Contact Seller", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().addAuthStateListener(authStateListener);


                }
            });

        }
        else                                         //if logged in  ->  prompt login
        {
            Log.d("LOGGINGOUT", "Auth NOT NULL");
            // Build the menu with default options: light theme, 90 degrees, 72dp radius.
            // Set 4 default SubActionButtons
            rightLowerMenu = new FloatingActionMenu.Builder(getActivity())
                    .addSubActionView(itemBuilder.setContentView(rlIcon1).build())
                    .addSubActionView(itemBuilder.setContentView(rlIcon2).build())
                    .addSubActionView(itemBuilder.setContentView(rlIcon3).build())
                    .attachTo(actionButton)
                    .build();

            // Listen menu open and close events to animate the button content view
            rightLowerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener()
            {
                @Override
                public void onMenuOpened(FloatingActionMenu menu)
                {
                    // Rotate the icon of rightLowerButton 45 degrees clockwise
                    //Toast.makeText(getActivity(), "Contact Seller", Toast.LENGTH_SHORT).show();
                    iv_contact.setRotation(0);
                    PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                    ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(iv_contact, pvhR);
                    animation.start();
                }

                @Override
                public void onMenuClosed(FloatingActionMenu menu)
                {
                    // Rotate the icon of rightLowerButton 45 degrees counter-clockwise
                    iv_contact.setRotation(45);
                    PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                    ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(iv_contact, pvhR);
                    animation.start();
                }
            });
        }

        //Call seller
        rlIcon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callNumber();
            }
        });
        //Email seller
        rlIcon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //SMS seller
        rlIcon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMSMessage();
            }
        });


        //Setting Texts
        ivDetail.setTransitionName((String) userAd.getTitle());                   //for shared element transition
        tvTitle.setText((String) userAd.getTitle());
        //tvSeller.setText((String) userReg.getName());
        tvDescription.setText((String) userAd.getDescription());
        tvPrice.setText( "$" + ((String) userAd.getPrice()));
        tvDate.setText("Posted on "+(String)userAd.getDate());
        //tvDate.setText(String) adInfo.get("date"));         //date feature missing
        //Setting image
        if (!(userAd.getUrl_img().isEmpty()))
        {
            /*byte[] imageAsBytes = Base64.decode(userAd.getUrl_img(), 0);
            Bitmap bmp_pic = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);

            //byte[] imageAsBytes = Base64.decode(userReg.getProfile_pic(), 0);
            //Bitmap bmp_pic = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
            Bitmap bt=Bitmap.createScaledBitmap(bmp_pic, bmp_pic.getWidth(), bmp_pic.getHeight(), false);*/
            try {
                String encodedpath= URLEncoder.encode(userAd.getUrl_img(), "utf-8");
                System.out.println("Encoded String"+encodedpath);
                // Create a reference from an HTTPS URL
                // Note that in the URL, characters are URL escaped!
                StorageReference httpsReference = MainActivity.storage.getReferenceFromUrl("https://firebasestorage.googleapis.com/v0/b/project-7354348151753711110.appspot.com/o/"+encodedpath);
                //Download the file now
                final long ONE_MEGABYTE=1024*1024;
                httpsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        //Data for "image" is returns, use this as needed
                        Bitmap bmp_pic = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        Bitmap bt = Bitmap.createScaledBitmap(bmp_pic, bmp_pic.getWidth(), bmp_pic.getHeight(), false);
                        ivDetail.setImageBitmap(bt);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Handle any errors
                    }
                });
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //ivDetail.setImageBitmap(bt);
        }



        //ivDetail.setImageResource(R.drawable.detail);
        // Inflate the layout for this fragment
        return rootView;
    }


    private void callNumber() {
        // Note that this ACTION_CALL requires permission
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + mobile_seller));
        startActivity(callIntent);
    }

    private void sendSMSMessage() {
        try {
            //userAd.getDataSnapshot().getRef().removeEventListener();
            Uri uri = Uri.parse("smsto:"+mobile_seller);
            // No permisison needed
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
            startActivity(smsIntent);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "SMS failed, please try again later!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater)
    {
        if(menu.findItem(R.id.action_share)==null)
        {
            toolbar_title=((MainActivity)getActivity()).getSupportActionBar().getTitle().toString();
            ((MainActivity)getActivity()).getSupportActionBar().setTitle("");
            inflater.inflate(R.menu.menu_detail_frag, menu);
        }
        //inflater.inflate(R.menu.menu_load, menu);
        MenuItem shareItem=menu.findItem(R.id.action_share);
        Drawable newIcon = (Drawable)shareItem.getIcon();
        newIcon.mutate().setColorFilter(getResources().getColor(R.color.syracuse_orange), PorterDuff.Mode.SRC_IN);
        mShareActionProvider=(ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        Intent intentShare=new Intent(Intent.ACTION_SEND);
        intentShare.setType("text/plain");
        intentShare.putExtra(Intent.EXTRA_TEXT, "Title is " + userAd.getTitle() + ",Price is " + userAd.getPrice());//+", Seller number is "+mobile_seller
        mShareActionProvider.setShareIntent(intentShare);
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id=item.getItemId();
        switch (id){
            case R.id.action_share:
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        actionButton.detach();
        //actionButton.setVisibility(View.GONE);
        //Commented as code update takes care
        //((MainActivity)getActivity()).getSupportActionBar().setTitle(toolbar_title);
        ((MainActivity)getActivity()).toolbar.setVisibility(View.VISIBLE);
        ((MainActivity)getActivity()).setSupportActionBar(((MainActivity) getActivity()).toolbar);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);

        ((MainActivity)getActivity()).actionBarDrawerToggle=new ActionBarDrawerToggle(((MainActivity)getActivity()),((MainActivity)getActivity()).drawerLayout,((MainActivity)getActivity()).toolbar,R.string.drawer_open,R.string.drawer_close)
        {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        ((MainActivity)getActivity()).drawerLayout.setDrawerListener(((MainActivity)getActivity()).actionBarDrawerToggle);
        ((MainActivity)getActivity()).actionBarDrawerToggle.syncState();
       /* ((MainActivity)getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(true);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);*/
        /*((MainActivity)getActivity()).getSupportActionBar().hide();            //hide old toolbar
        ((MainActivity)getActivity()).setSupportActionBar(((MainActivity) getActivity()).toolbar);
        ((MainActivity)getActivity()).actionBarDrawerToggle.syncState();
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        if(rightLowerMenu!=null) {
            if (rightLowerMenu.isOpen()) {
                rightLowerMenu.close(true);
            }
        }

        if(authStateListener != null)
            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);              //allow screen to rotate again
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //super.onActivityResult(requestCode, resultCode, data);
        Log.d("OnActivityResult", "resultCode == " + resultCode);
        //Log.d("OnActivityResult", "requestCode == " + requestCode + "\tLOGIN_REQUEST: "+ FRAG_LOGIN_REQUEST);

        if (requestCode == AdDetailFragment.LOGIN_REQUEST/* && resultCode == getActivity().RESULT_OK*/) {

        Log.d("OnActivityResult", "RETURNED");
        /*
        rightLowerMenu = new FloatingActionMenu.Builder(getActivity())
                .addSubActionView(itemBuilder.setContentView(rlIcon1).build())
                .addSubActionView(itemBuilder.setContentView(rlIcon2).build())
                .addSubActionView(itemBuilder.setContentView(rlIcon3).build())
                .attachTo(actionButton)
                .build();
        */

        // Listen menu open and close events to animate the button content view
        rightLowerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees clockwise
                //Toast.makeText(getActivity(), "Contact Seller", Toast.LENGTH_SHORT).show();
                iv_contact.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(iv_contact, pvhR);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees counter-clockwise
                iv_contact.setRotation(45);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(iv_contact, pvhR);
                animation.start();
            }
        });
    }

    }

}
