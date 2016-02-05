package cz.mapnik.app.model;

import java.util.ArrayList;

/**
 * Created by chaemil on 3.2.16.
 */
public class Game {

    private Type type;
    private Time time;
    private int locationType;
    private int radius;
    private GameLocation gameLocation;
    private ArrayList<Guess> guesses;

    public Game() {
    }

    public ArrayList<Guess> getGuesses() {
        return guesses;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setGuesses(ArrayList<Guess> guesses) {
        this.guesses = guesses;
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
