package com.example.sunit_lp.ideaapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPagerAdFragment extends Fragment
{
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_MOVIE_DATA = "hashmap_movie";
    public static final String ARG_MOVIE_INDEX = "index_movie";
    HashMap<String, ?> movie;


    public ViewPagerAdFragment()
    {
        // Required empty public constructor
    }

    //Returns a new instance of this fragment
    public static ViewPagerAdFragment newInstance(HashMap<String, ?> movie)
    {
        ViewPagerAdFragment fragment = new ViewPagerAdFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MOVIE_DATA, movie);
        fragment.setArguments(args);
        return fragment;
    }

    /* Mainly used to receive movied data from inside this fragment */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(getArguments() != null)
        {
            movie = (HashMap<String, ?>) getArguments().getSerializable(ARG_MOVIE_DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);

        TextView tv_title = (TextView) rootView.findViewById(R.id.tv_title);
        TextView tv_desc = (TextView) rootView.findViewById(R.id.tv_desc);
        ImageView iv_image = (ImageView) rootView.findViewById(R.id.iv_image);
        TextView tv_year = (TextView) rootView.findViewById(R.id.tv_year);
        TextView tv_stars = (TextView) rootView.findViewById(R.id.tv_stars);

        //TextView stuff
        tv_title.setText((String) movie.get("name"));
        tv_desc.setText((String) movie.get("description"));
        iv_image.setImageResource((Integer) (movie.get("image")));
        tv_year.setText((String) movie.get("year"));
        tv_stars.setText((String) movie.get("stars"));

        return rootView;
    }

}
