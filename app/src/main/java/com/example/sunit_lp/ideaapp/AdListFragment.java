package com.example.sunit_lp.ideaapp;


import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LruCache;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInAnimator;

/**
 http://blog.sqisland.com/2014/12/recyclerview-grid-with-header.html
 */
public class AdListFragment extends Fragment
{
    final static String ARG_CATEGORY = "CATEGORY";
    //public String categoryFilter;
    List<Map<String,?>> adList;
    List<UserAd> useradList;

    RecyclerView recyclerView;
    GridRecyclerAdapter gridRecyclerAdapter;
    View rootView;
    ImageView fab_submitad;                          //Floating Image Filter Button
    DatabaseReference refBase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://cusexchange.firebaseio.com/");
    DatabaseReference refFav = null;

    ValueEventListener firebaseListener;
    ProgressBar progressBar;                            //loading indicator

    //public OnItemSelectedListener mListener;          //For shared fragment transition
    //public static LruCache<String, Bitmap> mImgMemoryCache;         //Q Image Caching

    public static AdListFragment newInstance(String category)
    {
        AdListFragment fragment = new AdListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Firebase.setAndroidContext(getActivity());
        //movieData = new MovieData();                     //Initialize Dummy Data
        adList = new ArrayList<Map<String,?>> ();         //initialize list
        useradList = new ArrayList<UserAd>();

        //allows MovieFragment to load MovieData when being swapped by MovieListFragment
        if(getArguments() != null)
        {
            //categoryFilter = getArguments().getString(ARG_CATEGORY);
            //Log.d("AdListFragment", "Category Filter: "+ categoryFilter);
        }

        //Create the LruCache (Least Recently used)
        /*
        if(mImgMemoryCache == null)                                   //Q image caching
        {
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            final int cacheSize = maxMemory / 8;

            mImgMemoryCache = new LruCache<String, Bitmap>(cacheSize)
            {
                @Override
                protected int sizeOf(String key, Bitmap bitmap)
                {
                    // The cache size will be measured in kilobytes rather than number of items
                    return bitmap.getByteCount() / 1024;
                }
            };
        }
        */

        //Setup Firebase Listener
        firebaseListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                //progressBar.setVisibility(View.VISIBLE);
                adList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())       //Gets emails
                {
                    for (DataSnapshot ad : postSnapshot.child("IndividualAds").getChildren())
                    {
                        //Log.d("onDataChange()", postSnapshot.getKey());
                        UserAd userAd = ad.getValue(UserAd.class);           //
                        if (checkFilters(userAd))
                        {
                            adList.add(userAd.createAd(userAd.getAddress(), userAd.getCategory(), userAd.getDescription(), userAd.getPrice(), userAd.getSubCategory(), userAd.getTitle(), userAd.getUrl_img(),userAd.getDate()));
                            useradList.add(userAd);
                        }
                    }
                }

                if(gridRecyclerAdapter != null)                 //needed for orientation change
                    gridRecyclerAdapter.notifyDataSetChanged();

                //progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError firebaseError)
            {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        };

        loadAllData();
    }


    @Override
    public void onResume()
    {
        super.onResume();
        if(((MainActivity) getActivity()).isFilterApplied == true)
        {
            ((MainActivity) getActivity()).isFilterApplied = false;
            loadAllData();                                            //get ads from firebase, also acts as a refresh when returning from filter activity
        }
        ((MainActivity) getActivity()).isFilterApplied = false;   //reset

        ((MainActivity)getActivity()).getSupportActionBar().show();

    }

    //Get all Ads from firebase
    public void loadAllData()
    {
        adList.clear();

        if(adList.size() == 0)
        {
            ((MainActivity)getActivity()).firebaseRef.addValueEventListener(firebaseListener);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.fragment_adlist, container, false);                              //In Fragments, you have to inflate them
        ((MainActivity)getActivity()).appBarLayout.setExpanded(true,true);

        //listener for onBackPressed
        rootView.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_BACK)
                {
                    Log.d("OnKeyListener", "back pressed");
                    ((MainActivity) getActivity()).firebaseRef.removeEventListener(firebaseListener);
                    return true;
                }
                return false;
            }
        });

        //Notify Toolbar of menu
        //setHasOptionsMenu(true); // tell host activity that this fragment has menu options that it wants to add

        //Setup Floating Filter Button
        fab_submitad = (ImageView) rootView.findViewById(R.id.fab_submit);
        fab_submitad.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((MainActivity)getActivity()).swapFragment(FilterFragment.newInstance());
            }
        });

        //Setup Grid Recycler Adapter
        gridRecyclerAdapter = new GridRecyclerAdapter(getActivity(), adList, ((MainActivity) getActivity()).mainCatFilter);
        gridRecyclerAdapter.mListener = (OnItemSelectedListener) getActivity();//lab10
        gridRecyclerAdapter.setOnItemClickListener(new GridRecyclerAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(View v, int position)
            {
                //((MainActivity) getActivity()).swapFragment(AdDetailFragment.newInstance((HashMap) adList.get(position)));     //normal swap fragment
                //((MainActivity) getActivity()).swapFragment(AdDetailFragment.newInstance(useradList.get(position)));
                gridRecyclerAdapter.mListener.onItemSelected(useradList.get(position), v);          //Allows shared element transition
            }

            @Override
            public void onItemMapClick(View v, final int position)
            {
                //((MainActivity) getActivity()).swapFragment(MapFragment.newInstance(useradList.get(position)));

                DialogMapFragment dialogMap = new DialogMapFragment(useradList.get(position));
                dialogMap.show(((MainActivity) getActivity()).getSupportFragmentManager(), "fragment_dialog_map");


            }

            @Override
            public void onItemFavClick(View v, int position)
            {
                //UserAd userAd1;
                //userAd1.createAd();
                UserAd temp = useradList.get(position);
                UserAd uad = new UserAd();

                //creating userad, uad, with no datasnapshot for fav
                uad.setAddress(temp.getAddress());
                uad.setCategory(temp.getCategory());
                uad.setDescription(temp.getDescription());
                uad.setPrice(temp.getPrice());
                uad.setSubCategory(temp.getSubCategory());
                uad.setTitle(temp.getTitle());
                uad.setUrl_img(temp.getUrl_img());
                uad.setKey(temp.getKey());
                uad.setEmail(temp.getEmail());
                //Set Favorite
                String temp_mod= temp.getEmail();                   //get seller's email
                System.out.println(temp_mod);
                final FirebaseAuth authData=FirebaseAuth.getInstance();
                String loginEmail=authData.getCurrentUser().getEmail().toString();
                final String loginEmail1=loginEmail.replace('.', ',');
                System.out.println(loginEmail1);
                refFav = FirebaseDatabase.getInstance().getReferenceFromUrl("https://cusexchange.firebaseio.com/"+loginEmail1+"/Favorites");
                System.out.println(refFav.child(uad.getKey()));
                refFav.child(uad.getKey()).setValue(uad);
                Toast.makeText(getActivity(),"Added to Favorites",Toast.LENGTH_SHORT).show();
             }
        });

        //Setup RecyclerView
        recyclerView = (RecyclerView) rootView.findViewById(R.id.adlist_recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        //Setup Animation
        setupRecyclerItemAnimation();                                                //animation for each item
        recyclerView.setAdapter(new AlphaInAnimationAdapter(gridRecyclerAdapter));

        //Setup ProgressBar
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        return rootView;
    }

    //Determine if userad should be displayed on list based on filters
    private boolean checkFilters(UserAd ad)
    {
        if(((MainActivity) getActivity()) == null)
        {
            return false;
        }

        boolean cat = ad.getCategory().equals(((MainActivity) getActivity()).mainCatFilter);
        boolean subcat = ad.getSubCategory().equals(((MainActivity) getActivity()).subCatFilter);
        boolean isSubCatEmpty = ((MainActivity) getActivity()).subCatFilter.equals("");

        if(((MainActivity) getActivity()).mainCatFilter.equals(""))
        {
            return false;
        }
        if(!cat)
        {
            return false;
        }
        if (!isSubCatEmpty)           //subCatFilter empty?
        {
            if(!subcat)
            {
                return false;
            }
        }
        if((((MainActivity) getActivity()).priceFilter != -1))
        {
            Log.d("AdListFragment", "ad.getPrice(): " + Float.parseFloat(ad.getPrice()) + "    priceFilter: " + ((MainActivity) getActivity()).priceFilter);
            if((Float.parseFloat(ad.getPrice()) > ((MainActivity) getActivity()).priceFilter))
            {
                return false;
            }
        }
        return true;
    }

    //Item Animation for each recycler item
    private void setupRecyclerItemAnimation()
    {
        FadeInAnimator animator = new FadeInAnimator();
        animator.setInterpolator(new OvershootInterpolator());
        animator.setAddDuration(120);
        animator.setRemoveDuration(120);

        recyclerView.setItemAnimator(animator);
    }

    //Setup Toolbar's search functionalities
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.toolbar_action, menu);

        //Setup Searchbar
        SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if(search != null)
        {
            search.setOnQueryTextListener(new SearchView.OnQueryTextListener()
            {
                @Override
                public boolean onQueryTextSubmit(String query)
                {
                    Toast.makeText(getActivity(), "\"" + query + "\" not found", Toast.LENGTH_SHORT).show();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String query)
                {
                    return true;
                }
            });
        }

        super.onCreateOptionsMenu(menu, inflater);
    }


    //For filtering in toolbar
    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch(id)
        {
            case R.id.action_filter:
                ((MainActivity) getActivity()).swapFragment(FilterFragment.newInstance());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */



    /**
     * At fragment detach
    */
    @Override
    public void onDetach()
    {
        super.onDetach();
        Log.d("onDetach()", "onDetach() called");
        ((MainActivity) getActivity()).firebaseRef.removeEventListener(firebaseListener);
    }

}

