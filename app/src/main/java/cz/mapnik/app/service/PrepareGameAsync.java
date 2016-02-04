package cz.mapnik.app.service;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

import cz.mapnik.app.activity.BaseActivity;
import cz.mapnik.app.model.Game;
import cz.mapnik.app.model.Guess;
import cz.mapnik.app.model.LocationType;
import cz.mapnik.app.model.Player;
import cz.mapnik.app.utils.MathUtils;
import cz.mapnik.app.utils.SmartLog;

/**
 * Created by chaemil on 3.2.16.
 */
public class PrepareGameAsync extends AsyncTask{

    private Game game;
    private ArrayList<Player> players;
    private BaseActivity caller;
    ArrayList<Guess> guesses = new ArrayList<>();
    private int cycles = 0;

    private int ROUNDS = 5;
    private double MIN_LAT = -90;
    private double MAX_LAT = 90;
    private double MIN_LNG = -180;
    private double MAX_LNG = 180;

    public PrepareGameAsync(Game game, ArrayList<Player> players, BaseActivity caller) {
        this.game = game;
        this.players = players;
        this.caller = caller;
    }

    @Override
    protected Object doInBackground(Object[] params) {

        double minLat = MIN_LAT;
        double maxLat = MAX_LAT;
        double minLng = MIN_LNG;
        double maxLng = MAX_LNG;

        switch (game.getLocationType()) {
            case LocationType.CITY:
                minLat = game.getGameLocation().getNorthEastBound().latitude;
                maxLat = game.getGameLocation().getSouthWestBound().latitude;
                minLng = game.getGameLocation().getNorthEastBound().longitude;
                maxLng = game.getGameLocation().getSouthWestBound().longitude;
                break;
        }


        for(int r = 0; r < ROUNDS * 5; r++) {

            double resultLat = MathUtils.randomInRange(minLat, maxLat);
            double resultLng = MathUtils.randomInRange(minLng, maxLng);

            checkForStreetView(caller, resultLat, resultLng);

        }

        return null;
    }

    private void addGuess() {
        guesses.add(new Guess("test",new LatLng(0.0, 0.0), "fake1", "fake2"));
    }

    private void checkForStreetView(final Context context, final double lat, final double lng) {

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
                                addGuess();
                            }

                            cycles += 1;

                            if (cycles == ROUNDS * 5) {
                                caller.gamePreparationFinished(guesses);
                            }
                        }
                    }
                });


            }
        });

    }
}
