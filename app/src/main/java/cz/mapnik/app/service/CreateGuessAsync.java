package cz.mapnik.app.service;

import android.content.Context;
import android.location.Address;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import cz.mapnik.app.model.Guess;
import cz.mapnik.app.utils.MapnikGeocoder;
import cz.mapnik.app.utils.MathUtils;
import cz.mapnik.app.utils.SmartLog;

/**
 * Created by chaemil on 4.2.16.
 */
public class CreateGuessAsync extends AsyncTask {

    private PrepareGameAsync caller;
    private Context context;
    private double originLat;
    private double originLng;
    private double minLat;
    private double maxLat;
    private double minLng;
    private double maxLng;
    private int radius;
    private boolean lastOne;

    public CreateGuessAsync(PrepareGameAsync caller, Context context, double originLat, double originLng, int radius, boolean lastOne) {
        this.caller = caller;
        this.context = context;
        this.originLat = originLat;
        this.originLng = originLng;
        this.radius = radius;
        this.lastOne = lastOne;
    }

    public CreateGuessAsync(PrepareGameAsync caller, Context context, double originLat, double originLng, double minLat, double maxLat, double minLng, double maxLng, boolean lastOne) {
        this.caller = caller;
        this.context = context;
        this.originLat = originLat;
        this.originLng = originLng;
        this.minLat = minLat;
        this.maxLat = maxLat;
        this.minLng = minLng;
        this.maxLng = maxLng;
        this.lastOne = lastOne;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        init(originLat, originLng, minLat, maxLat, minLng, maxLng, radius);

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        if (lastOne) {
            caller.preparationFinished();
        }
    }

    private void init(double originLat, double originLng, double minLat, double maxLat, double minLng, double maxLng, int radius) {

        Guess guess;

        if (minLat != 0 && maxLat != 0 && minLng != 0 && maxLng != 0) {

            guess = createGuess(originLat, originLng, minLat, maxLat, minLng, maxLng);

        } else {

            guess = createGuess(originLat, originLng, radius);

        }

        addGuess(guess);

    }

    private void addGuess(Guess guess) {
        try {
            caller.addGuess(guess);
        } catch (Exception e) {
            SmartLog.Log(SmartLog.LogLevel.DEBUG, "exception", e.getMessage());
        }
    }

    private Guess createGuess(double originLat, double originLng, double minLat,
                              double maxLat, double minLng, double maxLng) {


        String address = getAddress(originLat, originLng);
        String fakeAddress1 = getFakeAddress(minLat, maxLat, minLng, maxLng);
        String fakeAddress2 = getFakeAddress(minLat, maxLat, minLng, maxLng);

        Guess guess = new Guess(address, new LatLng(originLat, originLng), fakeAddress1, fakeAddress2);

        SmartLog.Log(SmartLog.LogLevel.DEBUG, "address", address + ", " + originLat + ", " + originLng);

        return guess;

    }

    private Guess createGuess(double originLat, double originLng, int radius) {

        List<Address> addresses = MapnikGeocoder.getAddressFromLatLng(context, originLat, originLng, 1);
        String address = addresses.get(0).getAddressLine(0);
        String fakeAddress1 = getFakeAddress(originLat, originLng, radius);
        String fakeAddress2 = getFakeAddress(originLat, originLng, radius);

        Guess guess = new Guess(address, new LatLng(originLat, originLng), fakeAddress1, fakeAddress2);

        SmartLog.Log(SmartLog.LogLevel.DEBUG, "address", address + ", " + originLat + ", " + originLng);

        return guess;
    }

    private String getAddress (double lat, double lng) {

        List<Address> addresses = MapnikGeocoder.getAddressFromLatLng(context, lat, lng, 1);

        return addresses.get(0).getAddressLine(0);
    }

    private String getFakeAddress(double minLat, double maxLat, double minLng, double maxLng) {

        List<Address> fakeAddresses = MapnikGeocoder.getAddressFromLatLng(context,
                MathUtils.randomInRange(minLat, maxLat),
                MathUtils.randomInRange(minLng, maxLng),
                1);
        return fakeAddresses.get(0).getAddressLine(0);

    }

    private String getFakeAddress(double originLat, double originLng, int radius) {

        LatLng randomLocation = MapnikGeocoder.getRandomNearbyLocation(originLat, originLng, radius);

        return getAddress(randomLocation.latitude, randomLocation.longitude);

    }
}
