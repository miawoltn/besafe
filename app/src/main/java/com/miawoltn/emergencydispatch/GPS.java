package com.miawoltn.emergencydispatch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.*;
import android.widget.Toast;

/**
 * Created by Muhammad Amin on 2/18/2017.
 */

public class GPS {

    LocationManager locationManager;
    LocationListener myLocationListener;
    static Context context;
    Location location;
    long minTime = 0;
    long minDistance = 0;
    static GPS localInstance = null;
    DialogCancelListener locationRequestCancel;

    /**
     *
     * @param context
     */
    private GPS(Context context) {
        this.context = context;
    }

    /**
     * Enforces the creation and use of a single instance of the GPS by other objects through out application's lifetime.
     *
     * @param context
     * @return
     */
    public static GPS getInstance(Context context) {
        if(localInstance == null) {
            localInstance = new GPS(context);
            return localInstance;
        }

        return  localInstance;
    }

    public void setLocationListener(LocationListener locationListener) {
        this.myLocationListener = locationListener;
    }

    /**
     *
     * @return
     */
    private boolean isGPSEnabled() {
        LocationManager contentResolver = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsStatus = contentResolver.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (gpsStatus) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @return
     */
    private boolean isNetEnabled() {
        LocationManager contentResolver = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean netStatus = contentResolver.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (netStatus) {
            return true;
        } else {
            return false;
        }
    }

    public void requestLocationUpadate(DialogCancelListener locationRequestCancel) {
        this.locationRequestCancel = locationRequestCancel;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            Toast.makeText(context,"Permission not granted.",Toast.LENGTH_SHORT).show();
            return ;
        }

         if (!isGPSEnabled() ) {
             Operations.showSettingsAlert(context,context.getString(R.string.GPSAlertDialogTitle), context.getString(R.string.GPSAlertDialogMessage), context.getString(R.string.settings), context.getString(R.string.cancel), locationRequestCancel);
         }
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, myLocationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, myLocationListener);
        Toast.makeText(context, locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)+" ",Toast.LENGTH_SHORT).show();

    }

    public void removeLocationUpadate() {
        //noinspection MissingPermission
        locationManager.removeUpdates(myLocationListener);
    }


    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {

            Toast.makeText(context,"Location changed: Lng: " + loc.getLongitude()+"Lat: " + loc.getLatitude(), Toast.LENGTH_SHORT).show();


        /*------- To get city name from coordinates -------- */
           /* String cityName = null;
            Geocoder gcd = new Geocoder(context, Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0));
                    cityName = addresses.get(0).getLocality();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            Toast.makeText(context, cityName, Toast.LENGTH_SHORT).show();*/
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(context,"Provider disabled.", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(context,"Provider enabled.", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Toast.makeText(context,"Status changed.", Toast.LENGTH_SHORT).show();

        }
    }



}