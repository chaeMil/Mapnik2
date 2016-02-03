package cz.mapnik.app.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by chaemil on 4.2.16.
 */
public class Guess {
    private String name;
    private LatLng location;
    private String fakeName1;
    private String fakeName2;

    public Guess(String name, LatLng location, String fakeName1, String fakeName2) {
        this.name = name;
        this.location = location;
        this.fakeName1 = fakeName1;
        this.fakeName2 = fakeName2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getFakeName1() {
        return fakeName1;
    }

    public void setFakeName1(String fakeName1) {
        this.fakeName1 = fakeName1;
    }

    public String getFakeName2() {
        return fakeName2;
    }

    public void setFakeName2(String fakeName2) {
        this.fakeName2 = fakeName2;
    }
}
