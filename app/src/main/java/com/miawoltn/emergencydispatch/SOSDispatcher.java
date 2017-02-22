package com.miawoltn.emergencydispatch;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Locale;

/**
 * Created by Muhammad Amin on 2/18/2017.
 */

public class SOSDispatcher implements LocationListener, DialogCancelListener {

    final String SENT = "SMS_SENT";
    final String DELIVERED = "SMS_DELIVERED";
    Context context;
    GPS gps;

    boolean hasPendingDispatch = false;
    DistressType distressType = null;



    /**
     *
     *
     * @param context
     */
    public SOSDispatcher(Context context) {
        this.context = context;
    }


    public void Dispatch(DistressType distressType) {
        hasPendingDispatch = true;
        this.distressType = distressType;
        GPS gps = GPS.getInstance(context);
        gps.setLocationListener(this);
        gps.requestLocationUpadate(this);
    }


    /**
     * Sends SOS to the list of phone numbers specified.
     *
     * @param phoneNumbers
     * @param  SOS
     */
    public void sendSMS(List<String> phoneNumbers, String SOS) {
        for(String phoneNumber : phoneNumbers) {
            sendSMS(phoneNumber, SOS);
        }
    }

    /**
     * Sends an SOS as SMS to the number specified.
     *
     * @param phoneNumber
     * @param SOS
     */
    public void sendSMS(String phoneNumber, String SOS) {

       PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,  new Intent(DELIVERED), 0);

        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case AppCompatActivity.RESULT_OK:
                        Toast.makeText(context, R.string.result_ok, Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, R.string.generic_failure, Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, R.string.no_service, Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, R.string.null_pdu, Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, R.string.radio_off, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode())
                {
                    case AppCompatActivity.RESULT_OK:
                        Toast.makeText(context, "SOSDispatcher delivered",Toast.LENGTH_SHORT).show();
                        break;
                    case AppCompatActivity.RESULT_CANCELED:
                        Toast.makeText(context, "SOSDispatcher not delivered", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sosDispatcher = SmsManager.getDefault();
        sosDispatcher.sendTextMessage(phoneNumber, null, SOS, sentPI, deliveredPI);
    }


    /**
     * Sends SOS to the specified endpoint. Returns true if sent successfully and false if something happens and
     * message couldn't be sent, like, no internet, wrong endpoint, request timeout.
     *
     * @param endPoint
     * @param SOS
     * @return
     */
    public boolean sentToEndPoint(String endPoint, String SOS) {
        return  false;
    }


    public String getLocationDetails(Location location) {

            String details = " ";
            Geocoder gcd = new Geocoder(context, Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0));
                    Address address = addresses.get(0);
                    details = String.format("%s, %s, %s", address.getAddressLine(0), address.getAddressLine(1), address.getAddressLine(2));
                }
            }
            catch (IOException e) {
                return details;
               /* try {
                    String result = Operations.SendRequest(context.getString(R.string.googlemap_endpoint)+location.getLatitude()+","+location.getLongitude());
                    JSONArray jsonArray = new JSONArray(result);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    Toast.makeText(context,jsonObject.getString("formatted_address"), Toast.LENGTH_SHORT).show();
                } catch (Exception e1) {
                    return details;
                }*/
            }
        return details;
    }

    @Override
    public void onLocationChanged(Location location) {

        if(hasPendingDispatch) {
            Toast.makeText(context, "Upadate recieved ",Toast.LENGTH_SHORT).show();
            String locDetails = "";
            if(Operations.isDeviceConnected(context)){
                locDetails = getLocationDetails(location);
            }

           String message = String.format("%s \n%s,%s \n%s \n%s", context.getString(distressType.getValue()), location.getLongitude(), location.getLatitude(), locDetails, " " );
           sendSMS(context.getString(R.string.phone_number), context.getString(distressType.getValue()));
            System.out.print("Location received: \n");
            System.out.println(message);
            hasPendingDispatch = false;
           GPS.getInstance(context).removeLocationUpadate();
            Toast.makeText(context, "Listener removed. ",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onCancel() {
        Toast.makeText(context,"Dialog canceled.", Toast.LENGTH_SHORT).show();
    }

    enum DistressType {
        Accident(R.string.accident_message),
        Fire(R.string.fire_message),
        Murder(R.string.murder_message),
        Natural_Disaster(R.string.natural_disaster_message),
        Robbery(R.string.robbery_message),
        Suicide(R.string.suicide_message),
        Terror_Attack(R.string.terrror_attack_message);

        private int value;
        DistressType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
