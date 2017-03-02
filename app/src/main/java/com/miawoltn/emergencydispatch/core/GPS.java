package com.miawoltn.emergencydispatch.core;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.*;
import android.widget.Toast;

import com.miawoltn.emergencydispatch.util.Operations;
import com.miawoltn.emergencydispatch.R;

/**
 * Created by Muhammad Amin on 2/18/2017.
 */

public class GPS {

    LocationManager locationManager;
    LocationListener myLocationListener;
    LocationSettingsDialogListener locationRequestCancel;
    static GPS localInstance = null;
    static Context context;
    Location location;
    long minTime = 0;
    long minDistance = 0;



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

    public void requestLocationUpadate(LocationSettingsDialogListener locationRequestCancel) {
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

        showSettings();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, myLocationListener);
        //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, myLocationListener);
        Log.i("Last known location", String.valueOf(locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)));

    }

    public void requestTrackingLocationUpdate() {
        //noinspection MissingPermission
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, myLocationListener);
    }

    public void removeLocationUpadate() {
        //noinspection MissingPermission
        locationManager.removeUpdates(myLocationListener);
    }

    public void showSettings() {


        if (!isGPSEnabled()) {
            final AlertDialog alert =  Operations.showSettingsAlert(context,context.getString(R.string.GPSAlertDialogTitle), context.getString(R.string.GPSAlertDialogMessage), context.getString(R.string.settings), context.getString(R.string.cancel), locationRequestCancel);
            // Hide after some seconds
            final Handler handler  = new Handler();
            final Runnable runnable = new Runnable() {
                boolean flag = false;
                @Override
                public void run() {
                    if (alert.isShowing()) {
                        handler.removeCallbacks(this);
                       // Toast.makeText(context,"Dialog canceled.",Toast.LENGTH_SHORT).show();
                        alert.dismiss();
                        Toast.makeText(context,"Dialog dismissed.", Toast.LENGTH_SHORT).show();
                        Operations.sendBroadcast(context, context.getString(R.string.location_request_dialog_timeout));
                    }
                }
            };


          /*  alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    handler.removeCallbacks(runnable);
                    if(isGPSEnabled())  {
                        Toast.makeText(context,"Dialog canceled.",Toast.LENGTH_SHORT).show();
                        Operations.sendBroadcast(context, context.getString(R.string.location_request_dialog_timeout));
                    }

                }
            });*/

            handler.postDelayed(runnable, 10000);
        }
    }

}
