package cz.mapnik.app.activity;

import android.os.AsyncTask;
import android.os.Bundle;

import java.util.ArrayList;

import cz.mapnik.app.Mapnik;
import cz.mapnik.app.R;
import cz.mapnik.app.model.Game;
import cz.mapnik.app.model.Guess;
import cz.mapnik.app.model.Player;
import cz.mapnik.app.service.PrepareGameAsync;

/**
 * Created by chaemil on 3.2.16.
 */
public class PrepareGameActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_game);

        Game game = ((Mapnik) getApplication()).getCurrentGame();
        ArrayList<Player> players = ((Mapnik) getApplication()).getPlayers();

        PrepareGameAsync prepareGame = new PrepareGameAsync(game, players, this);
        prepareGame.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void gamePreparationFinished(ArrayList<Guess> guesses) {
        super.gamePreparationFinished(guesses);
    }
}
