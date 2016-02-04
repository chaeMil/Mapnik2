package cz.mapnik.app.interfaces;

import cz.mapnik.app.model.GameLocation;

public interface GeocoderCallbacks {

    void geocodingFinished(String strAddress, GameLocation gameLocation);
}