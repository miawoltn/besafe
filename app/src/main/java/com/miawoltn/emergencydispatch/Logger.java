package com.miawoltn.emergencydispatch;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by Muhammad Amin on 2/18/2017.
 */

public class Logger {

    public static String  LOG_FILE = "SOS.log";
    Context context;

    /**
     *
     * @param context
     */
    public Logger(Context context) {

    }


    public void saveLog(String data) {
        try {
            //opens the file for writing in a private context
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(LOG_FILE, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (Exception e) {
            android.util.Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    /**
     * Helper method for reading SOS history from file
     * @return
     */
    public String retrieveLog() {

        String content = "";

        try {
            //reads the content of the file
            InputStream inputStream = context.openFileInput(LOG_FILE);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String currentLine = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (currentLine = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(currentLine);
                }

                inputStream.close();
                content = stringBuilder.toString();
            }
        }
        catch (IOException e) {
            android.util.Log.e("login activity", "File not found: " + e.toString());
        }

        return content;
    }
}
