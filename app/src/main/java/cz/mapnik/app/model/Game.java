package cz.mapnik.app.model;

/**
 * Created by chaemil on 3.2.16.
 */
public class Game {

    private Type type;
    private Time time;
    private Location location;

    public Game() {
    }

    public Game(Type type, Time time, Location location) {
        this.type = type;
        this.time = time;
        this.location = location;
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

    public Type getType() {
        return type;
    }

    public Time getTime() {
        return time;
    }

    public Location getLocation() {
        return location;
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
