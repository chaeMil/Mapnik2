package cz.mapnik.app.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by chaemil on 3.2.16.
 */


public class MapnikGeocoder {

    public static LatLng getLocationFromAddress(Context context, String strAddress){

        Geocoder coder = new Geocoder(context);
        List<Address> address;

        try {
            address = coder.getFromLocationName(strAddress, 5);

            if (address == null) {
                return null;
            }

            Address location = address.get(0);

            return new LatLng(location.getLatitude(), location.getLongitude());

        } catch (Exception e) {
            SmartLog.Log(SmartLog.LogLevel.ERROR, "exception", e.toString());
        }

        return null;
    }

}
