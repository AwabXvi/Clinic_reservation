package com.bahri.crimnal.clinical.models;

import com.google.firebase.database.Exclude;

import java.util.List;

public class Clinic {
    String name = "";
    String phone = "";
    String info = "";
    String end_time = "";
    String start_time = "";
    List<String> doctors;
    String longitude, latitude;

    public Clinic(String name, String phone, String info, String end_time, String start_time, List<String> doctors, String latitude, String longitude) {
        this.name = name;
        this.phone = phone;
        this.info = info;
        this.end_time = end_time;
        this.start_time = start_time;
        this.doctors = doctors;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Clinic() {

    }

    @Exclude
    public String getName() {
        return name;
    }

    @Exclude
    public String getPhone() {

        return String.valueOf(phone);
    }

    @Exclude
    public String getInfo() {
        return info;
    }

    @Exclude
    public String getEnd_time() {
        return end_time;
    }

    @Exclude
    public String getStart_time() {
        return start_time;
    }


    public void setName(String name) {
        name = name;
    }

    public void setPhone(String phone) {

        phone = phone;
    }

    public void setInfo(String info) {

        info = info;
    }

    public void setEnd_time(String end_time) {
        end_time = end_time;
    }

    public void setStart_time(String start_time) {
        start_time = start_time;
    }

    public List<String> getDoctors() {
        return doctors;
    }

    public void setDoctors() {
        this.doctors = doctors;
    }

    public double getLongitude() {
        return Double.parseDouble(longitude);
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Exclude
    public double getLatitude() {
        return Double.parseDouble(latitude);
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}

