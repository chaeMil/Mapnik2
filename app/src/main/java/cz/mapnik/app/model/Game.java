package cz.mapnik.app.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by chaemil on 3.2.16.
 */
public class Game {

    private Type type;
    private Time time;
    private Location location;
    private LatLng startingLatLng;

    public Game() {
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setStartingLatLng(LatLng startingLatLng) {
        this.startingLatLng = startingLatLng;
    }

    public Type getType() {
        return type;
    }

    public Time getTime() {
        return time;
    }

    public Location getLocation() {
        return location;
    }

    public LatLng getStartingLatLng() {
        return startingLatLng;
    }

    public enum Type {
        MAP, ADDRESS
    }

    public enum Time {
        S30, M1, M3, M5
    }

    public enum Location {
        CITY, MONUMENTS, CUSTOM, RANDOM
    }
}
