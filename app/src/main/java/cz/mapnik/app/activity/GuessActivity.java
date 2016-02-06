package cz.mapnik.app.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

import java.util.ArrayList;

import at.markushi.ui.CircleButton;
import cz.mapnik.app.Mapnik;
import cz.mapnik.app.R;
import cz.mapnik.app.model.Game;
import cz.mapnik.app.model.Guess;
import cz.mapnik.app.model.Player;
import cz.mapnik.app.utils.SmartLog;

/**
 * Created by chaemil on 2.2.16.
 */
public class GuessActivity extends BaseActivity implements OnStreetViewPanoramaReadyCallback, View.OnClickListener {

    private static final int MAX_TURNS = 5;

    private ArrayList<Guess> guesses;
    private Game game;
    private StreetViewPanoramaFragment streetView;
    private ArrayList<Player> players;

    private CountDownTimer timer;
    private int currentTurn = -1;
    private int currentPlayer = -1;

    private int maxTurns;
    private RelativeLayout currentTurnWrapper;
    private RelativeLayout nextPlayerWrapper;
    private ImageView nextPlayerAvatar;
    private TextView nextPlayerNick;
    private CircleButton nextPlayerConfirm;
    private StreetViewPanorama panorama;
    private RelativeLayout nextTurnWrapper;
    private CircularFillableLoaders nextTurnIndicator;
    private CircleButton nextTurnConfirm;
    private ImageView currentPlayerAvatar;
    private TextView currentPlayerNick;
    private CircularFillableLoaders timeIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess);

        game = ((Mapnik) getApplication()).getCurrentGame();
        guesses = game.getGuesses();
        players = ((Mapnik) getApplication()).getPlayers();
        maxTurns = MAX_TURNS;

        getUI();

        setupUI();

        nextTurn();

    }

    private void getUI() {
        streetView = (StreetViewPanoramaFragment) getFragmentManager()
                .findFragmentById(R.id.streetView);
        streetView.getStreetViewPanoramaAsync(this);
        currentTurnWrapper = (RelativeLayout) findViewById(R.id.currentTurnWrapper);
        nextPlayerWrapper = (RelativeLayout) findViewById(R.id.nextPlayerWrapper);
        nextPlayerAvatar = (ImageView) findViewById(R.id.nextPlayerAvatar);
        nextPlayerNick = (TextView) findViewById(R.id.nextPlayerNick);
        nextPlayerConfirm = (CircleButton) findViewById(R.id.nextPlayerConfirm);
        nextTurnWrapper = (RelativeLayout) findViewById(R.id.nextTurnWrapper);
        nextTurnIndicator = (CircularFillableLoaders) findViewById(R.id.nextTurnIndicator);
        nextTurnConfirm = (CircleButton) findViewById(R.id.nextTurnConfirm);
        currentPlayerAvatar = (ImageView) findViewById(R.id.currentPlayerAvatar);
        currentPlayerNick = (TextView) findViewById(R.id.currentPlayerNick);
        timeIndicator = (CircularFillableLoaders) findViewById(R.id.timeIndicator);
    }

    private void setupUI() {
        nextPlayerConfirm.setOnClickListener(this);
        nextTurnConfirm.setOnClickListener(this);
    }

    private void nextTurn() {

        currentPlayer = -1;
        currentTurn += 1;

        hideWrapperViews();

        nextTurnWrapper.setVisibility(View.VISIBLE);
        nextTurnIndicator.setProgress((int) (100 / maxTurns * (currentTurn - 0.5)));

    }

    private void nextPlayer() {

        currentPlayer += 1;
        Player player = players.get(currentPlayer);

        hideWrapperViews();
        nextPlayerWrapper.setVisibility(View.VISIBLE);
        int avatarRes = getResources().getIdentifier(player.getAvatar(), "drawable", getPackageName());
        nextPlayerAvatar.setImageDrawable(getResources().getDrawable(avatarRes));
        nextPlayerNick.setText(player.getName());

    }

    private void prepareTurn() {

        timer = new CountDownTimer(game.getTimeValueInMillis(), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int max = game.getTimeValueInMillis();
                int progress = max - (int) millisUntilFinished;
                int timerValue = (int) (100.0 / (double) max * (double) progress);
                timeIndicator.setProgress(timerValue);
                SmartLog.Log(SmartLog.LogLevel.DEBUG, "tick", max + " | " + progress + " | " + timerValue);
            }

            @Override
            public void onFinish() {

            }
        };

        Player player = players.get(currentPlayer);
        int avatarRes = getResources().getIdentifier(player.getAvatar(), "drawable", getPackageName());

        currentPlayerNick.setText(player.getName());
        currentPlayerAvatar.setImageDrawable(getResources().getDrawable(avatarRes));

    }

    private void startTurn() {
        hideWrapperViews();
        currentTurnWrapper.setVisibility(View.VISIBLE);

        panorama.setPosition(guesses.get(currentTurn).getLocation(), 200);
        panorama.setStreetNamesEnabled(false);

        timer.start();
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
        SmartLog.Log(SmartLog.LogLevel.DEBUG, "location", guesses.get(0).getLocation().toString());

        this.panorama = panorama;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nextPlayerConfirm:
                if (currentPlayer < players.size()) {

                    startTurn();

                }
                break;
            case R.id.nextTurnConfirm:
                if (currentTurn <= maxTurns) {

                    nextPlayer();
                    prepareTurn();

                }
                break;
        }
    }

    private void hideWrapperViews() {
        nextPlayerWrapper.setVisibility(View.GONE);
        nextTurnWrapper.setVisibility(View.GONE);
        currentTurnWrapper.setVisibility(View.GONE);
    }
}
