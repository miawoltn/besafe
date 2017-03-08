package com.miawoltn.emergencydispatch.core;

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
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

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
import com.miawoltn.emergencydispatch.R;
import com.miawoltn.emergencydispatch.fragment.SettingsFragment;
import com.miawoltn.emergencydispatch.util.Message;
import com.miawoltn.emergencydispatch.util.Operations;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;


/**
 * Created by Muhammad Amin on 2/18/2017.
 */

public class SOSDispatcher implements LocationListener, LocationSettingsDialogListener, TrackingDialogListener {

    final String SENT = "SMS_SENT";
    final String DELIVERED = "SMS_DELIVERED";
    public final static String SEND_FAIL_SAFE = "send_fail_safe";
    public final static String TRACKING_ENABLED = "tracking_enabled";
    public final static String TRACKING_DISABLED = "tracking_disabled";
    public final static String FAIL_SAFE_DISABLED = "fail_safe_disabled";
    public final static String FAIL_SAFE_ENABLED = "fail_safe_disabled";
    static boolean hasPendingDispatch = false;
    static boolean tracking = false;
    long lastTrackTime = System.currentTimeMillis();
    DistressType distressType = null;
    boolean isFalsePositive = true;
    static boolean endpointStatus = false;
    Context context;
    Contact contact;
    Logger logger;
    GPS gps;


    /**
     *
     *
     * @param context
     */
    public SOSDispatcher(Context context) {
        this.context = context;
        gps = GPS.getInstance(context);
        contact = Contact.getInstance(context);
        logger = Logger.getInstance(context);
    }


    /**
     *
     * @param distressType
     * @param track
     */
    public void Dispatch(DistressType distressType, boolean track) {
        if (track) {
            Track();
        }
        Dispatch(distressType);
    }


    /**
     *
     * @param distressType
     */
    public void Dispatch(DistressType distressType) {
        hasPendingDispatch = true;
        this.distressType = distressType;
        listenToBroadcast();
        startTracking();
    }

    /**
     *
     */
    public void sendFalsePositive() {
            sendSMS(contact.getNumbers(), context.getString(R.string.fail_safe_ok_message));
            logger.updateMessage();
           // sentToEndPoint(context.getString(R.string.googlemap_endpoint), context.getString(R.string.fail_safe_ok_message));
            //logger.log(,contact.getNumberIds());
            Operations.createNofication(context,context.getString(R.string.fail_safe_notification_title), context.getString(R.string.fail_safe_notification_ok_message)  );

    }

    /**
     * Enables real time tracking.
     */
    private void Track() {
        tracking = true;
       // gps.requestTrackingLocationUpdate();
    }

    /**
     *  Attaches listener to gps device.
     */
    public void startTracking() {
        gps.setLocationListener(this);
        gps.requestLocationUpadate(this);
    }

    /**
     * Removes location listening from gps.
     */
    public void stopTracking() {
        gps.removeLocationUpadate();
    }

    /**
     * Sends SOS to the list of number numbers specified.
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
                        if(SettingsFragment.isSMSNotificationEnabled())
                        Operations.createNofication(context,context.getString(R.string.sms_notification_title), context.getString(R.string.result_ok));
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        if(SettingsFragment.isSMSNotificationEnabled())
                        Operations.createNofication(context,context.getString(R.string.sms_notification_title),  context.getString(R.string.generic_failure));
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        if(SettingsFragment.isSMSNotificationEnabled())
                        Operations.createNofication(context,context.getString(R.string.sms_notification_title),  context.getString(R.string.no_service));
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        if(SettingsFragment.isSMSNotificationEnabled())
                        Operations.createNofication(context,context.getString(R.string.sms_notification_title),  context.getString(R.string.null_pdu));
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        if(SettingsFragment.isSMSNotificationEnabled())
                        Operations.createNofication(context,context.getString(R.string.sms_notification_title),  context.getString(R.string.radio_off));
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
                        if(SettingsFragment.isSMSNotificationEnabled())
                        Operations.createNofication(context,context.getString(R.string.sms_notification_title),  context.getString(R.string.sms_delivered));
                        break;
                    case AppCompatActivity.RESULT_CANCELED:
                        if(SettingsFragment.isSMSNotificationEnabled())
                        Operations.createNofication(context,context.getString(R.string.sms_notification_title),  context.getString(R.string.sms_not_delivered));
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
     * @param SOS
     * @return
     */
    public void sentToEndPoint(Message SOS) {
        if(Operations.isDeviceConnected(context)) {
            RequestQueue queue = Volley.newRequestQueue(context);
            String url = Operations.generateURL("192.168.137.1/message");
            String userId = "0";
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("message", SOS.getDistressType());
                jsonObject.put("longitude", SOS.getLongitude());
                jsonObject.put("latitude", SOS.getLatitude());
                jsonObject.put("location", SOS.getLocationDetails());
                jsonObject.put("Failsafe", 0);
                for(String file : context.fileList()) {
                    if(file == Operations.USER_ID){
                        userId =  Operations.readFromFile(context, Operations.USER_ID);
                        break;
                    }
                }
                jsonObject.put("userId", userId);
            }
            catch (Exception e) {
                Log.e("Json object", e.getMessage());
                Toast.makeText(context,"An error occurred. Please initiate the process again.", Toast.LENGTH_SHORT).show();
                return;
            }

            final String requestBody = jsonObject.toString();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            endpointStatus = true;
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


    public String getLocationDetails(Location location) {
            String details = "";
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
            }
        return details;
    }

    public void activateFailSafe() {

    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.print("Location received: \n");
        if(hasPendingDispatch) {
            Toast.makeText(context, "Upadate recieved ",Toast.LENGTH_SHORT).show();
            String locDetails = "";
            if(Operations.isDeviceConnected(context)){
                locDetails = getLocationDetails(location);
            }

            //Prepare message and send to endpints: SMS, Control Unit and Log.
            final Message message = new Message(context.getString(distressType.getValue()), location.getLongitude(), location.getLatitude(), locDetails, " " );
            sendSMS(contact.getNumbers(), message.toString()); // SMS

            //control unit
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sentToEndPoint(message);
                    if(SettingsFragment.isControUnitNotificationEnabled()) {
                        if(endpointStatus)
                            Operations.createNofication(context,context.getString(R.string.control_unit_notification_title), context.getString(R.string.control_unit_dispatch_ok_message));
                        else
                            Operations.createNofication(context,context.getString(R.string.control_unit_notification_title), context.getString(R.string.control_unit_dispatch_fail_message));
                    }
                }
            }).start();

            //log
            logger.log(message,contact.getNumberIds());

            hasPendingDispatch = false;
            context.sendBroadcast(new Intent(FAIL_SAFE_ENABLED));
            Log.d("pending dispatch","No pending dispatch");
            if(Operations.isDeviceConnected(context)) {
                Operations.showTrackingAlert(context,context.getString(R.string.tracking_dialog_title), context.getString(R.string.tracking_dialog_messge), context.getString(R.string.yes),context.getString(R.string.cancel), this);
                lastTrackTime = System.currentTimeMillis();
                //return;
            }
            else {
                stopTracking();
                Log.i("Dispatch", "Listener removed.");
            }

        }

        if(tracking) {
            Log.i("Location:","tracking");
            if((System.currentTimeMillis() - lastTrackTime) > 5000) {
                Tracker tracker = new Tracker(context);
                tracker.execute(location);
                Log.i("Tracking", String.valueOf(location.getLongitude())+", "+String.valueOf(location.getLongitude())+" "+
                        System.currentTimeMillis());
                lastTrackTime = System.currentTimeMillis();
            }

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
    }

    @Override
    public void onOk() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(intent);
    }

    @Override
    public void onTrackingAccept() {
        Track();
        context.sendBroadcast(new Intent(TRACKING_ENABLED));
    }

    @Override
    public void onTrackingIgnore() {
        stopTracking();
    }

    public void listenToBroadcast() {
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                sendFalsePositive();
            }
        }, new IntentFilter(SEND_FAIL_SAFE));

        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Message message = new Message(context.getString(distressType.getValue()), 0.0, 0.0, " ", " " );
                List<String> numbers = contact.getNumbers();
                Log.i("Checking number:",numbers.toString());
                sendSMS(numbers, message.toString());
                logger.log(message,contact.getNumberIds());
            }
        }, new IntentFilter(context.getString(R.string.location_request_dialog_timeout)));

        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                tracking = true;
            }
        }, new IntentFilter(TRACKING_ENABLED));

        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                tracking = false;
                stopTracking();
            }
        }, new IntentFilter(TRACKING_DISABLED));

    }

    public enum DistressType {
        Accident(R.string.accident_message),
        Fire(R.string.fire_message),
        Murder(R.string.murder_message),
        Natural_Disaster(R.string.natural_disaster_message),
        Robbery(R.string.robbery_message),
        Suicide(R.string.suicide_message),
        Terror_Attack(R.string.terrror_attack_message),
        Emergency_Others(R.string.emergency_others);

        private int value;
        DistressType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
