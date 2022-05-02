package com.example.creatinguser;

/* public class GoogleMaps extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private ActivityGoogleMapsBinding binding;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGoogleMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    /*@Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Adding markers based on their coordinates and then adding them to a map
        LatLng interlude = new LatLng(33.788542199826665, -118.13353625782406);
        LatLng blondies = new LatLng(33.79668758396616, -118.14309084425682);
        LatLng portCity = new LatLng(33.78254749337177, -118.14340355111402);
        LatLng crookedDuck = new LatLng(33.783980693882555, -118.13390584960761);
        LatLng mvpGrill = new LatLng(33.7957953681204, -118.12622952011328);

        marker = mMap.addMarker(new MarkerOptions().position(interlude).title("Interlude pub"));
        mMap.addMarker(new MarkerOptions().position(blondies).title("Blondie's"));
        mMap.addMarker(new MarkerOptions().position(portCity).title("Port City Tavern"));
        mMap.addMarker(new MarkerOptions().position(crookedDuck).title("The Crooked Duck"));
        mMap.addMarker(new MarkerOptions().position(mvpGrill).title("MVP Grill"));

        mMap.setOnMarkerClickListener(this);

        //second value is zoom, can be from 1 to 20
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(interlude, 15));

        //mMap.setOnMarkerClickListener((OnMarkerClickListener) this);

        


        //public void openBlondies()
        //{
        //    Intent intent = new Intent(this, blondie_info.xml);
        //    startActivity(intent);
        //}

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // on marker click we are getting the title of our marker
                // which is clicked and displaying it in a toast message.
                String markerName = marker.getTitle();
                //Toast.makeText(MapsActivity.this, "Clicked location is " + markerName, Toast.LENGTH_SHORT).show();
                if(marker.getTitle()=="Interlude pub")
                {
                    //setContentView(R.layout.blondie_info);
                    Intent intent = new Intent(getApplicationContext(), Interlude_info_v2.class);
                    startActivity(intent);
                }

                return false;
            }

    });


    @Override
    public boolean onMarkerClick(Marker marker) {

        if(marker.getTitle()=="Interlude pub")
        {
            //setContentView(R.layout.blondie_info);
            Intent intent = new Intent(getApplicationContext(), Interlude_info_v2.class);
            startActivity(intent);
        }


        //clicker works
        Toast.makeText(this, "My Position: " + marker.getPosition().latitude,Toast.LENGTH_LONG).show();

        return false;
    }
}} */

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class GoogleMaps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marker;

    // below are the latitude and longitude
    // of 4 different locations.

    LatLng interlude = new LatLng(33.788542199826665, -118.13353625782406);
    LatLng blondies = new LatLng(33.79668758396616, -118.14309084425682);
    LatLng portCity = new LatLng(33.78254749337177, -118.14340355111402);
    LatLng crookedDuck = new LatLng(33.783980693882555, -118.13390584960761);
    LatLng mvpGrill = new LatLng(33.7957953681204, -118.12622952011328);

    // two array list for our lat long and location Name;
    private ArrayList<LatLng> latLngArrayList;
    private ArrayList<String> locationNameArraylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // initializing our array lists.
        latLngArrayList = new ArrayList<>();
        locationNameArraylist = new ArrayList<>();

        // on below line we are adding
        // data to our array list.
        latLngArrayList.add(interlude);
        locationNameArraylist.add("Interlude");
        latLngArrayList.add(blondies);
        locationNameArraylist.add("Blondies");
        latLngArrayList.add(portCity);
        locationNameArraylist.add("Port City Tavern");
        latLngArrayList.add(crookedDuck);
        locationNameArraylist.add("Crooked Duck");
        latLngArrayList.add(mvpGrill);
        locationNameArraylist.add("MVP Grill");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // below line is to add marker to google maps
        for (int i = 0; i < latLngArrayList.size(); i++) {

            // adding marker to each location on google maps
            mMap.addMarker(new MarkerOptions().position(latLngArrayList.get(i)).title(locationNameArraylist.get(i)));

            // below line is use to move camera.

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(interlude, 15));
        }

        // adding on click listener to marker of google maps.
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // on marker click we are getting the title of our marker
                // which is clicked and displaying it in a toast message.
                String markerName = marker.getTitle();

                //if(markerName.equals("Interlude")) {
                switch(markerName) {

                    case "Interlude":
                        Intent i = new Intent(getApplicationContext(), Interlude_info_v2.class);
                        startActivity(i);
                        break;

                    case "Blondies":
                        Intent j = new Intent(getApplicationContext(),Blondie_Info_v3.class);
                        startActivity(j);
                        break;

                    case "Crooked Duck":
                        Intent k = new Intent(getApplicationContext(),CrookedDuck_info_v2.class);
                        startActivity(k);
                        break;

                    case "Port City Tavern":
                        Intent l = new Intent(getApplicationContext(),Port_City_info_v2.class);
                        startActivity(l);
                        break;

                    case "MVP Grill":
                        Intent m = new Intent(getApplicationContext(),MVP_Grill_info_v2.class);
                        startActivity(m);
                        break;






                    default:
                        Toast.makeText(GoogleMaps.this, "Didn't Work", Toast.LENGTH_SHORT).show();

                }

                //Toast.makeText(GoogleMaps.this, "Clicked location is " + markerName, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }
}