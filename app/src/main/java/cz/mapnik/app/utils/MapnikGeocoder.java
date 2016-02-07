package cz.mapnik.app.utils;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import cz.mapnik.app.activity.BaseActivity;
import cz.mapnik.app.model.GameLocation;

/**
 * Created by chaemil on 3.2.16.
 */


public class MapnikGeocoder {

    public static void getLocationFromAddress(final String strAddress, final BaseActivity caller) {

        String baseUrl = "http://maps.googleapis.com/maps/api/geocode/json?address=";

        String address = strAddress;
        try {
            address = URLEncoder.encode(strAddress, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        SmartLog.Log(SmartLog.LogLevel.DEBUG, "url", baseUrl + address);

        Ion.with(caller).load(baseUrl + address).asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                GameLocation gameLocation = null;

                if (result != null) {
                    SmartLog.Log(SmartLog.LogLevel.DEBUG, "result", result.toString());

                    if (result.has("status")) {

                        SmartLog.Log(SmartLog.LogLevel.DEBUG, "result.has(status)", result.get("status").getAsString());

                        if (result.get("status").getAsString().equals("OK")) {

                            JsonArray results = result.getAsJsonArray("results");

                            JsonArray addressComponents = results.get(0).getAsJsonObject().get("address_components").getAsJsonArray();

                            String cityName = "";

                            boolean addressFound = false;
                            for (int a = 0; a < addressComponents.size(); a++) {
                                JsonObject addressComponent = addressComponents.get(a).getAsJsonObject();
                                JsonArray types = addressComponent.get("types").getAsJsonArray();
                                for (int t = 0; t < types.size(); t++) {
                                    if (!addressFound) {
                                        String type = types.get(t).getAsString();

                                        if (type.equals("neighborhood")
                                                || type.equals("sublocality_level_1")
                                                || type.equals("locality")
                                                || type.equals("administrative_area_level_2")
                                                || type.equals("administrative_area_level_1")) {

                                            cityName = addressComponent.get("long_name").getAsString();
                                            addressFound = true;
                                        }
                                    }
                                }
                            }

                            if (cityName.equals("")) {
                                cityName = results.get(0).getAsJsonObject().get("formatted_address").getAsString();
                            }

                            if (!cityName.equals("")) {

                                JsonObject geometry = results.get(0).getAsJsonObject().get("geometry").getAsJsonObject();
                                JsonObject location = geometry.get("location").getAsJsonObject();
                                LatLng centerLatLng = new LatLng(location.get("lat").getAsDouble(), location.get("lng").getAsDouble());
                                LatLng northEast = null;
                                LatLng southWest = null;

                                if (geometry.has("bounds")) {
                                    SmartLog.Log(SmartLog.LogLevel.DEBUG, "hasBounds", String.valueOf(true));
                                    JsonObject northEastBounds = geometry.get("bounds").getAsJsonObject().get("northeast").getAsJsonObject();
                                    JsonObject southWestBounds = geometry.get("bounds").getAsJsonObject().get("southwest").getAsJsonObject();

                                    northEast = new LatLng(northEastBounds.get("lat").getAsDouble(), northEastBounds.get("lng").getAsDouble());
                                    southWest = new LatLng(southWestBounds.get("lat").getAsDouble(), southWestBounds.get("lng").getAsDouble());

                                    SmartLog.Log(SmartLog.LogLevel.DEBUG, "northEast", northEast.toString());

                                    gameLocation = new GameLocation(cityName, centerLatLng, northEast, southWest);
                                } else {
                                    gameLocation = new GameLocation(cityName, centerLatLng);
                                }
                            }
                        }
                    }
                }

                caller.geocodingFinished(strAddress, gameLocation);
            }

        });

    }

    public static LatLng getRandomNearbyLocation(double latitude, double longitude, int radius) {
        Random random = new Random();

        // Convert radius from meters to degrees
        double radiusInDegrees = radius / 111000f;

        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x = x / Math.cos(latitude);

        double foundLongitude = new_x + longitude;
        double foundLatitude = y + latitude;

        return new LatLng(foundLatitude, foundLongitude);
    }

    public static List<Address> getAddressFromLatLng(Context context, double lat, double lng,
                                                     int numberOfLocations) {
        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lat,lng,numberOfLocations);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addresses;
    }

}
