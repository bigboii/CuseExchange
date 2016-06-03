package com.example.sunit_lp.ideaapp;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment
{
    private static final String ARG_MAP_DATA = "FIREBASE_AD_MAP";

    //CONSTANTS
    public static final String TAG = MainActivity.class.getSimpleName();

    private GoogleMap googleMap;
    public double gpsLatitude = 0.0, gpsLongitude = 0.0;             //GPS Coordinates
    LocationManager locationManager;
    Location location;
    //List<Map<String,?>> adList;
    //HashMap<String, ?> adInfo;
    UserAd userAd;
    //UserReg userReg;

    public MapFragment()
    {
        // Required empty public constructor
    }

    public static MapFragment newInstance()
    {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    public static MapFragment newInstance(UserAd userAd)
    {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MAP_DATA, userAd);         //Allows fragments to send data to other fragments
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //movieData = new MovieData();                     //Initialize Dummy Data
        //adList = new ArrayList<Map<String,?>>();         //initialize list

        //allows MovieFragment to load MovieData when being swapped by MovieListFragment
        if(getArguments() != null)
        {
            userAd = (UserAd) getArguments().getSerializable(ARG_MAP_DATA);
            //Log.d("AdListFragment", "Category Filter: "+ categoryFilter);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        //loadAllData();                                            //get ads from firebase, also acts as a refresh when returning from filter activity
        //((MainActivity) getActivity()).isFilterApplied = false;   //reset
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        //1. Get current Location
        //location = getLastKnownLocation();

        //2. Initialize Google Map
        createMapView();
        configureMap(googleMap, location);

        //Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        //startActivity(gpsOptionsIntent);

        return rootView;
    }

    //Get last known location
    private Location getLastKnownLocation() {
        locationManager = (LocationManager)getContext().getSystemService(getContext().LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers)
        {
            try
            {
                Location l = locationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
            catch(SecurityException e)
            {
                /*
                if (locationManager != null) {
    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                */
            }
        }
        return bestLocation;
    }

    private void createMapView()
    {
        /**
         * Catch the null pointer exception that
         * may be thrown when initialising the map
         */
        try
        {
            if(null == googleMap){
                ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(new OnMapReadyCallback()
                {
                    @Override
                    public void onMapReady(GoogleMap googleMap)
                    {
                        addMarker(userAd.getAddress(), userAd.getTitle());
                        LatLng latlng = geocodeAddress(userAd.getAddress());
                        CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(latlng, 14);
                        googleMap.animateCamera(camera);
                    }
                });
                googleMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();


                /**
                 * If the map is still null after attempted initialisation, show an error to the user
                 */
                if(null == googleMap) {
                    Toast.makeText(getContext(), "Error creating map", Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch (NullPointerException exception){
            Log.e("mapApp", exception.toString());
        }
    }


    /**
     * Adds a marker to the map
     */
    private void configureMap(GoogleMap map, Location location)
    {
        if (map == null)
            return; // Google Maps not available
        try
        {
            MapsInitializer.initialize(getContext());
        }
        catch (Exception e) {
            Log.e(TAG, "Have GoogleMap but then error", e);
            return;
        }
        /* Enable UI buttons in map */
        if (locationManager != null)              //check permission?
        {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                map.setMyLocationEnabled(true);                                 // Creates MyLocation button/layer
            }
        }

        map.getUiSettings().setMyLocationButtonEnabled(true);           //
        map.getUiSettings().setZoomControlsEnabled(true);               //https://developers.google.com/maps/documentation/android/interactivity#zoom_controls

//        LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
//        CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(latlng, 14);
//        map.animateCamera(camera);
    }

    //GeoCode address to coordinates for googlemaps to use
    LatLng geocodeAddress(String place)
    {
        Geocoder gc = new Geocoder(getContext());
        LatLng latlng;
        if(gc.isPresent())
        {
            try
            {
                List<Address> list = gc.getFromLocationName(place, 1);

                Address address = list.get(0);
                double lat = address.getLatitude();
                double lng = address.getLongitude();
                latlng = new LatLng(address.getLatitude(), address.getLongitude());
                return latlng;
            }
            catch(Exception e)
            {

            }
        }

        return null;
    }

    //Add marker at address
    void addMarker(String address, String title)
    {
        LatLng latlng = geocodeAddress(address);
        googleMap.addMarker(new MarkerOptions().position(latlng).title(title));
    }


    //Reverse GeoCode
    void reverseGeocodeCoordinates()
    {
        Geocoder gc = new Geocoder(getContext());

        if(gc.isPresent())
        {
            try
            {
                List<Address> list = gc.getFromLocation(37.42279, -122.08506,1);

                Address address = list.get(0);

                StringBuffer str = new StringBuffer();
                str.append("Name: " + address.getLocality() + "\n");
                str.append("Sub-Admin Ares: " + address.getSubAdminArea() + "\n");
                str.append("Admin Area: " + address.getAdminArea() + "\n");
                str.append("Country: " + address.getCountryName() + "\n");
                str.append("Country Code: " + address.getCountryCode() + "\n");

                String strAddress = str.toString();
            }
            catch(Exception e)
            {

            }

        }
    }



/*
    //Async Task, convert JSON to geo location
    private class DataLongOperationAsynchTask extends AsyncTask<String, Void, String[]>
    {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String[] doInBackground(String... params) {
            String response;
            try {
                response = getLatLongByURL("http://maps.google.com/maps/api/geocode/json?address=mumbai&sensor=false");
                Log.d("response",""+response);
                return new String[]{response};
            } catch (Exception e) {
                return new String[]{"error"};
            }
        }

        @Override
        protected void onPostExecute(String... result) {
            try {
                JSONObject jsonObject = new JSONObject(result[0]);

                double lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lng");

                double lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lat");

                Log.d("latitude", "" + lat);
                Log.d("longitude", "" + lng);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }


    public String getLatLongByURL(String requestURL) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
    */

}
