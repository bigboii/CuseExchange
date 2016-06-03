package com.example.sunit_lp.ideaapp;


import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdPagerFragment extends Fragment
{
    private static final String ARG_USERAD_DATA = "USERAD";

    ImageView ivDetail;
    ImageView ivProfileImage;
    TextView tvTitle;
    TextView tvPrice;
    TextView tvSeller;
    TextView tvDescription;
    TextView tvDate;                //Missing in firebase
    UserAd userAd;

    DatabaseReference firebaseSellerRef;
    UserReg userReg;

    public AdPagerFragment()
    {
        // Required empty public constructor
    }

    public static AdPagerFragment newInstance(UserAd ad)
    {
        AdPagerFragment fragment = new AdPagerFragment();
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

        if (getArguments() != null) {
            //adInfo = (HashMap<String, ?>) getArguments().getSerializable(ARG_MOVIE_DATA);
            userAd = (UserAd) getArguments().getSerializable(ARG_USERAD_DATA);
        }

        getSellerInfo();
    }

    //Get all Ads from firebase
    public void getSellerInfo()
    {
        String userPath="https://cusexchange.firebaseio.com/"+userAd.getEmail()+"/Personal_Info";
        firebaseSellerRef = FirebaseDatabase.getInstance().getReferenceFromUrl(userPath);

        firebaseSellerRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                userReg = dataSnapshot.getValue(UserReg.class);
                tvSeller.setText((String) userReg.getName());

                //Setting image
                if (!(userReg.getProfile_pic().isEmpty()))
                {
                    byte[] imageAsBytes = Base64.decode(userReg.getProfile_pic(), 0);
                    Bitmap bmp_pic = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);

                    //byte[] imageAsBytes = Base64.decode(userReg.getProfile_pic(), 0);
                    //Bitmap bmp_pic = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                    Bitmap bt = Bitmap.createScaledBitmap(bmp_pic, bmp_pic.getWidth(), bmp_pic.getHeight(), false);
                    ivProfileImage.setImageBitmap(bt);
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError)
            {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_ad_pager, container, false);                              //In Fragments, you have to inflate them

        ivDetail = (ImageView) rootView.findViewById(R.id.iv_detail);
        ivProfileImage = (ImageView) rootView.findViewById(R.id.profile_image);
        tvTitle = (TextView) rootView.findViewById(R.id.tv_title);
        tvSeller = (TextView) rootView.findViewById(R.id.tv_seller);
        tvPrice = (TextView) rootView.findViewById(R.id.tv_price);
        tvDescription = (TextView) rootView.findViewById(R.id.tv_description);
        tvDate = (TextView) rootView.findViewById(R.id.tv_date);

        //Setting Texts
        ivDetail.setTransitionName((String) userAd.getTitle());                   //for shared element transition
        tvTitle.setText((String) userAd.getTitle());
        //tvSeller.setText((String) userAd.getName());
        tvDescription.setText((String) userAd.getDescription());
        tvPrice.setText( "$" + ((String) userAd.getPrice()));
        tvDate.setText("Posted on "+(String)userAd.getDate());
        //tvDate.setText(String) adInfo.get("date"));         //date feature missing
        //Setting image
        if (!(userAd.getUrl_img().isEmpty()))
        {
            byte[] imageAsBytes = Base64.decode(userAd.getUrl_img(), 0);
            Bitmap bmp_pic = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);

            //byte[] imageAsBytes = Base64.decode(userReg.getProfile_pic(), 0);
            //Bitmap bmp_pic = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
            Bitmap bt=Bitmap.createScaledBitmap(bmp_pic, bmp_pic.getWidth(), bmp_pic.getHeight(), false);
            ivDetail.setImageBitmap(bt);
        }

        tvPrice.setTransitionName((String) userAd.getPrice() + "_tv2");           //for shared element transition
        tvTitle.setTransitionName((String) userAd.getTitle() + "_tv");            //for shared element transition

        return rootView;
    }

}
