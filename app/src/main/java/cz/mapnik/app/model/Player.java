package cz.mapnik.app.model;

/**
 * Created by chaemil on 2.2.16.
 */
public class Player {

    private String name;
    private String avatar;
    private int score;

    public Player(String name, String avatar) {
        this.name = name;
        this.avatar = avatar;
        this.score = 0;
    }

    public boolean equals(Object other) {
        if (other instanceof Player) {
            if (((Player) other).getName().equals(this.name)
                    && ((Player) other).getAvatar().equals(this.avatar)) {
                return true;
            }
        }
        return false;
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

    public String getAvatar() {
        return avatar;
    }
}
