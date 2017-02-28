package com.miawoltn.emergencydispatch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Muhammad Amin on 2/22/2017.
 */

public class Operations {


    public static final String TRACKER_LOG_FILENAME = "tracker.log";

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
    public static AlertDialog.Builder showSettingsAlert(final Context context, String title, String message, String positiveButton, String negativeButton, final DialogCancelListener dialogCancelListener)
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

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

        alertDialog.show();
        return  alertDialog;
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

    public static String postRequest(String endPoint, String data) {
        String message = "";
        if(endPoint == null || endPoint.trim().length() == 0) {
            message = "enpoint not specified";
            return message;
        }

        HttpURLConnection connection;
        try {
            URL url = new URL(endPoint);
            connection = (HttpURLConnection)url.openConnection();
            StringBuilder stringBuilder = new StringBuilder();
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            JSONObject jsonObject = new JSONObject(data);
            String postData = jsonObject.toString();
            byte[] outputBytes = postData.getBytes("UTF-8");
            OutputStream os = connection.getOutputStream();
            os.write(outputBytes);

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
            e.printStackTrace();
            message = e.getMessage();
        }
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
}
