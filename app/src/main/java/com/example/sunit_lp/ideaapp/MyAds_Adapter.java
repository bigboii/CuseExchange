package com.example.sunit_lp.ideaapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.Query;
//import com.firebase.ui.FirebaseRecyclerAdapter;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Updated by Sunit on 3/20/2016.
 */
public class MyAds_Adapter extends FirebaseRecyclerAdapter<UserAd,MyAds_Adapter.ViewHolder> {
    private final Context context;
    //private static List<Map<String,?>> movieList;
    static OnItemClickListener mItemClickListener;
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public CardView cv;
        public ImageView Ad_img;
        public TextView Ad_name;
        public TextView Ad_desc;
        public TextView Ad_price;
        public ImageView Ad_img1;
        public ImageButton select_fav;
        public ViewHolder(View v)
        {
            super(v);

            cv=(CardView)v.findViewById(R.id.cv);
            Ad_img=(ImageView)v.findViewById(R.id.Ad_iVw);
            Ad_name=(TextView)v.findViewById(R.id.Ad_name);
            Ad_desc=(TextView)v.findViewById(R.id.Ad_description);
            Ad_price=(TextView)v.findViewById(R.id.Ad_price);
            Ad_img1=(ImageView)v.findViewById(R.id.Ad_iVwMovLoad);
            //select_fav=(ImageButton)v.findViewById(R.id.select_fav);
           v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mItemClickListener!=null)
                    {
                        mItemClickListener.onItemClick(v,getAdapterPosition());
                    }
                }
            });
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onItemLongClick(v, getAdapterPosition());
                    }
                    return true;
                }
            });
            /*if(select_fav!=null)
            {
                select_fav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mItemClickListener!=null)
                            select_fav.setVisibility(View.GONE);
                            mItemClickListener.onFavClick(v,getPosition());
                    }
                });
            }*/
            if(Ad_img1!=null){
                Ad_img1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mItemClickListener!=null)
                            mItemClickListener.onOverflowMenuClick(v,getPosition());
                    }
                });
            }
        }
    }
    public MyAds_Adapter(Class<UserAd> modelClass, int modelLayout, Class<ViewHolder> holder, Query ref, Context context)
    {
        super(modelClass, modelLayout, holder, ref);
        this.context=context;
        //this.movieList=movieList;
    }

    @Override
    protected void populateViewHolder(final ViewHolder viewHolder, UserAd userAd, int i) {
        //System.out.println(userAd.getTitle().toString());
        //Log.d("MyAds_Adapter" , userAd.getTitle().toString() +"    " + i);
        viewHolder.Ad_name.setText(userAd.getTitle().toString());
        viewHolder.Ad_desc.setText(userAd.getDescription().toString());
        viewHolder.Ad_price.setText(userAd.getPrice().toString() + "$");
    if(!userAd.getUrl_img().isEmpty()) {
        /*byte[] imageAsBytes = Base64.decode(userAd.getUrl_img(), 0);
        Bitmap bmp_pic = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        Bitmap bt=Bitmap.createScaledBitmap(bmp_pic, bmp_pic.getWidth(), bmp_pic.getHeight(), false);*/
        try {
            String encodedpath= URLEncoder.encode(userAd.getUrl_img(), "utf-8");
            System.out.println("Encoded String"+encodedpath);
            // Create a reference from an HTTPS URL
            // Note that in the URL, characters are URL escaped!
            StorageReference httpsReference = MainActivity.storage.getReferenceFromUrl("https://firebasestorage.googleapis.com/v0/b/project-7354348151753711110.appspot.com/o/"+encodedpath);
            //Download the file now
            final long ONE_MEGABYTE=1024*1024;
            httpsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    //Data for "image" is returns, use this as needed
                    Bitmap bmp_pic = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Bitmap bt = Bitmap.createScaledBitmap(bmp_pic, bmp_pic.getWidth(), bmp_pic.getHeight(), false);
                    viewHolder.Ad_img.setImageBitmap(bt);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Handle any errors
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //viewHolder.Ad_img.setImageBitmap(bt);
    }
        //Picasso.with(context).load(userAd.getUrl_img()).into(viewHolder.Ad_img);

    }
    public void setOnItemClickListener(final OnItemClickListener mItemClickListener)
    {
        this.mItemClickListener=mItemClickListener;
    }
}