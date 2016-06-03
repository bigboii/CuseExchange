package com.example.sunit_lp.ideaapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by YQ on 3/30/2016.
 */
public class FilterActivity extends AppCompatActivity
{
    Spinner spinnerCategory;
    Spinner spinnerSort;
    Spinner spinnerSort2;
    SeekBar seekbar;
    TextView tvPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        spinnerCategory = (Spinner) findViewById(R.id.spinner_cat);
        spinnerSort = (Spinner) findViewById(R.id.spinner_sort);
        //spinnerSort2 = (Spinner) findViewById(R.id.spinner_sort2);
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        tvPrice = (TextView) findViewById(R.id.tv_price);

        //Set Category Spinner
        //http://www.mkyong.com/android/android-spinner-drop-down-list-example/
        ArrayList<String> categoryList = new ArrayList<String>();
        categoryList.add("Books");
        categoryList.add("Household");
        categoryList.add("Housing");
        categoryList.add("Events");
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categoryList);
        spinnerCategory.setAdapter(categoryAdapter);

        //Set Sort Spinner
        ArrayList<String> sortList = new ArrayList<String>();
        sortList.add("Price (Ascending)");
        sortList.add("Price (Descending)");
        sortList.add("Date (Recent)");
        sortList.add("Date (Oldest)");
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, sortList);
        spinnerSort.setAdapter(sortAdapter);

        //Seekbar
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                progressChanged = progress;
                if(progress != 100)
                {
                    tvPrice.setText("$" + progress);
                }
                else
                {
                    tvPrice.setText("$" + progress + "+");
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar)
            {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

    }
}
