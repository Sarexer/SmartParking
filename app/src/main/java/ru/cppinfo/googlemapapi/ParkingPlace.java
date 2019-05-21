package ru.cppinfo.googlemapapi;

/**
 * Created by shaka on 18.05.2019.
 */
public class ParkingPlace implements RowType{
    private double latitude;
    private double longitude;
    private boolean status;
    private String street;
    private int people;
    private int distance;

    public ParkingPlace(double latitude, double longitude, boolean status) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getPeople() {
        return people;
    }

    public void setPeople(int people) {
        this.people = people;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
