package cz.mapnik.app.service;

import android.os.AsyncTask;

import java.util.ArrayList;

import cz.mapnik.app.activity.BaseActivity;
import cz.mapnik.app.model.Game;
import cz.mapnik.app.model.Player;

/**
 * Created by chaemil on 3.2.16.
 */
public class PrepareGameAsync extends AsyncTask {

    private Game game;
    private ArrayList<Player> players;
    private BaseActivity caller;

    public PrepareGameAsync(Game game, ArrayList<Player> players, BaseActivity caller) {
        this.game = game;
        this.players = players;
        this.caller = caller;
    }

    @Override
    protected Object doInBackground(Object[] params) {



        return null;
    }
}
