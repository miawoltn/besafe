package com.miawoltn.emergencydispatch.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.miawoltn.emergencydispatch.core.LocationSettingsDialogListener;
import com.miawoltn.emergencydispatch.R;
import com.miawoltn.emergencydispatch.core.Logger;
import com.miawoltn.emergencydispatch.core.TrackingDialogListener;
import com.miawoltn.emergencydispatch.fragment.HomeFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Muhammad Amin on 2/22/2017.
 */

public class Operations {


    public static final String TRACKER_LOG_FILENAME = "tracker.log";
    public static final String MESSAGE_ID = "id.message";
    public static final String USER_ID = "id.user";

    /**
     *
     * @param context
     * @return
     */
    public static boolean isDeviceConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;


        return false;
    }

    /**
     * Function to show settings alert dialog
     */
    public static AlertDialog showSettingsAlert(final Context context, String title, String message, String positiveButton, String negativeButton, final LocationSettingsDialogListener dialogCancelListener)
    {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        //Setting Dialog Title
        alertDialog.setTitle(title);

        //Setting Dialog Message
        alertDialog.setMessage(message);


        alertDialog.setPositiveButton(positiveButton, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });


        alertDialog.setNegativeButton(negativeButton, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialogCancelListener.onCancel();
                dialog.cancel();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
        return  alert;
    }

    /**
     * Function to show settings alert dialog
     */
    public static AlertDialog showTrackingAlert(final Context context, String title, String message, String positiveButton, String negativeButton, final TrackingDialogListener dialogCancelListener)
    {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        //Setting Dialog Title
        alertDialog.setTitle(title);

        //Setting Dialog Message
        alertDialog.setMessage(message);


        alertDialog.setPositiveButton(positiveButton, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
               dialogCancelListener.onTrackingAccept();
            }
        });


        alertDialog.setNegativeButton(negativeButton, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialogCancelListener.onTrackingIgnore();
                dialog.cancel();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
        return  alert;
    }

    public static void showSnackBar(View view, String content) {
         Snackbar.make(view, content, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
    }


    public static void sendBroadcast(Context context, String action) {
        context.sendBroadcast(new Intent(action));
    }


    public static String SendRequest(String site) throws Exception {
        if(site == null)
            return "empty";
        HttpURLConnection connection;
        try {
            URL url = new URL(site);
            connection = (HttpURLConnection)url.openConnection();
            StringBuilder stringBuilder = new StringBuilder();
            connection.setConnectTimeout(5000);
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = bufferedReader.readLine();
                while (line != null) {
                    stringBuilder.append(line);
                    line = bufferedReader.readLine();
                }
                return line;
            }
        }
        catch (Exception e){
            e.printStackTrace();return "Error";
        }
        return "empty";
    }

    public static String postRequest(String endPoint, Message data) {
        String message = null;
        if(endPoint == null || endPoint.trim().length() == 0) {
            message = "enpoint not specified";
            return message;
        }

        HttpURLConnection connection;
        try {
            Log.i("End point", "connecting to endpoint");
            URL url = new URL(endPoint);
            connection = (HttpURLConnection)url.openConnection();
            StringBuilder stringBuilder = new StringBuilder();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            JSONObject jsonObject = new JSONObject();
/*            jsonObject.put("message", String.valueOf(data.getDistressType()));
            jsonObject.put("longitude", String.valueOf(data.getLongitude()));
            jsonObject.put("latitude",String.valueOf(data.getLongitude()));
            jsonObject.put("phNumber", "08069036740");
            jsonObject.put("location", data.getLocationDetails());
            jsonObject.put("Failsafe", 0);*/
            String str = "{message:msg,longitude:long,latitude:lat,phNumber:num,location:loc,Failsafe:0}";

            String postData = jsonObject.toString();
            byte[] outputBytes = str.getBytes("UTF-8");
            OutputStream os = connection.getOutputStream();
            os.write(outputBytes);

            Log.i("End point", connection.getResponseMessage());
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){

                return "Success";
            }
        }
        catch (Exception e){
            e.printStackTrace();
            Log.i("End point",e.getMessage());
        }
        Log.i("End point"," ");
        return message;
    }


    /**
     * Helper method for saving settings to file
     * @param filename
     * @param data
     */
    public static void writeLog(Context context, String filename, String data) {
        try {
            //opens the file for writing in a private context
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_APPEND));
            outputStreamWriter.write(data);
            outputStreamWriter.write("\n");
            outputStreamWriter.close();
        }
        catch (Exception e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }




    /**
     * Helper method for reading settings from file
     * @param filename
     * @return
     */
    public static String readFromFile(Context context, String filename) {

        String ret = "";

        try {
            //reads the content of the file
            InputStream inputStream = context.openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (Exception e) {
            Log.e("login activity", "File not found: " + e.toString());
        }

        return ret;
    }

    public static void createNofication(Context context, String title, String message) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        Intent resultIntent = new Intent(context, HomeFragment.class);

        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =  PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }


    /**
     * Utitlity function for creating intents with extras
     * @param intentClass
     * @param bundle
     * @return
     */
    public static Intent createIntent(Context context, Class intentClass, Bundle bundle) {
        return  new Intent(context,intentClass).putExtras(bundle);
    }

    /**
     *
     * @param service
     */
    public static void startService(Context context, Intent service) {
        context.startService(service);
    }

    /**
     * Helper method for stopping a service
     * @param service
     */
    public static void stopService(Context context, Intent service) {
        context.stopService(service);
    }


    public static String generateURL(String endpoint) {
        return String.format("http://%s",endpoint);
    }


    public static void requestDispatchNumbers(Context context, final Logger logger) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://192.168.137.1/getEmergencyNumbers";
        final JSONObject jsonObject = new JSONObject();

        StringRequest stringRequest = new StringRequest( url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Response: ", response.toString());
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for(int i = 0; i < jsonArray.length(); i++) {
                                 JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                 logger.insertContact(jsonObject1.getString("Agency"), jsonObject1.getString("Number"), System.currentTimeMillis());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("json error", e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        ) {

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = " ";
                if (response != null) {
                    responseString = new String(response.data);
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        queue.add(stringRequest);
    }

    public static void postUserData(final Context context, UserData userData) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = Operations.generateURL("192.168.137.1/regUser");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Name", userData.getName());
            jsonObject.put("ContactNumber", userData.getNumber());
            jsonObject.put("Address", userData.getAddress());
        }
        catch (Exception e) {
            Log.e("Json object", e.getMessage());
        }

        final String requestBody = jsonObject.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Operations.writeLog(context,Operations.USER_ID, response);
                        Log.i("User details", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("User details", "error occured.");
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(new String(response.data));
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        queue.add(stringRequest);
    }
}
