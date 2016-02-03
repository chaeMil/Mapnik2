package cz.mapnik.app.interfaces;

import cz.mapnik.app.model.GameLocation;

public abstract interface GeocoderCallbacks {

    public abstract void geocodingFinished(String strAddress, GameLocation gameLocation);

}