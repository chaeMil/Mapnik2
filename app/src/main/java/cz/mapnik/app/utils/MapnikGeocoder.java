package cz.mapnik.app.utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cz.mapnik.app.activity.BaseActivity;
import cz.mapnik.app.model.GameLocation;

/**
 * Created by chaemil on 3.2.16.
 */


public class MapnikGeocoder {

    public static void getCityFromAddress(final String strAddress, final BaseActivity caller) {

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

                            for (int a = 0; a < addressComponents.size(); a++) {
                                JsonObject addressComponent = addressComponents.get(a).getAsJsonObject();
                                JsonArray types = addressComponent.get("types").getAsJsonArray();
                                for (int t = 0; t < types.size(); t++) {
                                    if (types.get(t).getAsString().equals("locality")) {
                                        cityName = addressComponent.get("long_name").getAsString();
                                        break;
                                    }
                                }
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

}
