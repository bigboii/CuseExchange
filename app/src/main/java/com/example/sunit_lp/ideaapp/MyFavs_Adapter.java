package com.example.sunit_lp.ideaapp;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.Query;
//import com.google.firebase.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Updated by Sunit on 3/20/2016.
 */
public class MyFavs_Adapter extends FirebaseRecyclerAdapter<UserAd,MyFavs_Adapter.ViewHolder> {
    private final Context context;
    //private static List<Map<String,?>> movieList;
    static OnItemClickListener mItemClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public CardView cv;
        public ImageView Ad_img;
        public TextView Ad_name;
        public TextView Ad_desc;
        public TextView Ad_price;
        public ImageButton Delete_fav;

        //Expand + Collapse variables
        private RelativeLayout headerLayout;
        private LinearLayout descentLayout;
        Button email_button;
        Button call_button;
        Button sms_button;
        public ViewHolder(View v)
        {
            super(v);

            cv=(CardView)v.findViewById(R.id.cv);
            Ad_img=(ImageView)v.findViewById(R.id.Ad_iVw);
            Ad_name=(TextView)v.findViewById(R.id.Ad_name);
            Ad_desc=(TextView)v.findViewById(R.id.Ad_description);
            Ad_price=(TextView)v.findViewById(R.id.Ad_price);
            Delete_fav=(ImageButton)v.findViewById(R.id.delete_fav);

            headerLayout = (RelativeLayout) v.findViewById(R.id.relLayout);
            descentLayout = (LinearLayout) v.findViewById(R.id.descent);
            call_button=(Button)v.findViewById(R.id.call_button);
            sms_button=(Button)v.findViewById(R.id.sms_button);
            email_button=(Button)v.findViewById(R.id.email_button);
            call_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callNumber();
                }
            });
            sms_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendSMSMessage();
                }
            });
            email_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mItemClickListener!=null)
                    {
                        mItemClickListener.onItemClick(v,getAdapterPosition());

                        if (descentLayout.getVisibility()==View.GONE)
                        {
                            expand();
                        }
                        else
                        {
                            collapse();
                        }
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
            if(Delete_fav!=null){
            Delete_fav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.onFavClick(v,getPosition());
                    }
                });
            }
        }

        /**
         Card Expand + Collapse animation
         */

        /**
         called when expanding
         */
        private void expand() {
            //set Visible
            descentLayout.setVisibility(View.VISIBLE);

            final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);     //measure width
            final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);    //measure height
            descentLayout.measure(widthSpec, heightSpec);

            ValueAnimator mAnimator = slideAnimator(0, descentLayout.getMeasuredHeight());
            mAnimator.start();
        }

        /**
         called when collapsing
         */
        public void collapse()
        {
            int finalHeight = descentLayout.getHeight();
            ValueAnimator animator = slideAnimator(finalHeight, 0);

            animator.addListener(new Animator.AnimatorListener()
            {
                @Override
                public void onAnimationEnd(Animator animator)
                {
                    //Height=0, but it set visibility to GONE
                    descentLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator)
                {

                }

                @Override
                public void onAnimationRepeat(Animator animator)
                {

                }

                @Override
                public void onAnimationStart(Animator animator)
                {

                }
            });
            animator.start();
        }

        private ValueAnimator slideAnimator(int start, int end) {

            ValueAnimator animator = ValueAnimator.ofInt(start, end);

            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
            {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator)
                {
                    //Update Height
                    int value = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = descentLayout.getLayoutParams();
                    layoutParams.height = value;
                    descentLayout.setLayoutParams(layoutParams);
                }
            });
            return animator;
        }
    }//end of viewholder


    public MyFavs_Adapter(Class<UserAd> modelClass, int modelLayout, Class<ViewHolder> holder, DatabaseReference ref, Context context)
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
    public static void callNumber() {
       /* // Note that this ACTION_CALL requires permission
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + mobile_seller));
        startActivity(callIntent);*/
    }

    public static void sendSMSMessage() {
        /*try {
            //userAd.getDataSnapshot().getRef().removeEventListener();
            Uri uri = Uri.parse("smsto:"+mobile_seller);
            // No permisison needed
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
            startActivity(smsIntent);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "SMS failed, please try again later!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }*/
    }
}