package cz.mapnik.app.service;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

import cz.mapnik.app.R;
import cz.mapnik.app.activity.BaseActivity;
import cz.mapnik.app.model.Game;
import cz.mapnik.app.model.Guess;
import cz.mapnik.app.model.LocationType;
import cz.mapnik.app.utils.MapnikGeocoder;
import cz.mapnik.app.utils.MathUtils;
import cz.mapnik.app.utils.SmartLog;

/**
 * Created by chaemil on 3.2.16.
 */
public class PrepareGameAsync extends AsyncTask{

    public static final String ERROR_LINK = "http://maps.googleapis.com/maps/api/streetview?size=400x400&location=34.834806,-41.314475";
    public static final int DEFAULT_GUESS_RADIUS = 800;
    public static final int ROUNDS = 5;
    public static final int TRIES = 5;
    public static final int MIN_VALID_GUESSES = 5;
    public static final double MIN_LAT = -90;
    public static final double MAX_LAT = 90;
    public static final double MIN_LNG = -180;
    public static final double MAX_LNG = 180;

    private String errorString;
    private Game game;
    private BaseActivity caller;
    ArrayList<Guess> guesses = new ArrayList<>();
    private int cycles = 0;
    private boolean boundsAvailable = false;
    private String apiKey;
    private ArrayList<AsyncTask> childAsyncTasks;

    public PrepareGameAsync(Game game, BaseActivity caller) {
        this.game = game;
        this.caller = caller;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        apiKey = caller.getResources().getString(R.string.api_key);
        childAsyncTasks = new ArrayList<>();

        Ion.with(caller).load(ERROR_LINK + "&key=" + apiKey).asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                errorString = result;
            }
        });

        caller.setMaxPreparationSteps(ROUNDS * TRIES);
    }

    @Override
    protected Object doInBackground(Object[] params) {

        doTheWork();

        return null;
    }

    private void doTheWork() {

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

    }


    private void checkForStreetView(final Context context, final boolean boundsAvailable,
                                    final double lat, final double lng,
                                    final double minLat, final double maxLat, final double minLng, final double maxLng,
                                    final int radius) {

        final String baseLink = "http://maps.googleapis.com/maps/api/streetview?size=400x400&sensor=true&location=" + lat + "," + lng + "&key=" + apiKey;

        Ion.with(context).load(baseLink).asString().setCallback(new FutureCallback<String>() {
            @Override
            public void onCompleted(Exception e, String result) {
                if (result != null && errorString != null) {

                    SmartLog.Log(SmartLog.LogLevel.DEBUG, "link", baseLink);

                    cycles += 1;

                    caller.increaseCurrentPreparationStep();

                    SmartLog.Log(SmartLog.LogLevel.DEBUG, "cycles", String.valueOf(cycles));

                    if (errorString.equals(result)) {
                        SmartLog.Log(SmartLog.LogLevel.DEBUG, "available", "false");

                        if (cycles == ROUNDS * TRIES) {
                            preparationFinished();
                        }

                    } else {
                        SmartLog.Log(SmartLog.LogLevel.DEBUG, "available", "true");

                        CreateGuessAsync createGuessAsync;

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

                        AsyncTask childAsync = createGuessAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        childAsyncTasks.add(childAsync);

                    }
                }
            }
        });

    }

    public void addGuess(Guess guess) {
        guesses.add(guess);
    }

    public void preparationFinished() {

        for (int a = 0; a < childAsyncTasks.size(); a++) {
            if (childAsyncTasks.get(a) != null) {
                childAsyncTasks.get(a).cancel(true);
            }
        }

        SmartLog.Log(SmartLog.LogLevel.DEBUG, "finalGuessesCount", String.valueOf(guesses.size()));

        caller.gamePreparationFinished(guesses);
    }
}
