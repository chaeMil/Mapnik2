package cz.mapnik.app.service;

import android.content.Context;
import android.location.Address;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import cz.mapnik.app.activity.BaseActivity;
import cz.mapnik.app.model.Game;
import cz.mapnik.app.model.Guess;
import cz.mapnik.app.model.LocationType;
import cz.mapnik.app.model.Player;
import cz.mapnik.app.utils.MapnikGeocoder;
import cz.mapnik.app.utils.MathUtils;
import cz.mapnik.app.utils.SmartLog;

/**
 * Created by chaemil on 3.2.16.
 */
public class PrepareGameAsync extends AsyncTask{

    private static final int DEFAULT_GUESS_RADIUS = 800;
    private Game game;
    private BaseActivity caller;
    ArrayList<Guess> guesses = new ArrayList<>();
    private int cycles = 0;
    private boolean boundsAvailable = false;

    private int ROUNDS = 5;
    private int TRIES = 5;
    private double MIN_LAT = -90;
    private double MAX_LAT = 90;
    private double MIN_LNG = -180;
    private double MAX_LNG = 180;

    public PrepareGameAsync(Game game, BaseActivity caller) {
        this.game = game;
        this.caller = caller;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        caller.setMaxPreparationSteps(ROUNDS * TRIES);
    }

    @Override
    protected Object doInBackground(Object[] params) {

        double minLat = MIN_LAT;
        double maxLat = MAX_LAT;
        double minLng = MIN_LNG;
        double maxLng = MAX_LNG;

        switch (game.getLocationType()) {
            case LocationType.CITY:

                if (game.getGameLocation().getNorthEastBound() != null) {
                    boundsAvailable = true;
                    minLat = game.getGameLocation().getNorthEastBound().latitude;
                    maxLat = game.getGameLocation().getSouthWestBound().latitude;
                    minLng = game.getGameLocation().getNorthEastBound().longitude;
                    maxLng = game.getGameLocation().getSouthWestBound().longitude;
                }
                break;
        }


        for(int r = 0; r < ROUNDS * TRIES; r++) {

            double resultLat;
            double resultLng;
            if (boundsAvailable) {
                resultLat = MathUtils.randomInRange(minLat, maxLat);
                resultLng = MathUtils.randomInRange(minLng, maxLng);
            } else {
                LatLng noBoundsLocation = MapnikGeocoder.getRandomNearbyLocation(game.getGameLocation().getCenter().latitude,
                        game.getGameLocation().getCenter().longitude,
                        3000);
                resultLat = noBoundsLocation.latitude;
                resultLng = noBoundsLocation.longitude;
            }

            try {
                Thread.sleep(1000); //1000 milliseconds is one second. To not spam Google APIs
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            checkForStreetView(caller, boundsAvailable, resultLat, resultLng, minLat, maxLat, minLng, maxLng, DEFAULT_GUESS_RADIUS);

        }

        return null;
    }


    private void checkForStreetView(final Context context, final boolean boundsAvailable,
                                    final double lat, final double lng,
                                    final double minLat, final double maxLat, final double minLng, final double maxLng,
                                    final int radius) {

        final String errorLink = "http://maps.googleapis.com/maps/api/streetview?size=400x400&location=34.834806,-41.314475";
        final String baseLink = "http://maps.googleapis.com/maps/api/streetview?size=400x400&sensor=true&location=";

        Ion.with(caller).load(errorLink).asString().setCallback(new FutureCallback<String>() {


            @Override
            public void onCompleted(Exception e, final String errorString) {


                Ion.with(context).load(baseLink + lat + "," + lng).asString().setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (result != null && errorString != null) {

                            SmartLog.Log(SmartLog.LogLevel.DEBUG, "link", baseLink + lat + "," + lng);

                            if (errorString.equals(result)) {
                                SmartLog.Log(SmartLog.LogLevel.DEBUG, "available", "false");

                            } else {
                                SmartLog.Log(SmartLog.LogLevel.DEBUG, "available", "true");

                                CreateGuessAsync createGuessAsync;

                                cycles += 1;

                                caller.increaseCurrentPreparationStep();

                                boolean lastOne = false;

                                if (cycles == ROUNDS * TRIES) {
                                    lastOne = true;
                                }

                                if (boundsAvailable) {
                                    createGuessAsync = new CreateGuessAsync(PrepareGameAsync.this,
                                            context, lat, lng, minLat, maxLat, minLng, maxLng, lastOne);
                                } else {
                                    createGuessAsync = new CreateGuessAsync(PrepareGameAsync.this,
                                            context, lat, lng, radius, lastOne);
                                }

                                createGuessAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


                            }
                        }
                    }
                });


            }
        });

    }

    public void addGuess(Guess guess) {
        guesses.add(guess);
    }

    public void preparationFinished() {
        if (cycles == ROUNDS * TRIES) {
            caller.gamePreparationFinished(guesses);
        }
    }
}
