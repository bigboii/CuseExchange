package com.example.sunit_lp.ideaapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FilterFragment extends Fragment
{
    Spinner spinnerCategory;
    Spinner spinnerSubCategory;
    Spinner spinnerSort;
    Spinner spinnerSort2;
    SeekBar seekbar;
    TextView tvPrice;
    TextView tvApplyFilter;

    //String mainCatFilter;
    //String subCatFilter;
    //String sortFilter;
    ArrayAdapter<String> subCatAdapter;

    public static FilterFragment newInstance()
    {
        FilterFragment fragment = new FilterFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_filter, container, false);

        //Disabled Coordinator Layout
        ((MainActivity)getActivity()).appBarLayout.setExpanded(false,true);

        spinnerCategory = (Spinner) rootView.findViewById(R.id.spinner_cat);
        spinnerSubCategory = (Spinner) rootView.findViewById(R.id.spinner_subcat);
        spinnerSort = (Spinner) rootView.findViewById(R.id.spinner_sort);
        //spinnerSort2 = (Spinner) findViewById(R.id.spinner_sort2);
        seekbar = (SeekBar) rootView.findViewById(R.id.seekbar);
        tvPrice = (TextView) rootView.findViewById(R.id.tv_price);
        tvApplyFilter = (TextView) rootView.findViewById(R.id.tv_applyfilter);

        //Set Category Spinner
        //http://www.mkyong.com/android/android-spinner-drop-down-list-example/
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.categories_array));
        spinnerCategory.setEnabled(false);                 //disable spinner
        spinnerCategory.setClickable(false);
        spinnerCategory.setAdapter(categoryAdapter);

        int i = 0;
        for(String cat : getResources().getStringArray(R.array.categories_array))         //set selected item based on current filter
        {
            if(cat.equals(((MainActivity) getActivity()).mainCatFilter))
                spinnerCategory.setSelection(i);
            i++;
        }

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                int subcat = 0;
                ((MainActivity) getActivity()).mainCatFilter = spinnerCategory.getSelectedItem().toString();

                switch (position)
                {
                    case 0:
                        ((MainActivity) getActivity()).mainCatFilter = "";
                        subcat = R.string.subcat_default;
                        break;
                    case 1:
                        ((MainActivity) getActivity()).mainCatFilter = "Books";
                        subcat = R.array.subcategories_Books;
                        break;
                    case 2:
                        ((MainActivity) getActivity()).mainCatFilter = "Housing";
                        subcat = R.array.subcategories_Housing;
                        break;
                    case 3:
                        ((MainActivity) getActivity()).mainCatFilter = "Home Accessories";
                        subcat = R.array.subcategories_HomeAccessories;
                        break;
                    case 4:
                        ((MainActivity) getActivity()).mainCatFilter = "Valuables";
                        subcat = R.array.subcategories_Valuables;
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
                ((MainActivity) getActivity()).mainCatFilter = spinnerCategory.getSelectedItem().toString();
            }
        });

        //Set Sub Category Spinner
        i = 0;
        int subcatId = -1;
        if(((MainActivity)getActivity()).mainCatFilter.equals("Books"))
            subcatId = R.array.subcategories_Books;
        if(((MainActivity)getActivity()).mainCatFilter.equals("Housing"))
            subcatId = R.array.subcategories_Housing;
        if(((MainActivity)getActivity()).mainCatFilter.equals("Home Accessories"))
            subcatId = R.array.subcategories_HomeAccessories;
        if(((MainActivity)getActivity()).mainCatFilter.equals("Valuables"))
            subcatId = R.array.subcategories_Valuables;

        subCatAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(subcatId));
        spinnerSubCategory.setAdapter(subCatAdapter);

        if(subcatId != -1 && !((MainActivity)getActivity()).subCatFilter.equals(""))
        {
            for(String subcat : getResources().getStringArray(subcatId))
            {
                if(subcat.equals(((MainActivity)getActivity()).subCatFilter))
                    spinnerSubCategory.setSelection(i);                                                   //set selected item based on current filter
                i++;
            }
        }

        spinnerSubCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                int subcat = 0;
                ((MainActivity)getActivity()).subCatFilter = spinnerSubCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
                ((MainActivity)getActivity()).subCatFilter = spinnerSubCategory.getSelectedItem().toString();
            }
        });

        //Set Sort Spinner
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.category_sortby));
        spinnerSort.setAdapter(sortAdapter);
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                ((MainActivity) getActivity()).sortFilter = spinnerSort.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
                ((MainActivity) getActivity()).sortFilter = spinnerSort.getSelectedItem().toString();
            }

        });

        //Seekbar
        seekbar.setProgress((int) ((MainActivity) getActivity()).priceFilter);
        if(((MainActivity) getActivity()).priceFilter == -1)
            tvPrice.setText("$" + 0);
        else
            tvPrice.setText("$" + ((MainActivity) getActivity()).priceFilter);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                progressChanged = progress;
                ((MainActivity) getActivity()).priceFilter = (float) progress;
                if (progress != 100)
                {
                    tvPrice.setText("$" + progress);
                } else
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

        tvApplyFilter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((MainActivity) getActivity()).isFilterApplied = true;
                ((MainActivity) getActivity()).getSupportFragmentManager().beginTransaction().remove(FilterFragment.this).commit();
                ((MainActivity) getActivity()).getSupportFragmentManager().popBackStack();
            }
        });


        return rootView;
    }


}
