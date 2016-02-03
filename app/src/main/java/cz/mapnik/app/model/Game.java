package cz.mapnik.app.model;

/**
 * Created by chaemil on 3.2.16.
 */
public class Game {

    private Type type;
    private Time time;
    private int locationType;
    private GameLocation gameLocation;

    public Game() {
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public void setLocationType(int locationType) {
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

    public int getLocationType() {
        return locationType;
    }

    public GameLocation getGameLocation() {
        return gameLocation;
    }

}
