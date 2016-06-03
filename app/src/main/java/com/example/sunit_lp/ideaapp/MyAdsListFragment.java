package com.example.sunit_lp.ideaapp;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyAdsListFragment extends Fragment
{
    private static final String ARG_SECTION_NUMBER="section_number";
    //MovieData movieData;
    MyAds_Adapter myAds_adapter;
    RecyclerView mRecycleView;
    LinearLayoutManager mLayoutManager;
    DatabaseReference ref = null;
    DatabaseReference ref1 = null;
    DatabaseReference ref2=null;
    //Edit Ad variables
    ImageView displayImage;
    EditText textcategory;
    EditText textsubcategory;
    EditText displaytitle;
    EditText updatedescribe;
    EditText addinfo;
    EditText updPrice;
    Button button_editad;
    //private List<Map<String,?>> movieList;
   /* public static LruCache<String, Bitmap> mMemoryCache;*/
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.myads_fragment,container,false);

        //movieData=new MovieData();
        mRecycleView=(RecyclerView)rootView.findViewById(R.id.Ad_recycleview);
        mRecycleView.setHasFixedSize(true);
        mLayoutManager=new LinearLayoutManager(getActivity());
        mRecycleView.setLayoutManager(mLayoutManager);
        //myAds_adapter=new MyAds_Adapter(getActivity(),movieData.getMoviesList());
        //ref1 = new Firebase ("https://cusexchange.firebaseio.com/svij@syr,edu/IndividualAds");
        ref= FirebaseDatabase.getInstance().getReferenceFromUrl("https://cusexchange.firebaseio.com/");
        final FirebaseAuth authData = FirebaseAuth.getInstance();//.getAuth();
        String temp_mod=(authData.getCurrentUser().getEmail()).toString();
        final String temp_mod1=temp_mod.replace('.', ',');
        ref1=FirebaseDatabase.getInstance().getReferenceFromUrl("https://cusexchange.firebaseio.com/" + temp_mod1 + "/IndividualAds");
        System.out.println(ref1.toString());
        myAds_adapter =new MyAds_Adapter(UserAd.class,R.layout.myads_content_layout,MyAds_Adapter.ViewHolder.class,ref1,getActivity());
        mRecycleView.setAdapter(myAds_adapter);
        //defaultAnimation();
        //itemAnimation();
        //adapterAnimation();
        myAds_adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                /*Movie cloud=myAds_adapter.getItem(position);
                //final HashMap<String,?> movie =(HashMap<String, ?>) movieData.getItem(position);
                //String id=(String)movie.get("id");
                *//**//*String nam= (String) movieData.getItem(position).get("id");
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container2, Fragment_DetailView.newInstance(cloud)).addToBackStack(null).commit();
                // mlistenerOnListItemSelected(position,movie);
                //final Firebase ref=new Firebase("https://sunitvijay.firebaseio.com/moviedata");
                //myAds_adapter=new MyAds_Adapter(Movie.class,R.layout.myads_content_layout,MyAds_Adapter.ViewHolder.class,ref,getActivity());
                Log.d("debug", "click");
                //if(mCurCheckPosition==0)
*/
            }

            @Override
            public void onItemLongClick(View view, int position) {
                //getActivity().startActionMode(new ActionBarCallBack(position));
            }

            @Override
            public void onOverflowMenuClick(View v, final int position) {
                PopupMenu popupMenu = new PopupMenu(getActivity(), v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.ad_delete:
                                UserAd objDel = myAds_adapter.getItem(position);
                                System.out.println(ref1.child(objDel.getKey()));
                                ref1.child(objDel.getKey()).removeValue();
                                return true;
                            case R.id.ad_edit:
                                UserAd objAdd = myAds_adapter.getItem(position);
                                callEditDialog(objAdd);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                MenuInflater inflater1 = popupMenu.getMenuInflater();
                inflater1.inflate(R.menu.contextual_or_popup_menu, popupMenu.getMenu());
                popupMenu.show();
            }

            @Override
            public void onFavClick(View v, int position) {
                /*UserAd userAd=myAds_adapter.getItem(position);
                //String getKey=userAd.getKey().toString();
                final AuthData authData = ref.getAuth();
                String temp_mod=(authData.getProviderData().get("email")).toString();
                final String temp_mod1=temp_mod.replace('.', ',');
                ref2=new Firebase("https://cusexchange.firebaseio.com/"+temp_mod1+"/Favorites");
                ref2.child(userAd.getKey()).setValue(userAd);*/
                // Dont want create another instance in firebase. Need to use hashmap like for all ads

            }
        });
        return rootView;
    }

    public void callEditDialog(final UserAd obj) {
        final Dialog myDialog = new Dialog(getActivity());
        myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        myDialog.setContentView(R.layout.editad);
        myDialog.show();
        myDialog.setCancelable(false);
        //myDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        button_editad = (Button) myDialog.findViewById(R.id.button_editad);
        displayImage=(ImageView)myDialog.findViewById(R.id.displayImage);
        textcategory=(EditText)myDialog.findViewById(R.id.textcategory);
        textsubcategory=(EditText)myDialog.findViewById(R.id.textsubcategory);
        displaytitle=(EditText)myDialog.findViewById(R.id.displaytitle);
        updatedescribe=(EditText)myDialog.findViewById(R.id.updatedescribe);
        addinfo=(EditText)myDialog.findViewById(R.id.addinfo);
        updPrice=(EditText)myDialog.findViewById(R.id.updPrice);
        if(obj.getCategory().equals("Housing"))
        {
            addinfo.setVisibility(View.VISIBLE);
            addinfo.setText(obj.getAddress());
            addinfo.setEnabled(false);
        }
        displaytitle.setText(obj.getTitle());
        textcategory.setText(obj.getCategory());
        textsubcategory.setText(obj.getSubCategory());
        updatedescribe.setText(obj.getDescription());
        updPrice.setText(obj.getPrice());
        if(!obj.getUrl_img().isEmpty()) {
            byte[] imageAsBytes = Base64.decode(obj.getUrl_img(), 0);
            Bitmap bmp_pic = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
            Bitmap bt=Bitmap.createScaledBitmap(bmp_pic, bmp_pic.getWidth(), bmp_pic.getHeight(), false);
            displayImage.setImageBitmap(bt);
        }
        displaytitle.setEnabled(false);
        textcategory.setEnabled(false);
        textsubcategory.setEnabled(false);

        myDialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    myDialog.dismiss();
                }
                return false;
            }
        });


        button_editad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_editad.setEnabled(false);

                final ProgressDialog progressDialog = new ProgressDialog(getContext(),
                        R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Updating...");
                progressDialog.show();

                // TODO: Implement your own authentication logic here.

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                // On complete call either onLoginSuccess or onLoginFailed
                                // onLoginFailed();
                                obj.setDescription(updatedescribe.getText().toString());
                                obj.setPrice(updPrice.getText().toString());
                                ref1.child(obj.getKey()).setValue(obj);
                                progressDialog.dismiss();
                                myDialog.dismiss();
                            }
                        }, 3000);
            }
        });

    }

    public static MyAdsListFragment newInstance(int pos)
    {
        MyAdsListFragment fragment=new MyAdsListFragment();
        /*Bundle args=new Bundle();
        args.putInt(ARG_SECTION_NUMBER,pos);
        fragment.setArguments(args);*/
        return fragment;
    }
   /* public class ActionBarCallBack implements ActionMode.Callback {
        int position;
        public ActionBarCallBack(int position) {
            this.position=position;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.contextual_or_popup_menu,menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            //HashMap hm=(HashMap)movieData.getItem(position);
            *//*Movie mv=task1_adapter.getItem(position);
            mode.setTitle(mv.getName());*//*
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id=item.getItemId();
            switch (id){
                case R.id.ad_delete:
                   *//* Movie objDel=task1_adapter.getItem(position);
                    ref.child(objDel.getId()).removeValue();*//*
                   *//* Log.d("Delete", "DoubleClick");
                    final JSONObject[] object = new JSONObject[1];//=new JSONObject();
                    if(item!=null)
                        object[0] =new JSONObject();
                    else
                    object[0] =null;
                    Runnable runnable=new Runnable() {
                        @Override
                        public void run() {
                            String url=MovieDataJson.PHP_SERVER+"delete/id/";
                            object[0] =MovieDataJson.generateJSONObjectforDeletion(position);
                            MyUtility.sendHttPostRequest(url, object[0]);
                        }
                    };
                    new Thread(runnable).start();
                    movieData.getMoviesList().remove(position);
                    task1_adapter.notifyItemRemoved(position);*//*
                    mode.finish();
                    break;
                case R.id.ad_edit:
                    *//*Movie objAdd=task1_adapter.getItem(position);
                    objAdd.setName(objAdd.getName()+"_new");
                    objAdd.setId(objAdd.getId() + "_new");
                    ref.child(objAdd.getId()).setValue(objAdd);*//*
                    *//*final JSONObject[] object1 = new JSONObject[1];//=new JSONObject();
                    if(item!=null)
                        object1[0] =new JSONObject();
                    else
                        object1[0] =null;
                    Runnable runnable1=new Runnable() {
                        @Override
                        public void run() {
                            String url=MovieDataJson.PHP_SERVER+"add/";
                            object1[0] =MovieDataJson.generateJSONObjectforAddition(position);
                            MyUtility.sendHttPostRequest(url, object1[0]);
                        }
                    };
                    new Thread(runnable1).start();
                    movieData.getMoviesList().add(position + 1, (Map<String, ?>) movieData.getItem(position).clone());
                    task1_adapter.notifyItemInserted(position + 1);*//*
                    mode.finish();
                    break;
                default:
                    break;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    }*/
   @Override
   public void onDetach()
   {
       super.onDetach();
       //Log.d("onDetach()", "onDetach() called");
       ((MainActivity)getActivity()).getSupportActionBar().setTitle(((MainActivity)getActivity()).toolbar_title);
   }
}
