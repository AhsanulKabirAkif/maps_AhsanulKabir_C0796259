package com.example.maps_ahsanulkabir_c0796259;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private static final int REQUEST_CODE = 1;
    private Marker homeMarker;

    private int count = 0;


    Polygon shape;
    private static final int POLYGON_SIDES = 4;
    List<Marker> markers = new ArrayList();

    // location with location manager and listener
    LocationManager locationManager;
    LocationListener locationListener;
    Location location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                setHomeMarker(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (!hasLocationPermission())
            requestLocationPermission();
        else
            startUpdateLocation();


        // apply long press gesture
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
//                Location location = new Location("Your Destination");
//                location.setLatitude(latLng.latitude);
//                location.setLongitude(latLng.longitude);
                // set marker
                setMarker(latLng);
            }

            private void setMarker(LatLng latLng) {
                count ++;
                if(count == 1) {
                    MarkerOptions options = new MarkerOptions().position(latLng).title("Your Destination").snippet("A");

                    // check if there are already the same number of markers, we clear the map.
                    if (markers.size() == POLYGON_SIDES)
                        clearMap();

                    markers.add(mMap.addMarker(options));
                    if (markers.size() == POLYGON_SIDES)
                        drawShape();


                }
                if(count == 2) {
                    MarkerOptions options = new MarkerOptions().position(latLng).title("Your Destination").snippet("B");

                    // check if there are already the same number of markers, we clear the map.
                    if (markers.size() == POLYGON_SIDES)
                        clearMap();

                    markers.add(mMap.addMarker(options));
                    if (markers.size() == POLYGON_SIDES)
                        drawShape();
                }
                if(count == 3) {
                    MarkerOptions options = new MarkerOptions().position(latLng).title("Your Destination").snippet("C");

                    // check if there are already the same number of markers, we clear the map.
                    if (markers.size() == POLYGON_SIDES)
                        clearMap();

                    markers.add(mMap.addMarker(options));
                    if (markers.size() == POLYGON_SIDES)
                        drawShape();
                }
                if(count == 4) {
                    MarkerOptions options = new MarkerOptions().position(latLng).title("Your Destination").snippet("D");

                    // check if there are already the same number of markers, we clear the map.
                    if (markers.size() == POLYGON_SIDES)
                        clearMap();

                    markers.add(mMap.addMarker(options));
                    if (markers.size() == POLYGON_SIDES)
                        drawShape();
                }

            }

            private void drawShape() {
                PolygonOptions options = new PolygonOptions()

                        .fillColor(Color.GREEN)
                        .fillColor(0x35000000)
                        .strokeColor(Color.RED)
                        .strokeWidth(5);

                for (int i=0; i<POLYGON_SIDES; i++) {
                    options.add(markers.get(i).getPosition());
                }

                shape = mMap.addPolygon(options);
                count = 0;

            }
            private void clearMap() {
                for (Marker marker: markers)
                    marker.remove();

                markers.clear();
                shape.remove();
                shape = null;
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Geocoder geocoder = new Geocoder(MapsActivity.this);

                Double markerLon = marker.getPosition().longitude;
                Double markerLang = marker.getPosition().latitude;

                try {
                    List<Address> addressList = geocoder.getFromLocation(markerLang, markerLon, 1);
                    Address address = addressList.get(0);

                    String street = address.getThoroughfare();
                    String adNumber = address.getFeatureName();
                    String postalCode = address.getPostalCode();
                    String city = address.getSubAdminArea();
                    String province = address.getAdminArea();

                    Toast.makeText(MapsActivity.this,adNumber+" "+ street + " "+city+" "+province+" "+postalCode,Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }



                return false;
            }
        });
    }


    private void startUpdateLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);

    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void setHomeMarker(Location location) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions options = new MarkerOptions().position(userLocation)
                .title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .snippet("Your Location");
        homeMarker = mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (REQUEST_CODE == requestCode) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
            }
        }
    }
}
