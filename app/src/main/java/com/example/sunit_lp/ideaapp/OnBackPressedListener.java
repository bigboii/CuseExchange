package com.example.sunit_lp.ideaapp;

/**
 * Created by YQ on 4/26/2016.
 */
public interface OnBackPressedListener
{
    /**
     * Callback, which is called if the Back Button is pressed.
     * Fragments that extend MainFragment can/should override this Method.
     *
     * @return true if the App can be closed, false otherwise
     */
    void onBackPressed();
}
