package com.example.sunit_lp.ideaapp;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DialogMapFragment extends DialogFragment
{
    private static final String ARG_MAP_DATA = "FIREBASE_AD_MAP";

    //CONSTANTS
    public static final String TAG = MainActivity.class.getSimpleName();

    private GoogleMap googleMap;
    private SupportMapFragment gmap;
    public double gpsLatitude = 0.0, gpsLongitude = 0.0;             //GPS Coordinates
    LocationManager locationManager;
    Location location;
    //List<Map<String,?>> adList;
    //HashMap<String, ?> adInfo;
    UserAd userAd;
    //UserReg userReg;

    public DialogMapFragment()
    {

    }

    public DialogMapFragment(UserAd userAd)
    {
        // Required empty public constructor
        this.userAd = userAd;
    }

    /*
    public static DialogMapFragment newInstance()
    {
        DialogMapFragment fragment = new DialogMapFragment();
        return fragment;
    }

    public static DialogMapFragment newInstance(UserAd userAd)
    {
        DialogMapFragment fragment = new DialogMapFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MAP_DATA, userAd);         //Allows fragments to send data to other fragments
        fragment.setArguments(args);
        return fragment;
    }
    */

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

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
        final View rootView = inflater.inflate(R.layout.fragment_dialog_map, container, false);

        getDialog().setTitle(userAd.getTitle());        //set title of dialogbox
        //1. Get current Location
        //location = getLastKnownLocation();

        //2. Initialize Google Map
        createMapView();
        configureMap(googleMap, location);

        return rootView;
    }

    private void createMapView()
    {
        /**
         * Catch the null pointer exception that
         * may be thrown when initialising the map
         */
        try
        {
            //googleMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            //gmap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map))
            //gmap.getMapAsync();

            Log.d("DIALOGMAP", "googlemapping");

            if(null == googleMap)
            {
                Log.d("DIALOGMAP", "googlemap not null");
                ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMapAsync(new OnMapReadyCallback()
                {
                    @Override
                    public void onMapReady(GoogleMap googleMap)
                    {
                        addMarker(userAd.getAddress(), userAd.getTitle());
                        LatLng latlng = geocodeAddress(userAd.getAddress());
                        CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(latlng, 17);
                        googleMap.animateCamera(camera);
                    }
                });

                googleMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();


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
        //if (locationManager != null)              //check permission?
        //{
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                map.setMyLocationEnabled(true);                                 // Creates MyLocation button/layer
            }
        //}

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (googleMap != null)
            getFragmentManager().beginTransaction().remove((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).commit();
    }
}
