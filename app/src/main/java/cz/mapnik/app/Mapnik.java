package cz.mapnik.app;

import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import cz.mapnik.app.activity.MainActivity;
import cz.mapnik.app.model.Game;
import cz.mapnik.app.model.Player;
import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by chaemil on 2.2.16.
 */
public class Mapnik extends MultiDexApplication {

    private ArrayList<Player> players;
    private Game currentGame;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

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
        currentGame = null;
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(Game game) {
        this.currentGame = game;
    }
}
