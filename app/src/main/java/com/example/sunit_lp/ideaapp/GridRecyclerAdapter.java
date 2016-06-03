package com.example.sunit_lp.ideaapp;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GridRecyclerAdapter extends RecyclerView.Adapter<GridRecyclerAdapter.TextViewHolder>
{
    private List<String> labels;

    private Context mContext;
    OnItemClickListener mItemClickListener;           //Custom ItemListener from interface above
    public OnItemSelectedListener mListener;          //For shared fragment transition
    //public OnItemSelectedListener mListener;
    private List<Map<String,?>> adList = new ArrayList<Map<String, ?>>();                 //DUMMY DATA: List of movies, passed by "reference"
    String mainFilter;

    /**
         Interface OnItemClickListener: interface used by app to set the event listeners
     */
    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
        void onItemMapClick(View view, int position);
        void onItemFavClick(View view, int position);
    }

    //2nd Constructor
    public GridRecyclerAdapter(Context context, List<Map<String, ?>> data, String filter)
    {
        /*
        labels = new ArrayList<String>(count);
        for (int i = 0; i < count; ++i)
        {
            labels.add(String.valueOf(i));
        }
        */
        mContext = context;
        this.adList = data;
        this.mainFilter = filter;
    }

    //Allow Adapter using activity or fragment to set event listeners
    public void setOnItemClickListener(final OnItemClickListener mItemClickListener)
    {
        this.mItemClickListener = mItemClickListener;
    }


    @Override
    public TextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_recycler, parent, false);
        return new TextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TextViewHolder holder, final int position)
    {
        //Bind Dummy Data for now
        if(adList.size() != 0)
        {
            Map<String, ?> data = this.adList.get(position);
            holder.bindData(data, position);
        }
    }

    @Override
    public int getItemCount() {
        return adList.size();
    }

    public class TextViewHolder extends RecyclerView.ViewHolder
    {
        public TextView tvPrice;
        public TextView tvTitle;
        public TextView tvDescription;
        public ImageView imageView;
        public ImageView favImageView;
        public ImageView mapImageView;

        public TextViewHolder(View itemView)
        {
            super(itemView);
            tvPrice = (TextView) itemView.findViewById(R.id.item_tv_price);
            tvTitle = (TextView) itemView.findViewById(R.id.item_tv_title);
            tvDescription = (TextView) itemView.findViewById(R.id.item_tv_description);
            imageView = (ImageView) itemView.findViewById(R.id.item_iv);
            favImageView = (ImageView) itemView.findViewById(R.id.item_iv_fav);
            mapImageView = (ImageView) itemView.findViewById(R.id.item_iv_map);

            //Check for favorites
           /* MainActivity.firebaseRef.addAuthStateListener(new Firebase.AuthStateListener()
            {
                @Override
                public void onAuthStateChanged(AuthData authData)
                {
                    if (authData == null)
                    {
                        //user not logged in
                        favImageView.setVisibility(View.GONE);
                    } else
                    {
                        favImageView.setVisibility(View.VISIBLE);
                    }
                }
            });*/

            if(!mainFilter.equals("Housing"))
                mapImageView.setVisibility(View.GONE);
            else
                mapImageView.setVisibility(View.VISIBLE);

            //ViewHolder onClickListener
            itemView.setOnClickListener(new View.OnClickListener()                    //Recycler Listener
            {
                @Override
                public void onClick(View v)
                {
                    if (mItemClickListener != null)
                        mItemClickListener.onItemClick(v, getAdapterPosition());
                }
            });

            favImageView.setOnClickListener(new View.OnClickListener()                 //Favorites Listener
            {
                @Override
                public void onClick(View v)
                {
                    if (mItemClickListener != null)
                        favImageView.setVisibility(View.GONE);
                        mItemClickListener.onItemFavClick(v, getAdapterPosition());
                }
            });

            mapImageView.setOnClickListener(new View.OnClickListener()                 //Maps Listener
            {
                @Override
                public void onClick(View v)
                {
                    if (mItemClickListener != null)
                        mItemClickListener.onItemMapClick(v, getAdapterPosition());
                }
            });
        }

        //Called by onBindViewHolder() to initialize components
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public void bindData(Map<String, ?> ad, int position)
        {
            tvPrice.setText("$" + ad.get("price"));
            tvTitle.setText((String) ad.get("title"));
            tvDescription.setText((String) ad.get("description"));
            imageView.setTransitionName((String) ad.get("title"));           //for shared element transition
            tvTitle.setTransitionName((String) ad.get("title") + "_tv");     //for shared element transition
            tvPrice.setTransitionName((String) ad.get("price") + "_tv2");

            FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(FirebaseAuth authData) {
                    if (authData == null) {
                        favImageView.setVisibility(View.GONE);
                    } else {
                        favImageView.setVisibility(View.VISIBLE);
                    }
                }
            });

                        //2. Set Image
            if (!((String) ad.get("url_img")).isEmpty()) {
                byte[] imageAsBytes = Base64.decode((String) ad.get("url_img"), 0);
                Bitmap bmp_pic = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                //byte[] imageAsBytes = Base64.decode(userReg.getProfile_pic(), 0);
                //Bitmap bmp_pic = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
                Bitmap bt = Bitmap.createScaledBitmap(bmp_pic, bmp_pic.getWidth(), bmp_pic.getHeight(), false);

                imageView.setImageBitmap(bt);                            //old method
            }

//            //Caching code
//            String imgurl = (String) ad.get("url_img");
//            Log.d("IMGURL", imgurl.toString());
//            if(!imgurl.equals(""))
//            {
//                String substr = imgurl.substring(0, Math.min(imgurl.length(), 100));
//                final Bitmap bitmap = AdListFragment.mImgMemoryCache.get(substr);          //check cache if image exists
//                if (bitmap != null)                 //if image found in cache, use it
//                {
//                    imageView.setImageBitmap(bitmap);
//                } else                               //else, process image
//                {
//                    if (!((String) ad.get("url_img")).isEmpty()) {
//                        byte[] imageAsBytes = Base64.decode((String) ad.get("url_img"), 0);
//                        Bitmap bmp_pic = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
//
//
//                        //byte[] imageAsBytes = Base64.decode(userReg.getProfile_pic(), 0);
//                        //Bitmap bmp_pic = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
//                        Bitmap bt = Bitmap.createScaledBitmap(bmp_pic, bmp_pic.getWidth(), bmp_pic.getHeight(), false);
//
//                        if (bt != null)                                               //Q image caching
//                        {
//                            String substr2 = imgurl.substring(0, Math.min(imgurl.length(), 100));           //use first 50 char string as key for caching
//                            AdListFragment.mImgMemoryCache.put(substr2, bt);
//                        }
//
//                        imageView.setImageBitmap(bt);                            //old method
//                    }
//                }
//            }
        }
    }
}