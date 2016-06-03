package com.example.sunit_lp.ideaapp;

/**
 * Created by YQ on 2/14/2016.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 RecyclerView Adapter
 */
class HousingListRecyclerAdapter extends RecyclerView.Adapter<HousingListRecyclerAdapter.MovieViewHolder>
{
    static final String PACKAGE_ID = "yq.lab4:id/";       //Needed for ID checking

    private List<Map<String,?>> data;                 //List of movies, passed by "reference"
    private Context mContext;
    OnItemClickListener mItemClickListener;           //Custom ItemListener from interface above

    /**
     Interface OnItemClickListener: interface used by app to set the event listeners
     */
    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
        void onOverflowMenuClick(View v, final int position);
    }

    //Constructor
    public HousingListRecyclerAdapter(Context myContext, List<Map<String, ?>> data)
    {
        this.data = data;
        mContext = myContext;
    }
/*
    @Override
    public int getItemViewType(int position)
    {
        if((Double) data.get(position).get("rating") < 8.0)
            return 0;
        //Used to change background of color based on position
        else
            return 1;
    }
*/
    //Create new views
    @Override
    public HousingListRecyclerAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v;

        //Log.d("onCreateViewHolder", "viewType:" + viewType);
        //inflate layout based on view types

        switch(viewType)
        {
            case 0:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder, parent, false);
                break;
            case 1:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder, parent, false);
                break;
            default:
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder, parent, false);
                break;
        }

        MovieViewHolder vh = new MovieViewHolder(v);
        return vh;
    }

    //Replace the context of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position)
    {

        Map<String, ?> movie = data.get(position);
        holder.bindMovieData(movie, position);

        holder.cardView.setCardBackgroundColor(Color.parseColor("#FF9800"));
    }

    //Return size of dataset
    @Override
    public int getItemCount()
    {
        return data.size();
    }

    //Allow app to set event listeners
    public void setOnItemClickListener(final OnItemClickListener mItemClickListener)
    {
        this.mItemClickListener = mItemClickListener;
    }

    //Duplicate cardview for long click
    public void duplicateItem(int position)
    {
        //Don't use assignment to copy. Instead, use the Map's clone() function.
        //Map mp = data.get(position);
        //mp.put("selection", false);
        //data.add(position + 1, mp);

        //Shallow copy
        data.get(position);
        data.add(position + 1, (Map<String,?>) ((HashMap<String, ?>) data.get(position)).clone());

        if((Boolean) data.get(position+1).get("selection") == true)
        {
            Log.d("duplicate", "true");
            //data.get(position+1).put("")
        }
        else
        {
            Log.d("duplicate", "false");
        }

        //data.get(position+1).put("selection", false);     //This line doesn't work for some reason

    }

    /**
     Custom Recycler ViewHolder
     */

    class MovieViewHolder extends RecyclerView.ViewHolder
    {
        public CardView cardView;
        public ImageView iv;
        public ImageView ivOverflow;
        public TextView tvTitle;
        public TextView tvDescription;

        //Constructor
        public MovieViewHolder(View v)
        {
            super(v);
            cardView = (CardView) v.findViewById(R.id.cardview);
            iv = (ImageView) v.findViewById(R.id.iv_viewholder);
            tvTitle = (TextView) v.findViewById(R.id.tv1_viewholder);
            tvDescription = (TextView) v.findViewById(R.id.tv2_viewholder);
            ivOverflow = (ImageView) v.findViewById(R.id.iv_overflow);

            //ViewHolder onClickListener
            v.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mItemClickListener != null)
                    {
                        mItemClickListener.onItemClick(v, getPosition());
                    }

                    //return true;
                }
            });

            //ViewHolder onLongClickListener
            v.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    if (mItemClickListener != null)
                    {
                        mItemClickListener.onItemLongClick(v, getPosition());
                    }

                    return true;
                }
            });

            //iv_overflow onClickListener
            if(ivOverflow != null)
            {
                ivOverflow.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if(mItemClickListener != null)
                        {
                            mItemClickListener.onOverflowMenuClick(v, getPosition());
                        }
                    }
                });
            }
        }

        //Called by Adapter to initialize components
        public void bindMovieData(Map<String, ?> movie, int position)
        {
            iv.setImageResource((Integer) movie.get("image"));
            tvTitle.setText((String) movie.get("name"));
            tvDescription.setText((String) movie.get("description"));
        }

    } // end of ViewHolder
}// end of Adapter