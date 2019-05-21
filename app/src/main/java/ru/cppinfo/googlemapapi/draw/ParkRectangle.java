package ru.cppinfo.googlemapapi.draw;


import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

public class ParkRectangle {
    private double placeLength = 0.000015;
    private double placeWidth = 0.000005;

    private LatLng topLeft;
    private LatLng topRight;
    private LatLng bottomRight;
    private LatLng bottomLeft;
    private LatLng center;

    public ParkRectangle(LatLng center) {
        this.center = center;
        calculate();
        rotate(30);
    }

    private void calculate(){
        LatLng left = new LatLng(center.latitude - placeWidth, center.longitude);
        LatLng right = new LatLng(center.latitude + placeWidth, center.longitude);

        topLeft = new LatLng(left.latitude, left.longitude + placeLength);
        bottomLeft = new LatLng(left.latitude, left.longitude - placeLength);

        topRight = new LatLng(right.latitude, right.longitude + placeLength);
        bottomRight = new LatLng(right.latitude, right.longitude - placeLength);
    }

    private void rotate(double angle){
        angle = angle*Math.PI/180;
        topLeft = rotateCoordinate(topLeft, angle);
        topRight = rotateCoordinate(topRight, angle);
        bottomRight = rotateCoordinate(bottomRight, angle);
        bottomLeft = rotateCoordinate(bottomLeft, angle);
    }

    private LatLng rotateCoordinate(LatLng coordinate, double angle){
        double lat = coordinate.latitude;
        double lng = coordinate.longitude;
        double centerLat = center.latitude;
        double centerLng = center.longitude;

        /*LatLng rotatedCoordinate = new LatLng(
                lat * Math.cos(angle) - lng * Math.sin(angle),
                lat * Math.sin(angle) + lng * Math.cos(angle));*/
        DecimalFormat format = new DecimalFormat("##.000000");
        double newLat = centerLat + (lat-centerLat)*Math.cos(angle) - (lng - centerLng)*Math.sin(angle);
        double newLng = centerLng + (lng -centerLng)*Math.cos(angle) + (lat - centerLat)*Math.sin(angle);
        LatLng rotatedCoordinate = new LatLng(
                Double.valueOf(format.format(newLat).replace(",",".")),
                Double.valueOf(format.format(newLng).replace(",",".")));
        System.out.println("");
        return rotatedCoordinate;
    }

    public ArrayList<LatLng> getCoordinates(){
        ArrayList<LatLng> coordinates = new ArrayList<>();
        coordinates.add(topLeft);
        coordinates.add(topRight);
        coordinates.add(bottomRight);
        coordinates.add(bottomLeft);

        return coordinates;
    }
}
