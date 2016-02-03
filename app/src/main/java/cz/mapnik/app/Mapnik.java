package cz.mapnik.app;

import android.app.Application;

import java.util.ArrayList;

import cz.mapnik.app.activity.MainActivity;
import cz.mapnik.app.model.Player;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by chaemil on 2.2.16.
 */
public class Mapnik extends Application {

    private ArrayList<Player> players;

    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/RobotoSlab-Regular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        players = new ArrayList<>();
    }

    public boolean addPlayer(Player player, MainActivity caller) {
        if (!players.contains(player)) {
            players.add(player);
            caller.notifyPlayersChanged();
            return true;
        } else {
            return false;
        }

    }

    public void removePlayer(Player player, MainActivity caller) {
        players.remove(player);
        caller.notifyPlayersChanged();
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void resetGame() {
        players.clear();
    }
}
