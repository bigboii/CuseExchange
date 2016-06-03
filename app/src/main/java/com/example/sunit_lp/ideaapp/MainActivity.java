package com.example.sunit_lp.ideaapp;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,OnItemSelectedListener, OnBackPressedListener {
    public static final String FIREBASEREF = "https://cusexchange.firebaseio.com/";
    public static final int FRAG_LOGIN_REQUEST = 12345;

    Fragment mFragment;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    //private static final String FIREBASEREF = "https://cusexchange.firebaseio.com/";
    static DatabaseReference firebaseRef;
    Toolbar toolbar;
    TextView header_name;
    DrawerLayout drawerLayout;
    View headerView;
    CircleImageView header_profile;
    TextView header_email;
    String toolbar_title;
    //Filter Variables
    boolean isFilterApplied;
    String mainCatFilter;
    String subCatFilter;
    String sortFilter;
    float priceFilter;


    //For Coordinator Layout
    AppBarLayout appBarLayout;



    //OnBackPressed interface
    OnBackPressedListener onBackPressedListener;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Firebase.setAndroidContext(this);
        Log.d("RegisterReciever", String.valueOf(registerReceiver(new ConnectionService(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))));        //Enabling broadcast receiver
        registerReceiver(new ConnectionService(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        firebaseRef = FirebaseDatabase.getInstance().getReferenceFromUrl(FIREBASEREF);       //continue setting up firebase
        //System.out.println("Seconds is "+seconds+" Minutes is "+minutes+" Hours are "+hours+" Day is "+day+" Month is "+monthName+" year is "+year);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setSubtitleTextColor(0xFFFFFF);
        setSupportActionBar(toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        //Adding Navigation view
        navigationView =(NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerView=(View) LayoutInflater.from(this).inflate(R.layout.header_blank, null);
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth authData) {
                final FirebaseUser user = authData.getCurrentUser();
                if (user != null) {
                    //user logged in
                    navigationView.removeHeaderView(headerView);
                    headerView = (View) LayoutInflater.from(MainActivity.this).inflate(R.layout.header, null);
                    navigationView.addHeaderView(headerView);
                    header_name = (TextView) navigationView.getHeaderView(0).findViewById(R.id.header_name1);
                    header_email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.header_email1);
                    header_profile = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.profile_image);
                    header_email.setText(user.getEmail().toString());
                    String temp_mod = header_email.getText().toString();
                    final String temp_mod1 = temp_mod.replace('.', ',');
                    System.out.println(temp_mod1);

                    firebaseRef.child(temp_mod1).child("Personal_Info").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserReg userReg = dataSnapshot.getValue(UserReg.class);
                            //Setting up values for account page
                            header_name.setText(userReg.getName());
                            byte[] imageAsBytes = Base64.decode(userReg.getProfile_pic(), Base64.DEFAULT);
                            Bitmap bmp_pic = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                            header_profile.setImageBitmap(bmp_pic);
                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) {
                            Toast.makeText(MainActivity.this, "Seems to be some error! Please check your internet Connectivity", Toast.LENGTH_SHORT).show();
                        }
                    });
                    //header_name.setText((authData.getProviderData().get("email")).toString());
                    navigationView.getMenu().findItem(R.id.item_login).setVisible(false);
                    navigationView.getMenu().findItem(R.id.item_logout).setVisible(true);
                    navigationView.getMenu().findItem(R.id.item_favorites).setVisible(true);
                    navigationView.getMenu().findItem(R.id.item_myaccount).setVisible(true);
                    navigationView.getMenu().findItem(R.id.item_myads).setVisible(true);
                } else {
                    // user is not logged in
                    // No need now as it is handled in logout item clicked
                    navigationView.removeHeaderView(headerView);
                    headerView = (View) LayoutInflater.from(getApplicationContext()).inflate(R.layout.header_blank, null);
                    navigationView.addHeaderView(headerView);
                    navigationView.getMenu().findItem(R.id.item_login).setVisible(true);
                    navigationView.getMenu().findItem(R.id.item_logout).setVisible(false);
                    navigationView.getMenu().findItem(R.id.item_favorites).setVisible(false);
                    navigationView.getMenu().findItem(R.id.item_myaccount).setVisible(false);
                    navigationView.getMenu().findItem(R.id.item_myads).setVisible(false);
                }
            }
        });
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer);
        actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        //Initialize Filters to nothing
        mainCatFilter = "";
        subCatFilter = "";
        sortFilter = "";
        priceFilter = -1;

        if(savedInstanceState == null)
        {
            Fragment frag = HomeFragment.newInstance();
            //frag.setEnterTransition(new Slide(Gravity.RIGHT));
            mFragment = frag;
            //mFragment = getSupportFragmentManager().getFragment(savedInstanceState, "mFragment");
            getSupportFragmentManager().beginTransaction().replace(R.id.container, mFragment).commit();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent;
        int id=item.getItemId();

        switch (id){
            case R.id.item_home:
                if(!(mFragment instanceof HomeFragment))        //check if fragment is already there
                {
                    mFragment = HomeFragment.newInstance();
                    getSupportActionBar().setTitle("CuseXchange");
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, mFragment).addToBackStack(null).commit();
                }
                break;
            case R.id.item_submit:
                intent=new Intent(this,SubmitAd.class);
                startActivity(intent);
                break;
            /*case R.id.item_myads:
                if(!(mFragment instanceof MyAdsListFragment))        //check if fragment is already there
                {
                    mFragment = MyAdsListFragment.newInstance(0);
                    toolbar_title=getSupportActionBar().getTitle().toString();
                    getSupportActionBar().setTitle("My Ads");
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, mFragment).addToBackStack(null).commit();
                }
                break;*/
            case R.id.item_myaccount:
                intent=new Intent(this,MyAccount.class);
                startActivity(intent);
                break;
            /*case R.id.item_favorites:
                if(!(mFragment instanceof MyFavsListFragment))        //check if fragment is already there
                {
                    mFragment = MyFavsListFragment.newInstance(0);
                    toolbar_title=getSupportActionBar().getTitle().toString();
                    getSupportActionBar().setTitle("Favorites");
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, mFragment).addToBackStack(null).commit();
                }
                break;*/
            case R.id.item_login:
                intent = new Intent(MainActivity.this, LoginActivity.class);     //MainActivity.this vs MainActivity.class ??
                startActivity(intent);
                break;
            case R.id.item_logout:
                FirebaseAuth.getInstance().signOut();
                //navigationView.getHeaderView(View.GONE);
                navigationView.removeHeaderView(headerView);
                headerView=(View) LayoutInflater.from(this).inflate(R.layout.header_blank,null);
                navigationView.addHeaderView(headerView);
                navigationView.getMenu().findItem(R.id.item_login).setVisible(true);
                navigationView.getMenu().findItem(R.id.item_logout).setVisible(false);
                navigationView.getMenu().findItem(R.id.item_favorites).setVisible(false);
                navigationView.getMenu().findItem(R.id.item_myaccount).setVisible(false);
                navigationView.getMenu().findItem(R.id.item_myads).setVisible(false);
                if(!(mFragment instanceof HomeFragment))        //check if fragment is already there
                {
                    mFragment = HomeFragment.newInstance();
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, mFragment).addToBackStack(null).commit();
                }
                /*intent = new Intent(MainActivity.this, LoginActivity.class);     //MainActivity.this vs MainActivity.class ??
                startActivity(intent);
                *///logout logic
                break;
            case R.id.item_contactus:
                if(!(mFragment instanceof ContactUsFragment))        //check if fragment is already there
                {
                    mFragment = ContactUsFragment.newInstance(0);
                    toolbar_title=getSupportActionBar().getTitle().toString();
                    getSupportActionBar().setTitle("Contact Us");
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, mFragment).addToBackStack(null).commit();
                }
                break;
            case R.id.item_help:
                intent=new Intent(MainActivity.this,Help.class);
                startActivity(intent);
                break;
            default:
                //Toast.makeText(getApplicationContext(),"Somethings Wrong",Toast.LENGTH_SHORT).show();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    //Regular Fragment Swap without shared element transition
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void swapFragment(Fragment frag)
    {
        frag.setEnterTransition(new Slide(Gravity.LEFT));
        //frag.setExitTransition(new Slide(Gravity.RIGHT));
        mFragment = frag;          //for lists

        getSupportFragmentManager().beginTransaction().replace(R.id.container, mFragment).addToBackStack(null).commit();

        /*
        if(expandToolbar){
            appBarLayout.setExpanded(true,true);
        }else{
            appBarLayout.setExpanded(false,true);
        }
        */
    }

    //Fragment swap with shared element transition
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onItemSelected(UserAd userAd, View sharedImaged) {
        AdDetailFragment details = AdDetailFragment.newInstance(userAd);
        details.setSharedElementEnterTransition(new DetailsTransition());
        details.setEnterTransition(new Explode());
        //details.setEnterTransition(new Slide());
        details.setExitTransition(new Explode());
        details.setSharedElementReturnTransition(new DetailsTransition());

        ImageView iv =  (ImageView) sharedImaged.findViewById(R.id.item_iv);
        TextView tv1 = (TextView) sharedImaged.findViewById(R.id.item_tv_title);
        TextView tv2 = (TextView) sharedImaged.findViewById(R.id.item_tv_price);

        getSupportFragmentManager().beginTransaction()
                .addSharedElement(iv, iv.getTransitionName())
                .addSharedElement(tv1, tv1.getTransitionName())
                .addSharedElement(tv2, tv2.getTransitionName())
                .replace(R.id.container, details)
                .addToBackStack(null)
                .commit();
    }

    /*
    @Override
    public void onBackPressed()
    {
        if (onBackPressedListener != null)
            onBackPressedListener.onBackPressed();
        else
            super.onBackPressed();
    }
    */

/*    @Override
    public void onAttach(Activity activity) {
        mFragment.onAttach();

        if(behavior != null)
            return;

        FrameLayout layout =(FrameLayout) getActivity().findViewById(R.id.dashboard_content);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) layout.getLayoutParams();

        behavior = params.getBehavior();
        params.setBehavior(null);

    }

    @Override
    public void onDetach() {
        mFragment.onDetach();
        if(behavior == null)
            return;

        FrameLayout layout =(FrameLayout)findViewById(R.id.container);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) layout.getLayoutParams();

        params.setBehavior(behavior);

        layout.setLayoutParams(params);

        behavior = null;
    }*/
}
