package com.techelogy.locationextractor;

import android.Manifest;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationServices;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1001;
    private final int REQUEST_CODE_ACCESS_LOCATION = 101;
    GoogleApiClient mGoogleApiClient;

    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidgets();

        if (isGooglePlayServicesAvailable()) {

            System.out.println("isGooglePlayServicesAvailable() = " + isGooglePlayServicesAvailable());
            initGoogleApiClient();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void processLocationPermission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // show explanation why this permission is needed


                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_ACCESS_LOCATION);

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_ACCESS_LOCATION);
            }


        } else {
            System.out.println("permission is already granted= ");


            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            System.out.println("mLastLocation = " + mLastLocation);


            if (mLastLocation != null) {
                System.out.println("mLastLocation.getLatitude() = " + mLastLocation.getLatitude());
                System.out.println("mLastLocation.getLongitude() = " + mLastLocation.getLongitude());

                getAddressFromLatAndLong(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                //  getAddressFromLatAndLong(-35.27727,149.11713);


            } else {
                LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
                boolean enabled = service
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (!enabled) {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }

                // createLocationRequest();
            }
        }
    }

/*
    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);


        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());


    }*/


    private void initWidgets() {
        context = MainActivity.this;
    }

    private void initGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


    }


    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        System.out.println("on connected has been called = ");


        processLocationPermission();

    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("onConnectionSuspended has been called = ");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        System.out.println("onConnectionFailed has been called = " + connectionResult.getErrorMessage());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        System.out.println("String[] permissions = " + permissions);
        System.out.println("int[] grantResults= " + grantResults);

        switch (requestCode) {
            case REQUEST_CODE_ACCESS_LOCATION:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted successfully

                    System.out.println("permission granted successfully = ");


                    processLocationPermission();


                } else {
                    //

                    System.out.println("permission could not be granted= ");
                }
                break;

        }
    }


    private void getAddressFromLatAndLong(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5


            Address addressObj = addresses.get(0);
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String subLocality = addressObj.getSubLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

            System.out.println("address = " + address);
            System.out.println("subLocality = " + subLocality);
            System.out.println("city = " + city);
            System.out.println("state = " + state);
            System.out.println("country = " + country);
            System.out.println("postalCode = " + postalCode);
            System.out.println("knownName = " + knownName);
            System.out.println("FeatureName = " + addressObj.getFeatureName());
            System.out.println("getPremises = " + addressObj.getPremises());


        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(MainActivity.this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");

            }
            return false;
        }
        return true;
    }
}
