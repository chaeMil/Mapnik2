package cz.mapnik.app.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

import java.util.ArrayList;

import at.markushi.ui.CircleButton;
import cz.mapnik.app.Mapnik;
import cz.mapnik.app.R;
import cz.mapnik.app.model.Game;
import cz.mapnik.app.model.Guess;
import cz.mapnik.app.model.LocationType;
import cz.mapnik.app.model.Player;
import cz.mapnik.app.model.Type;
import cz.mapnik.app.utils.BitmapUtils;
import cz.mapnik.app.utils.DimensUtils;
import cz.mapnik.app.utils.MapUtils;
import cz.mapnik.app.utils.SmartLog;

/**
 * Created by chaemil on 2.2.16.
 */
public class GuessActivity extends BaseActivity implements OnStreetViewPanoramaReadyCallback,
        View.OnClickListener {

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
    private CircleButton guessButton;
    private CircleButton makeGuessButton;
    private LinearLayout mapWrapper;
    private MapFragment guessMapFragment;
    private GoogleMap guessMap;
    private CircleButton mapCloseButton;
    private RelativeLayout mapBG;
    private LatLng guessedLocation;
    private long timeRemaining;
    private ArrayList<ArrayList> turnGuesses;
    private LatLngBounds gameBoundaries;
    private MapFragment summaryMapFragment;
    private GoogleMap summaryMap;
    private RelativeLayout turnSummaryWrapper;
    private CircleButton summaryConfirmButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess);

        game = ((Mapnik) getApplication()).getCurrentGame();

        if (game != null && game.getGuesses() != null) {

            turnGuesses = game.getTurnGuesses();
            guesses = game.getGuesses();
            players = ((Mapnik) getApplication()).getPlayers();
            maxTurns = MAX_TURNS;

            getUI();

            setupUI();

            nextTurn();
        } else {
            finish();
        }

    }

    private void getUI() {
        streetView = (StreetViewPanoramaFragment) getFragmentManager()
                .findFragmentById(R.id.streetView);
        streetView.getStreetViewPanoramaAsync(this);
        guessMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        guessMap = guessMapFragment.getMap();
        guessMap.setOnMapLoadedCallback(guessMapReadyCallback);
        summaryMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapSummary);
        summaryMap = summaryMapFragment.getMap();
        summaryMap.setOnMapLoadedCallback(summaryMapReadyCallback);
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
        guessButton = (CircleButton) findViewById(R.id.guessButton);
        makeGuessButton = (CircleButton) findViewById(R.id.makeGuessButton);
        mapWrapper = (LinearLayout) findViewById(R.id.mapWrapper);
        mapCloseButton = (CircleButton) findViewById(R.id.mapCloseButton);
        mapBG = (RelativeLayout) findViewById(R.id.mapBG);
        turnSummaryWrapper = (RelativeLayout) findViewById(R.id.turnSummaryWrapper);
        summaryConfirmButton = (CircleButton) findViewById(R.id.summaryConfirmButton);
    }

    private void setupUI() {
        nextPlayerConfirm.setOnClickListener(this);
        nextTurnConfirm.setOnClickListener(this);
        guessButton.setOnClickListener(this);
        mapWrapper.setOnClickListener(this);
        mapCloseButton.setOnClickListener(this);
        makeGuessButton.setOnClickListener(this);
        summaryConfirmButton.setOnClickListener(this);

        switch (game.getType()) {
            case MAP:
                guessButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_maps_map));
                break;
            case ADDRESS:
                guessButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_hardware_keyboard));
                break;
        }

    }

    private void nextTurn() {

        currentPlayer = -1;
        currentTurn += 1;
        turnGuesses.add(currentTurn, new ArrayList());
        if (timer != null) {
            timer.cancel();
        }

        hideWrapperViews();

        nextTurnWrapper.setVisibility(View.VISIBLE);
        if (currentTurn == 0) {
            nextTurnIndicator.setProgress(100 / maxTurns * (currentTurn + 1));
        } else {
            nextTurnIndicator.setProgress(100 / maxTurns * currentTurn);
        }


    }

    private void nextPlayer() {

        currentPlayer += 1;
        timeRemaining = game.getTimeValueInMillis();
        Player player = players.get(currentPlayer);
        if (timer != null) {
            timer.cancel();
        }

        if (players.size() > 1) {
            hideWrapperViews();
            nextPlayerWrapper.setVisibility(View.VISIBLE);
        } else {
            hideWrapperViews();
            currentTurnWrapper.setVisibility(View.VISIBLE);
            prepareTurn();
            startTurn();
        }
        int avatarRes = getResources().getIdentifier(player.getAvatar(), "drawable", getPackageName());
        nextPlayerAvatar.setImageDrawable(getResources().getDrawable(avatarRes));
        nextPlayerNick.setText(player.getName());
        currentPlayerAvatar.setImageDrawable(getResources().getDrawable(avatarRes));
        currentPlayerNick.setText(player.getName());

    }

    private void prepareTurn() {

        timer = new CountDownTimer(game.getTimeValueInMillis(), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int max = game.getTimeValueInMillis();
                int progress = max - (int) millisUntilFinished;
                int timerValue = (int) (100.0 / (double) max * (double) progress);
                timeIndicator.setProgress(timerValue);
                timeRemaining = millisUntilFinished;

                //SmartLog.Log(SmartLog.LogLevel.DEBUG, "tick", max + " | " + progress + " | " + timerValue);
            }

            @Override
            public void onFinish() {

            }
        };

        guessedLocation = null;
        guessMap.clear();
        Player player = players.get(currentPlayer);
        int avatarRes = getResources().getIdentifier(player.getAvatar(), "drawable", getPackageName());

        currentPlayerNick.setText(player.getName());
        currentPlayerAvatar.setImageDrawable(getResources().getDrawable(avatarRes));

    }

    private void showTurnSummary() {

        hideWrapperViews();
        turnSummaryWrapper.setVisibility(View.VISIBLE);

        ArrayList<LatLng> playerGuesses = game.getTurnGuesses().get(currentTurn);
        summaryMap.clear();
        addMapBoundaries();

        for(int g = 0; g < playerGuesses.size(); g++) {
            if (playerGuesses.get(g) != null) {

                int avatarRes = getResources().getIdentifier(players.get(g).getAvatar(), "drawable", getPackageName());
                Bitmap avatar = BitmapUtils.drawableToBitmap(getResources().getDrawable(avatarRes));
                avatar = BitmapUtils.scaleToFitHeight(avatar, (int) DimensUtils.pxFromDp(getApplicationContext(), 40));

                summaryMap.addMarker(new MarkerOptions()
                        .position(playerGuesses.get(g))
                        .snippet(players.get(g).getName())
                        .icon(BitmapDescriptorFactory.fromBitmap(avatar)));
            }
        }

        gameBoundaries = new LatLngBounds(game.getGameLocation().getSouthWestBound(),
                game.getGameLocation().getNorthEastBound());

        summaryMap.moveCamera(CameraUpdateFactory.newLatLngBounds(gameBoundaries, 60));

        summaryMap.addMarker(new MarkerOptions()
                .position(game.getGuesses().get(currentTurn).getLocation())
                .snippet(getString(R.string.correct_location))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

    }


    private void submitGuess() {

        int score = 0;

        switch (game.getType()) {
            case MAP:
                if (guessedLocation != null) {

                    LatLng correctLocation = game.getGuesses().get(currentTurn).getLocation();
                    double guessTime = game.getTimeValueInMillis() - timeRemaining;
                    score = calculateScore(correctLocation, guessedLocation, guessTime, game.getTimeValueInMillis());

                }
                break;
        }

        SmartLog.Log(SmartLog.LogLevel.DEBUG, "score", String.valueOf(score));

        players.get(currentPlayer).addScore(score);
        game.getTurnGuesses().get(currentTurn).add(currentPlayer, guessedLocation);
        panorama.setPosition(new LatLng(0, 0));

        if (currentPlayer + 1 >= players.size()) {
            showTurnSummary();
        } else {
            if (currentPlayer + 1 < players.size()) {
                nextPlayer();
            } else {
                nextTurn();
            }
        }
    }

    private int calculateScore(LatLng correctLocation, LatLng guessedLocation, double guessTime, int turnTime) {

        int resultTimePercent = 100 - ((int) (100.0 / (double) turnTime * guessTime));

        float maxDistance;

        double distance = (double) MapUtils.distance((float) correctLocation.latitude, (float) correctLocation.longitude,
                (float) guessedLocation.latitude, (float) guessedLocation.longitude);

        int resultDistancePercent = 0;

        switch (game.getLocationType()) {
            case LocationType.CITY:
                LatLng northEastBounds = game.getGameLocation().getNorthEastBound();
                LatLng southWestBounds = game.getGameLocation().getSouthWestBound();
                maxDistance = MapUtils.distance((float) northEastBounds.latitude,
                        (float) northEastBounds.longitude, (float) southWestBounds.latitude,
                        (float) southWestBounds.longitude);

                resultDistancePercent = 100 - ((int) (100.0 / maxDistance * distance));
                break;
        }

        return (int) ((double) resultDistancePercent + (double) resultTimePercent) / 2;
    }

    private void startTurn() {
        hideWrapperViews();
        currentTurnWrapper.setVisibility(View.VISIBLE);

        panorama.setPosition(guesses.get(currentTurn).getLocation(), 200);
        panorama.setStreetNamesEnabled(false);

        guessedLocation = null;

        timer.start();
        guessMap.clear();
    }

    private void showMap() {

        mapWrapper.setVisibility(View.VISIBLE);
        mapBG.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.SlideInUp).duration(200).playOn(mapWrapper);
        YoYo.with(Techniques.FadeIn).duration(200).playOn(mapBG);

        if (game.getGameLocation().getNorthEastBound() != null) {
            gameBoundaries = new LatLngBounds(game.getGameLocation().getSouthWestBound(),
                    game.getGameLocation().getNorthEastBound());
        } else {
            gameBoundaries = MapUtils.convertCenterAndRadiusToBounds(game.getGameLocation().getCenter(),
                    game.getRadius());
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (guessedLocation == null) {
                    guessMap.clear();
                    addMapBoundaries();
                    guessMap.moveCamera(CameraUpdateFactory.newLatLngBounds(gameBoundaries, 60));
                }
            }
        }, 250);


    }

    private void hideMap() {

        YoYo.with(Techniques.SlideOutDown).duration(200).playOn(mapWrapper);
        YoYo.with(Techniques.FadeOut).duration(200).playOn(mapBG);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mapWrapper.setVisibility(View.GONE);
                mapBG.setVisibility(View.GONE);
            }
        }, 200);

    }

    private void addMapBoundaries() {

        if (game.getGameLocation().getSouthWestBound() != null && game.getGameLocation().getNorthEastBound() != null) {

            LatLng topLeft = new LatLng(game.getGameLocation().getNorthEastBound().latitude, game.getGameLocation().getSouthWestBound().longitude);
            LatLng topRight = new LatLng(game.getGameLocation().getNorthEastBound().latitude, game.getGameLocation().getNorthEastBound().longitude);
            LatLng bottomLeft = new LatLng(game.getGameLocation().getSouthWestBound().latitude, game.getGameLocation().getSouthWestBound().longitude);
            LatLng bottomRight = new LatLng(game.getGameLocation().getSouthWestBound().latitude, game.getGameLocation().getNorthEastBound().longitude);

            if (guessMap != null) {
                guessMap.addPolyline(new PolylineOptions()
                        .add(topLeft)
                        .add(topRight)
                        .add(bottomRight)
                        .add(bottomLeft)
                        .add(topLeft)
                        .color(getResources().getColor(R.color.bright_green)));
            }

            if (summaryMap != null) {
                summaryMap.addPolyline(new PolylineOptions()
                        .add(topLeft)
                        .add(topRight)
                        .add(bottomRight)
                        .add(bottomLeft)
                        .add(topLeft)
                        .color(getResources().getColor(R.color.bright_green_alpha)));
            }

        } else if (game.getGameLocation().getCenter() != null && game.getRadius() != 0) {

            if (guessMap != null) {
                guessMap.addCircle(new CircleOptions()
                        .center(game.getGameLocation().getCenter())
                        .radius(game.getRadius())
                        .strokeColor(getResources().getColor(R.color.bright_green)));
            }

            if (summaryMap != null)  {
                summaryMap.addCircle(new CircleOptions()
                        .center(game.getGameLocation().getCenter())
                        .radius(game.getRadius())
                        .strokeColor(getResources().getColor(R.color.bright_green_alpha)));
            }

        }

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
                    if (players.size() > 1 ) {
                        prepareTurn();
                    }

                }
                break;
            case R.id.guessButton:
                if (game.getType().equals(Type.MAP)) {
                    showMap();
                }
                break;
            case R.id.mapWrapper:
                hideMap();
                break;
            case R.id.mapCloseButton:
                hideMap();
                break;
            case R.id.makeGuessButton:
                if (guessedLocation != null) {
                    submitGuess();
                }
                break;
            case R.id.summaryConfirmButton:
                if (currentPlayer + 1 < players.size()) {
                    nextPlayer();
                } else {
                    nextTurn();
                }
                break;
        }
    }


    GoogleMap.OnMapLoadedCallback guessMapReadyCallback = new GoogleMap.OnMapLoadedCallback() {
        @Override
        public void onMapLoaded() {
            guessMap.getUiSettings().setAllGesturesEnabled(false);
            guessMap.getUiSettings().setZoomGesturesEnabled(true);
            guessMap.getUiSettings().setScrollGesturesEnabled(true);

            guessMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {

                    guessedLocation = latLng;

                    if (game.getRadius() != 0) {

                        if (MapUtils.distance((float) guessedLocation.latitude,
                                (float)guessedLocation.longitude,
                                (float) game.getGameLocation().getCenter().latitude,
                                (float) game.getGameLocation().getCenter().longitude) < game.getRadius()) {

                            guessMap.clear();

                            guessMap.addMarker(new MarkerOptions().position(guessedLocation));

                            addMapBoundaries();

                            showMakeGuessButton();

                        }

                    } else if (gameBoundaries.contains(guessedLocation)) {

                        guessMap.clear();

                        guessMap.addMarker(new MarkerOptions().position(guessedLocation));

                        addMapBoundaries();

                        showMakeGuessButton();
                    }
                }
            });
        }
    };

    GoogleMap.OnMapLoadedCallback summaryMapReadyCallback = new GoogleMap.OnMapLoadedCallback() {
        @Override
        public void onMapLoaded() {

            guessMap.getUiSettings().setAllGesturesEnabled(false);
            guessMap.getUiSettings().setZoomGesturesEnabled(true);
            guessMap.getUiSettings().setScrollGesturesEnabled(true);

        }
    };


    private void showMakeGuessButton() {
        if (makeGuessButton.getVisibility() != View.VISIBLE) {
            makeGuessButton.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.BounceInUp).duration(200).playOn(makeGuessButton);
        }
    }

    private void hideWrapperViews() {
        nextPlayerWrapper.setVisibility(View.GONE);
        nextTurnWrapper.setVisibility(View.GONE);
        currentTurnWrapper.setVisibility(View.GONE);
        mapWrapper.setVisibility(View.GONE);
        mapBG.setVisibility(View.GONE);
        turnSummaryWrapper.setVisibility(View.GONE);
    }


}
