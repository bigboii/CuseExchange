package com.example.sunit_lp.ideaapp;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ContactUsFragment extends android.support.v4.app.Fragment
{
    private static final String ARG_SECTION_NUMBER="section_number";
    EditText problem_stmt;
    Button problem_sbmt;

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        //outState.putSerializable("section_number", (Serializable) movieList);

    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.fragment_contact_us);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview=inflater.inflate(R.layout.fragment_contact_us,container,false);
        problem_sbmt =(Button)rootview.findViewById(R.id.problem_sbmt);
        problem_stmt=(EditText)rootview.findViewById(R.id.problem_stmt);

        problem_sbmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"sunitvijay88@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT,"Problem with CuseXchange" );
                i.putExtra(Intent.EXTRA_TEXT, problem_stmt.getText());
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }*/

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:" + "sunitvijay88@gmail.com"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "CuseXchange Customer Issue");
                emailIntent.putExtra(Intent.EXTRA_TEXT, problem_stmt.getText());

                try {
                    startActivity(Intent.createChooser(emailIntent, "Send email using..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "No email clients installed.", Toast.LENGTH_SHORT).show();
                }
                getActivity().getSupportFragmentManager().popBackStack();
            }

        });

        return rootview;
    }

    public static ContactUsFragment newInstance(int pos)
    {
        ContactUsFragment fragment=new ContactUsFragment();
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
