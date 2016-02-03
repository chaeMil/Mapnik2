package cz.mapnik.app.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.Random;

import cz.mapnik.app.activity.BaseActivity;
import cz.mapnik.app.model.Game;
import cz.mapnik.app.model.Guess;
import cz.mapnik.app.model.LocationType;
import cz.mapnik.app.model.Player;
import cz.mapnik.app.utils.SmartLog;

/**
 * Created by chaemil on 3.2.16.
 */
public class PrepareGameAsync extends AsyncTask {

    private Game game;
    private ArrayList<Player> players;
    private BaseActivity caller;

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


        ArrayList<Guess> guesses = new ArrayList<>();
        double minLat = MIN_LAT;
        double maxLat = MAX_LAT;
        double minLng = MIN_LNG;
        double maxLng = MAX_LNG;

        switch (game.getLocationType()) {
            case LocationType.CITY:
                minLat = game.getGameLocation().getNorthEastBound().latitude;
                maxLat = game.getGameLocation().getSouthWestBound().latitude;
                minLng = game.getGameLocation().getNorthEastBound().longitude;
                minLng = game.getGameLocation().getSouthWestBound().longitude;
                break;
        }


        for(int r = 0; r < ROUNDS; r++) {

            Random randomLat = new Random();
            Random randomLng = new Random();

            double resultLat = minLat + (maxLat - minLat) * randomLat.nextDouble();
            double resultLng = minLng + (maxLng - minLng) * randomLng.nextDouble();

            boolean streetViewAvailable = checkForStreetView(caller, resultLat, resultLng);

            SmartLog.Log(SmartLog.LogLevel.DEBUG, "streetViewAvailable", String.valueOf(streetViewAvailable));
        }


        return null;
    }

    private boolean checkForStreetView(Context context, double lat, double lng) {

        String baseLink = "http://maps.googleapis.com/maps/api/streetview?size=400x400&location=";
        final int noStreetViewImageSize = 640000;
        final boolean[] available = {false};

        Ion.with(context).load(baseLink + lat + "," + lng).asBitmap().setCallback(new FutureCallback<Bitmap>() {
            @Override
            public void onCompleted(Exception e, Bitmap result) {
                if (result != null) {
                    if (result.getByteCount() == noStreetViewImageSize) {
                        available[0] = false;
                    } else {
                        available[0] = true;
                    }
                }
            }
        });

        return available[0];
    }

}
