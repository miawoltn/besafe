package com.miawoltn.emergencydispatch.util;

import java.io.Serializable;

/**
 * Created by Muhammad Amin on 3/7/2017.
 */

public class UserData {
    private String name;
    private String number;
    private String address;

    public UserData(String name, String number, String address) {
        this.name = name;
        this.number = number;
        this.address = address;
    }

    public String getName() {
        return  name;
    }

    public String getNumber() {
        return number;
    }

    public String getAddress() {
        return address;
    }
}
