package cz.mapnik.app.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;
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

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Game game = ((Mapnik) getApplication()).getCurrentGame();

        getUI();

        prepareGame = new PrepareGameAsync(game, this);
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

        if (guesses.size() < PrepareGameAsync.MIN_VALID_GUESSES) {
            createRetryDialog();
        } else {
            ((Mapnik) getApplication()).getCurrentGame().setGuesses(guesses);

            Intent guessActivity = new Intent(this, GuessActivity.class);
            startActivity(guessActivity);
            finish();
        }
    }

    private void createRetryDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setMessage(getString(R.string.retry_game_preparation));
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent prepareGame = new Intent(PrepareGameActivity.this, PrepareGameActivity.class);
                        startActivity(prepareGame);
                        finish();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void setMaxPreparationSteps(int i) {
        this.maxPreparationSteps = i;
    }

    @Override
    public void increaseCurrentPreparationStep() {

        double progress = (double) currentPreparationStep / (double) maxPreparationSteps * 100.0;

        SmartLog.Log(SmartLog.LogLevel.DEBUG, "progress", String.valueOf(progress));

        loading.setProgress(100 - (int) progress);
    }
}
