package com.example.sunit_lp.ideaapp;

import android.view.View;

/**
 * Created by Sunit-LP on 2/14/2016.
 */
public interface OnItemClickListener{
    public void onItemClick(View view, int position);
    public void onItemLongClick(View view, int position);
    public void onOverflowMenuClick(View v, int position);
    public void onFavClick(View v,int position);
}