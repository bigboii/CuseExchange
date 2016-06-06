package com.example.sunit_lp.ideaapp;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Sunit-LP on 6/6/2016.
 */
public class MyNotificationsListFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
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
    public static MyNotificationsListFragment newInstance(int pos)
    {
        MyNotificationsListFragment fragment=new MyNotificationsListFragment();
        /*Bundle args=new Bundle();
        args.putInt(ARG_SECTION_NUMBER,pos);
        fragment.setArguments(args);*/
        return fragment;
    }
}
