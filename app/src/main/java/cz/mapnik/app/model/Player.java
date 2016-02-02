package cz.mapnik.app.model;

/**
 * Created by chaemil on 2.2.16.
 */
public class Player {

    private String name;
    private Avatar avatar;
    private int score;

    public Player(String name, Avatar avatar) {
        this.name = name;
        this.avatar = avatar;
        this.score = 0;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public Avatar getAvatar() {
        return avatar;
    }
}
