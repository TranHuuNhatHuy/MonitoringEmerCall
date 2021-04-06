package com.huy9515gmail.newemercallmonitoring;

/**
 * Created by Admin on 01-12-17.
 */

public class Ambulance {

    private String number;
    private Double lat, lng;
    private boolean isActive;
    private int times;
    private String id;

    public Ambulance(String number, Double lat, Double lng, boolean isActive, int times, String id) {
        this.number = number;
        this.lat = lat;
        this.lng = lng;
        this.isActive = isActive;
        this.times = times;
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public boolean getStatus() {
        return isActive;
    }

    public int getTimes() {
        return times;
    }

    public String getID() {
        return id;
    }

    public void setStatus(boolean status) {
        this.isActive = status;
    }

    public void setTimes(int times) {
        this.times = times;
    }
}
