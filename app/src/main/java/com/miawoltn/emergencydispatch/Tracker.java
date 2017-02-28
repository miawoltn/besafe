package com.miawoltn.emergencydispatch;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;

/**
 * Created by Muhammad Amin on 2/24/2017.
 */

public class Tracker extends AsyncTask<Location, Void, String> {

    private Context context;
    public Tracker(Context context) {
         this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Location... params) {
        if(Operations.isDeviceConnected(context)) {
            return  Operations.postRequest(context.getString(R.string.googlemap_endpoint), String.format("%f,%f",params[0].getLongitude(),params[0].getLatitude()));
        }
        return "Device offline;";
    }

    @Override
    protected void onPostExecute(String i) {
       Operations.writeLog(context,Operations.TRACKER_LOG_FILENAME,i);
    }
}
