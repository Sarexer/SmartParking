package ru.cppinfo.googlemapapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import ru.cppinfo.googlemapapi.ui.adapter.RowType;

/**
 * Created by shaka on 22.05.2019.
 */
public class Parking implements RowType {
    @SerializedName("latitude")
    @Expose
    private double latitude;
    @SerializedName("longitude")
    @Expose
    private double longitude;
    @SerializedName("pid")
    @Expose
    private int pid;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("parkingPlaces")
    @Expose
    private ArrayList<ParkingPlace> parkingPlaces;

    private int number;

    private int peoples;

    private int distance;


    public Parking(double latitude, double longitude, int pid, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.pid = pid;
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ArrayList<ParkingPlace> getParkingPlaces() {
        return parkingPlaces;
    }

    public void setParkingPlaces(ArrayList<ParkingPlace> parkingPlaces) {
        this.parkingPlaces = parkingPlaces;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getPeoples() {
        return peoples;
    }

    public void setPeoples(int peoples) {
        this.peoples = peoples;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
