package com.weebly.watermelon;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.weebly.watermelon.R;

public class MapActivity extends FragmentActivity{
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }
    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider =  locationManager.getBestProvider(criteria , true);
        Location location = locationManager.getLastKnownLocation(provider);
       // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
         //       new LatLng(location.getLatitude(), location.getLongitude()), 13));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                .zoom(12)                   // Sets the zoom
                .bearing(0)                // set north
                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        LocationListener locationListener = new LocationListener(){
            @Override
            public void onLocationChanged(Location location){
                mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Try").snippet("7.5").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))).showInfoWindow();

                if (location != null)
                {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(location.getLatitude(), location.getLongitude()), 13));

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                            .zoom(12)                   // Sets the zoom
                            .bearing(0)                // set north
                            .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                            .build();                   // Creates a CameraPosition from the builder
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                }

            }
            @Override
            public void onProviderDisabled(String provider){
            }
            @Override
            public void onProviderEnabled(String provider){}

            @Override
            public void onStatusChanged(String provider,int status, Bundle extras){}
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Toast toast = Toast.makeText(getApplicationContext(),"Option 1", Toast.LENGTH_SHORT);

        switch(item.getItemId()){
            case R.id.scan:
                toast.setText("Scan");
                toast.show();
                swapToScan();
                return true;
          //  case R.id.maps:
       //         toast.setText("Map");
         //       toast.show();
         //       return true;
            case R.id.help:
                toast.setText("Help");
                toast.show();
                swapToHelp();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
    private void swapToScan(){
        Intent intent = new Intent( MapActivity.this, MainActivity.class);
        startActivity(intent);

    }
    private void swapToHelp(){
        Intent intent = new Intent(MapActivity.this, Help.class);
        startActivity(intent);

    }
  /*  public void addMarkers(Location location, boolean demo){
        if(demo){
            DecimalFormat deci = new DecimalFormat("0.00");
            mMap.addMarker(new MarkerOptions().position(new LatLng(33.664520, -117.746448)).title(deci.format(Math.random()+10.0)).snippet("Costco Tustin").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))).showInfoWindow();
            mMap.addMarker(new MarkerOptions().position(new LatLng(33.772940, -117.940035)).title(deci.format(Math.random()+10.0)).snippet("Costco Tustin").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))).showInfoWindow();
            mMap.addMarker(new MarkerOptions().position(new LatLng(33.952545, -118.309450)).title(deci.format(Math.random()+10.0)).snippet("Costco Tustin").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))).showInfoWindow();

        }else {
            mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Try").snippet("7.5").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))).showInfoWindow();
        }
    }
    */
}

