package cz.mapnik.app.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

import java.util.ArrayList;

import cz.mapnik.app.Mapnik;
import cz.mapnik.app.R;
import cz.mapnik.app.model.Game;
import cz.mapnik.app.model.Guess;
import cz.mapnik.app.model.Player;
import cz.mapnik.app.service.PrepareGameAsync;
import cz.mapnik.app.utils.SmartLog;

/**
 * Created by chaemil on 3.2.16.
 */
public class PrepareGameActivity extends BaseActivity {

    private int maxPreparationSteps;
    private int currentPreparationStep;
    private CircularFillableLoaders loading;
    private PrepareGameAsync prepareGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare_game);

        Game game = ((Mapnik) getApplication()).getCurrentGame();
        ArrayList<Player> players = ((Mapnik) getApplication()).getPlayers();

        getUI();

        prepareGame = new PrepareGameAsync(game, players, this);
        prepareGame.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        prepareGame.cancel(true);
    }

    private void getUI() {
        loading = (CircularFillableLoaders) findViewById(R.id.loading);
    }

    @Override
    public void gamePreparationFinished(ArrayList<Guess> guesses) {
        super.gamePreparationFinished(guesses);

        Toast.makeText(this, "guesses found: " +guesses.size(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void setMaxPreparationSteps(int i) {
        this.maxPreparationSteps = i;
    }

    @Override
    public void increaseCurrentPreparationStep() {
        super.increaseCurrentPreparationStep();
        this.currentPreparationStep += 1;

        double progress = (double) currentPreparationStep / (double) maxPreparationSteps * 100.0;

        SmartLog.Log(SmartLog.LogLevel.DEBUG, "progress", String.valueOf(progress));

        loading.setProgress(100 - (int) progress);
    }
}
