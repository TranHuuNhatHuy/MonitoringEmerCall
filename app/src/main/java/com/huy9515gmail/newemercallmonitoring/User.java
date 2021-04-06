package com.huy9515gmail.newemercallmonitoring;

import java.io.Serializable;

/**
 * Created by Admin on 30-11-17.
 */
@SuppressWarnings("serial")
public class User implements Serializable {

    private String name, dob, gender, address, bhyt, cmnd, id;

    public User(String name, String dob, String gender, String bhyt, String address, String cmnd, String id) {
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.bhyt = bhyt;
        this.address = address;
        this.cmnd = cmnd;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDOB() {
        return dob;
    }

    public String getGender() {
        return gender;
    }

    public String getAddress() {
        return address;
    }

    public String getBHYT() {
        return bhyt;
    }

    public String getCMND() {
        return cmnd;
    }

    public String getID() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDOB(String dob) {
        this.dob = dob;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setBHYT(String bhyt) {
        this.bhyt = bhyt;
    }

    public void setCMND(String cmnd) {
        this.cmnd = cmnd;
    }

    public void setID(String id) {
        this.id = id;
    }

}
