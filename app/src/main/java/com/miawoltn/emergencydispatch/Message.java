package com.miawoltn.emergencydispatch;

/**
 * Created by Muhammad Amin on 2/24/2017.
 */

public class Message {


    private String distressType;
    private double longitude;
    private double latitude;
    private String locationDetails;
    private String name;

    public Message(String distressType, double longitude, double latitude, String locationDetails, String name) {
        this.distressType = distressType;
        this.longitude = longitude;
        this.latitude = latitude;
        this.locationDetails = locationDetails;
        this.name = name;
    }

    public String getDistressType() {
        return distressType;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return  latitude;
    }

    public String getLocationDetails() {
        return locationDetails;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("%s \n%s,%s \n%s \n%s", distressType, longitude, latitude, locationDetails, name);
    }
}
