package cz.mapnik.app.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by chaemil on 3.2.16.
 */
public class GameLocation {

    private String name;
    private LatLng center;
    private LatLng northEastBound;
    private LatLng southWestBound;

    public GameLocation(String name, LatLng center, LatLng northEastBound, LatLng southWestBound) {
        this.name = name;
        this.center = center;
        this.northEastBound = northEastBound;
        this.southWestBound = southWestBound;
    }

    public GameLocation(String name, LatLng center) {
        this.name = name;
        this.center = center;
    }

    public String getName() {
        return name;
    }

    public LatLng getCenter() {
        return center;
    }

    public LatLng getNorthEastBound() {
        return northEastBound;
    }

    public LatLng getSouthWestBound() {
        return southWestBound;
    }
}
