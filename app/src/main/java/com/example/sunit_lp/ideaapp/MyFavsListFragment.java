package com.example.sunit_lp.ideaapp;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyFavsListFragment extends Fragment
{
    private static final String ARG_SECTION_NUMBER="section_number";
    MyFavs_Adapter myFavs_adapter;
    RecyclerView mRecycleView;
    LinearLayoutManager mLayoutManager;
    DatabaseReference ref = null;
    DatabaseReference ref1 = null;
    DatabaseReference ref2=null;
    String toolbar_value;
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        //outState.putSerializable("section_number", (Serializable) movieList);

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.myads_fragment,container,false);

        //movieData=new MovieData();
        mRecycleView=(RecyclerView)rootView.findViewById(R.id.Ad_recycleview);
        mRecycleView.setHasFixedSize(true);
        mRecycleView.setLayoutManager(new GridLayoutManager(getActivity(),2));
        //myAds_adapter=new MyAds_Adapter(getActivity(),movieData.getMoviesList());
        //ref1 = new Firebase ("https://cusexchange.firebaseio.com/svij@syr,edu/IndividualAds");
        ref= FirebaseDatabase.getInstance().getReferenceFromUrl("https://cusexchange.firebaseio.com/");
        final FirebaseAuth authData = FirebaseAuth.getInstance();//.getAuth();
        String temp_mod=(authData.getCurrentUser().getEmail()).toString();
        final String temp_mod1=temp_mod.replace('.', ',');
        ref1=FirebaseDatabase.getInstance().getReferenceFromUrl("https://cusexchange.firebaseio.com/" + temp_mod1 + "/Favorites");
        System.out.println(ref1.toString());
        myFavs_adapter =new MyFavs_Adapter(UserAd.class,R.layout.myfavs_content_layout,MyFavs_Adapter.ViewHolder.class,ref1,getActivity());
        mRecycleView.setAdapter(myFavs_adapter);
        myFavs_adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }

            @Override
            public void onOverflowMenuClick(View v, final int position) {
            }

            @Override
            public void onFavClick(View v, int position) {
                UserAd userAd=myFavs_adapter.getItem(position);
                /*String getKey=userAd.getKey().toString();
                System.out.println(ref1.child(userAd.getKey()));
                System.out.println(getKey);
                */ref1.child(userAd.getKey()).removeValue();

            }
        });
        return rootView;
    }

    public static MyFavsListFragment newInstance(int pos)
    {
        MyFavsListFragment fragment=new MyFavsListFragment();
        /*Bundle args=new Bundle();
        args.putInt(ARG_SECTION_NUMBER,pos);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        //Log.d("onDetach()", "onDetach() called");
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(((MainActivity)getActivity()).toolbar_title);
    }

}
