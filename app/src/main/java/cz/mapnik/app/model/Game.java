package cz.mapnik.app.model;

/**
 * Created by chaemil on 3.2.16.
 */
public class Game {

    private Type type;
    private Time time;
    private LocationType locationType;
    private GameLocation gameLocation;

    public Game() {
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    public void setGameLocation(GameLocation gameLocation) {
        this.gameLocation = gameLocation;
    }

    public Type getType() {
        return type;
    }

    public Time getTime() {
        return time;
    }

    public LocationType getLocationType() {
        return locationType;
    }

    public GameLocation getGameLocation() {
        return gameLocation;
    }

    public enum Type {
        MAP, ADDRESS
    }

    public enum Time {
        S30, M1, M3, M5
    }

    public enum LocationType {
        CITY, MONUMENTS, CUSTOM, RANDOM
    }
}
