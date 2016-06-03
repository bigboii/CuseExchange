package com.example.sunit_lp.ideaapp;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */

//http://stackoverflow.com/questions/28386397/shared-element-transitions-between-views-not-activities-or-fragments

public class HomeFragment extends Fragment
{
    //VARIABLES
    private GridView gridView;                                             // 2 x 2 gridview in home fragment
    private GridAdapter gridAdapter;                                       // adapter for gridview
    private ArrayList<Integer> imageList = new ArrayList<Integer>();     // Arraylist of images of gridview
    private ArrayList<String> colorList = new ArrayList<String>();              // Arraylist of hex colors for gridview
    private ArrayList<String> categoryList = new ArrayList<String>();      // Arraylist of categories as string
   // private ArrayList<String> drawable_back=new ArrayList<>();      // Arraylist of drawables as string
    ImageView fab_submitad;

    //Screen Calculation Variables
    int screenWidthPx;
    int screenHeightPx;
    int columnWidth;

    //ViewPager stuff
    DatabaseReference firebasePagerRef;
    ViewPager viewPager;
    ViewPagerAdapter pagerAdapter;
    ValueEventListener fireListener;
    List<UserAd> useradList;

    //CONSTRUCTOR
    public HomeFragment()
    {
        // Required empty public constructor
    }

    //Returns a new instance of this fragment
    public static HomeFragment newInstance()
    {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Firebase.setAndroidContext(getActivity());
        //Setup Firebase Listener
        useradList = new ArrayList<UserAd>();
        fireListener = new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                useradList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    UserAd userAd = postSnapshot.getValue(UserAd.class);
                    useradList.add(userAd);
                    //Log.d("HomeFragment", postSnapshot.toString());
                }

                //if(pagerAdapter != null)                 //needed for orientation change
                pagerAdapter.notifyDataSetChanged();
                pagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), useradList.size());      //***NOTE: use getChildFragmentManager() instead of supportFragmentManager() for nested fragments
                viewPager.setAdapter(pagerAdapter);
                //Log.d("HomeFragment", useradList.size() + "");
            }

            @Override
            public void onCancelled(DatabaseError firebaseError)
            {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        };

        loadPagerData();
    }

    //Get all Ads from firebase
    public void loadPagerData()
    {
        useradList.clear();
        firebasePagerRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://cusexchange.firebaseio.com/sunitvijay88@gmail,com/IndividualAds");
        if(useradList.size() == 0)
        {
            firebasePagerRef.addValueEventListener(fireListener);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        //Disable coordinator layout
        ((MainActivity)getActivity()).appBarLayout.setExpanded(true,true);

        //Reset Filters to nothing
        ((MainActivity) getActivity()).mainCatFilter = "";
        ((MainActivity) getActivity()).subCatFilter = "";
        ((MainActivity) getActivity()).sortFilter = "";
        ((MainActivity) getActivity()).priceFilter = -1;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //getActivity().setContentView(R.layout.activity_main);

        //Recalculate screen layout on screen orientation change
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidthPx = size.x;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            screenHeightPx = size.y;
            columnWidth = (screenWidthPx / 4) - (int) calculateDpToPixel(5.0f, getContext()) / 2 - (int) calculateDpToPixel(5.0f, getContext());               //(item width) - (margin/2) - (horizontal spacing/2)
            gridView.setNumColumns(4);
        }
        else
        {
            screenHeightPx = size.y;
            gridView.setNumColumns(2);
            columnWidth = (screenWidthPx / 2) - (int) calculateDpToPixel(5.0f, getContext()) / 2 - (int) calculateDpToPixel(5.0f, getContext());               //(item width) - (margin/2) - (horizontal spacing/2)
        }

        gridAdapter.setHeight(columnWidth);
        gridAdapter.notifyDataSetChanged();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);                              //In Fragments, you have to inflate them

        //Notify Toolbar of menu
        //setHasOptionsMenu(true); // tell host activity that this fragment has menu options that it wants to add
        fab_submitad=(ImageView)rootView.findViewById(R.id.fab_submit);
        fab_submitad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),SubmitAd.class);
                startActivity(intent);
            }
        });
        //Rename Toolbar title
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("CuseExchange");

        //Initialize lists for adapters
        if(categoryList.size() == 0)
        {
            imageList.add(R.mipmap.ic_book_white_48dp);
            imageList.add(R.mipmap.ic_location_city_white_48dp);
            imageList.add(R.mipmap.ic_weekend_white_48dp);
            imageList.add(R.mipmap.ic_local_atm_white_48dp);
//            colorList.add("#FFD180");   //Initialize Colors
//            colorList.add("#FFAB40");
//            colorList.add("#FF9100");
//            colorList.add("#FF6D00");
            colorList.add("#3E3D3C");
            colorList.add("#3E3D3C");
            colorList.add("#3E3D3C");
            colorList.add("#3E3D3C");
            categoryList.add("Books");
            categoryList.add("Housing");
            categoryList.add("Home Accessories");
            categoryList.add("Valuables");
        }

        //Log.d("HomeFragment", "list size " + categoryList.size() + "       " + colorList.size());

        //Grid Layout Stuff
        gridView = (GridView) rootView.findViewById(R.id.home_gridview);

        //Populate GridView
        if(gridAdapter == null)
            gridAdapter = new GridAdapter(getActivity(), imageList, colorList, categoryList);

        /*
        //Calculate grid item width and height (dp)
        Configuration configuration = getActivity().getResources().getConfiguration();
        int screenWidthDp = configuration.screenWidthDp;     //width of screen in dp
        int screenHeightDp = configuration.screenHeightDp;   //height of screen in dp
        */

        //Calculate grid item width and height (pixels)
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidthPx = size.x;
        //screenHeightPx = size.y;

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            screenHeightPx = size.y;
            columnWidth = (screenWidthPx / 4) - (int) calculateDpToPixel(5.0f, getContext()) / 2 - (int) calculateDpToPixel(5.0f, getContext());               //(item width) - (margin/2) - (horizontal spacing/2)
            gridView.setNumColumns(4);
        }
        else
        {
            screenHeightPx = size.y;
            gridView.setNumColumns(2);
            columnWidth = (screenWidthPx / 2) - (int) calculateDpToPixel(5.0f, getContext()) / 2 - (int) calculateDpToPixel(5.0f, getContext());               //(item width) - (margin/2) - (horizontal spacing/2)
        }

        gridAdapter.setHeight(columnWidth);
        gridAdapter.notifyDataSetChanged();

        //if(gridView.getAdapter() == null)
            gridView.setAdapter(gridAdapter);

        //Setup ViewPager
        //movieData = new MovieData();                     //Initialize Dummy Movie Data

        viewPager = (ViewPager) rootView.findViewById(R.id.home_viewpager);
        pagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), useradList.size());      //***NOTE: use getChildFragmentManager() instead of supportFragmentManager() for nested fragments
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(1);
        viewPager.setPageTransformer(false, new ViewPager.PageTransformer()
        {
            @Override
            public void transformPage(View page, float position)
            {
                final float normalized_position = Math.abs(Math.abs(position) - 1);

                //Fading Out
                page.setAlpha(normalized_position);

                //Scaling Effect
                page.setScaleX(normalized_position / 2 + 0.5f);
                page.setScaleY(normalized_position / 2 + 0.5f);

                //Rotation Effect
                page.setRotationY(position * -30);
            }
        });


        return rootView;
    }

    //Convert DP to PX
    public static float calculateDpToPixel(float dp, Context context)
    {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    //Convert PX to DP

    //Setup Toolbar's search functionalities
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        /*if(menu.findItem(R.id.action_search) == null)
            inflater.inflate(R.menu.toolbar_action, menu); // Commented just for demo purpose
*/
        //Setup Searchbar
        SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if(search != null)
        {
            search.setOnQueryTextListener(new SearchView.OnQueryTextListener()
            {
                @Override
                public boolean onQueryTextSubmit(String query)
                {
                    Toast.makeText(getActivity(), "You can search for items here", Toast.LENGTH_SHORT).show();
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


    /**
        Grid Image Adapter
     */
    public class GridAdapter extends BaseAdapter
    {
        final private static int MAX_GRIDITEM_NUM = 4;                 //# of grid items in grid view
        private LayoutInflater inflater;

        //Variables
        private ArrayList<Integer> imageItems;
        private ArrayList<String> colorItems;
        private ArrayList<String> catItems;
        private Context context;

        private int mItemHeight = 0;
        private int mNumColumns = 0;
        private GridView.LayoutParams cardLayoutParams;

        private View gridItemView;


        //CONSTRUCTOR
        public GridAdapter(Context context, ArrayList<Integer> imageList, ArrayList<String> colorList, ArrayList<String> catList)
        {
            this.context = context;
            imageItems = imageList;
            colorItems = colorList;
            catItems = catList;

            inflater = ( LayoutInflater )context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        //Notify that we have 4 items in gridview
        @Override
        public int getCount()
        {
            return colorItems.size();
        }

        @Override
        public Object getItem(int position)
        {
            return position;
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        public void setHeight(int height)
        {
            mItemHeight = height;
        }

        //Initialize individual grid item views here
        @Override
        public View getView(final int position, View view, ViewGroup parent)
        {
            //LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridItemView = inflater.inflate(R.layout.item_grid_home, null);

            // 1. Set Image
            ImageView iv = (ImageView) gridItemView.findViewById(R.id.item_grid_home_iv);
            iv.setImageResource(imageItems.get(position));

            // 2. Set Color
            LinearLayout layout = (LinearLayout) gridItemView.findViewById(R.id.item_grid_home_layout);
            layout.setBackgroundColor(Color.parseColor(colorItems.get(position)));

            // 3. Set Category Text
            TextView tv = (TextView) gridItemView.findViewById(R.id.item_grid_home_tv);
            tv.setText(catItems.get(position));
            //Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/franklin-gothic-medium.ttf");                //Initialize custom font
            //Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/MaterialIcons-Regular.ttf");                //Initialize custom font
            //tv.setTypeface(typeface);

            // 4. Set Dimentions
            cardLayoutParams = new GridView.LayoutParams(mItemHeight, mItemHeight);
            gridItemView.setLayoutParams(cardLayoutParams);

            // 5. Set onClickListener()
            gridItemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    //v.setTransitionName("testAnimation");          //for shared element transition, WORKS?

                    switch (position)
                    {
                        case 0:                                               //1st griditem of 1st row
                            ((MainActivity)getActivity()).getSupportActionBar().setTitle("Books");
                            ((MainActivity) getActivity()).mainCatFilter = "Books";
                            ((MainActivity)getActivity()).swapFragment(AdListFragment.newInstance("Books"));
                            break;

                        case 1:                                               //2nd griditem of 1st row

                            ((MainActivity)getActivity()).getSupportActionBar().setTitle("Housing");
                            ((MainActivity) getActivity()).mainCatFilter = "Housing";
                            //((MainActivity)getActivity()).swapFragment(MapFragment.newInstance());
                            ((MainActivity)getActivity()).swapFragment(AdListFragment.newInstance("Housing"));

                            break;

                        case 2:                                               //3rd row, 1st griditem
                            ((MainActivity)getActivity()).getSupportActionBar().setTitle("Home Accessories");
                            ((MainActivity) getActivity()).mainCatFilter = "Home Accessories";
                            ((MainActivity)getActivity()).swapFragment(AdListFragment.newInstance("Home Accessories"));
                            break;

                        case 3:                                               //2nd griditem of 2nd row
                            ((MainActivity)getActivity()).getSupportActionBar().setTitle("Valuables");
                            ((MainActivity) getActivity()).mainCatFilter = "Valuables";
                            ((MainActivity)getActivity()).swapFragment(AdListFragment.newInstance("Valuables"));
                            break;
                    }
                }
            });

            //6. Set shared element tranisition from HomeFragment to AdListFragment
            //gridItemView.setTransitionName("PASS1");    //Doesn't Work


            return gridItemView;
        }
    }


    /**
     *  Custom adapter for UserReg Fragment's ViewPager
     * */
    public class ViewPagerAdapter extends FragmentPagerAdapter
    {
        int count;

        //Constructor
        public ViewPagerAdapter(FragmentManager fm, int size)
        {
            super(fm);
            count = size;
        }

        //Return a specific movie item
        @Override
        public Fragment getItem(int position)
        {
            return AdPagerFragment.newInstance(useradList.get(position));
        }


        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }


        @Override
        public int getCount()
        {
            return count;
        }


        @Override
        public void destroyItem(ViewGroup viewPager, int position, Object object) {      //issue Q
          //  viewPager.removeView((View) object);
        }


        @Override
        public CharSequence getPageTitle(int position)
        {
            Locale l = Locale.getDefault();
            String name = useradList.get(position).getTitle();
            return name.toUpperCase(l);
        }

    }

}
