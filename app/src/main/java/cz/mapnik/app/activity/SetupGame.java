package cz.mapnik.app.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import at.markushi.ui.CircleButton;
import cz.mapnik.app.Mapnik;
import cz.mapnik.app.R;
import cz.mapnik.app.model.Game;
import cz.mapnik.app.model.Player;
import cz.mapnik.app.utils.MapnikGeocoder;

/**
 * Created by chaemil on 3.2.16.
 */
public class SetupGame extends BaseActivity implements View.OnClickListener {

    private TextView locationText;
    private ArrayList<Player> players;
    private CircleButton typeMap;
    private CircleButton typeAddress;
    private CircleButton time_30s;
    private CircleButton time_1m;
    private CircleButton time_3min;
    private CircleButton time_5min;
    private CircleButton locationCity;
    private CircleButton locationMonuments;
    private CircleButton locationCustom;
    private CircleButton locationRandom;
    private Game game;
    private Dialog locationCityDialog;
    private AppCompatEditText cityEditText;
    private CircleButton checkForLatLng;
    private CircleButton confirmLocation;
    private LatLng cityLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_game);

        game = new Game();

        getData();

        getUI();

        setupUI();
    }

    private void getData() {
        players = ((Mapnik) getApplication()).getPlayers();
    }

    private void getUI() {
        typeMap = (CircleButton) findViewById(R.id.typeMap);
        typeAddress = (CircleButton) findViewById(R.id.typeAddress);
        time_30s = (CircleButton) findViewById(R.id.time_30sec);
        time_1m = (CircleButton) findViewById(R.id.time_1min);
        time_3min = (CircleButton) findViewById(R.id.time_3min);
        time_5min = (CircleButton) findViewById(R.id.time_5min);
        locationCity = (CircleButton) findViewById(R.id.location_cities);
        locationMonuments = (CircleButton) findViewById(R.id.location_monuments);
        locationCustom = (CircleButton) findViewById(R.id.location_custom);
        locationRandom = (CircleButton) findViewById(R.id.location_random);
        locationText = (TextView) findViewById(R.id.location);
    }

    private void setupUI() {
        typeMap.setOnClickListener(this);
        typeAddress.setOnClickListener(this);
        time_30s.setOnClickListener(this);
        time_1m.setOnClickListener(this);
        time_3min.setOnClickListener(this);
        time_5min.setOnClickListener(this);
        locationCity.setOnClickListener(this);
        locationMonuments.setOnClickListener(this);
        locationCustom.setOnClickListener(this);
        locationRandom.setOnClickListener(this);
        locationText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.typeAddress:
                setType(Game.Type.ADDRESS);
                break;
            case R.id.typeMap:
                setType(Game.Type.MAP);
                break;
            case R.id.time_30sec:
                setTime(Game.Time.S30);
                break;
            case R.id.time_1min:
                setTime(Game.Time.M1);
                break;
            case R.id.time_3min:
                setTime(Game.Time.M3);
                break;
            case R.id.time_5min:
                setTime(Game.Time.M5);
                break;
            case R.id.location_cities:
                setLocation(null);
                createLocationCityDialog();
                break;
            case R.id.location_monuments:
                setLocation(Game.Location.MONUMENTS);
                break;
            case R.id.location_custom:
                setLocation(Game.Location.CUSTOM);
                break;
            case R.id.location_random:
                setLocation(Game.Location.RANDOM);
                break;
            case R.id.confirmLocation:
                if (cityLatLng == null) {
                    if (cityEditText.getText().length() > 0) {
                        cityLatLng = MapnikGeocoder.getLocationFromAddress(this, cityEditText.toString().trim());
                        if (cityLatLng != null) {
                            confirmLocation.setColor(getResources().getColor(R.color.bright_green));
                            locationText.setText(cityEditText.getText());
                            game.setStartingLatLng(cityLatLng);
                        } else {
                            locationCityError();
                        }
                    } else {
                        locationCityError();
                    }
                } else {
                    locationCityDialog.dismiss();
                }
                break;
        }
    }

    private void locationCityError() {
        if (cityEditText != null) {
            YoYo.with(Techniques.Tada).duration(150).playOn(cityEditText);
            confirmLocation.setColor(getResources().getColor(R.color.red));
            locationText.setText("");
            game.setStartingLatLng(null);
        }
    }


    private void setType(Game.Type type) {
        typeMap.setColor(getResources().getColor(R.color.bright_green));
        typeAddress.setColor(getResources().getColor(R.color.bright_green));

        switch (type) {
            case MAP:
                typeMap.setColor(getResources().getColor(R.color.bright_purple));
                break;
            case ADDRESS:
                typeAddress.setColor(getResources().getColor(R.color.bright_purple));
                break;
        }

        game.setType(type);
    }

    private void setTime(Game.Time time) {
        time_30s.setColor(getResources().getColor(R.color.bright_green));
        time_1m.setColor(getResources().getColor(R.color.bright_green));
        time_3min.setColor(getResources().getColor(R.color.bright_green));
        time_5min.setColor(getResources().getColor(R.color.bright_green));

        switch (time) {
            case S30:
                time_30s.setColor(getResources().getColor(R.color.bright_purple));
                break;
            case M1:
                time_1m.setColor(getResources().getColor(R.color.bright_purple));
                break;
            case M3:
                time_3min.setColor(getResources().getColor(R.color.bright_purple));
                break;
            case M5:
                time_5min.setColor(getResources().getColor(R.color.bright_purple));
                break;
        }

        game.setTime(time);
    }

    private void setLocation(Game.Location location) {
        locationCity.setColor(getResources().getColor(R.color.bright_green));
        locationMonuments.setColor(getResources().getColor(R.color.bright_green));
        locationCustom.setColor(getResources().getColor(R.color.bright_green));
        locationRandom.setColor(getResources().getColor(R.color.bright_green));

        if (location == null) {
            game.setLocation(null);
        } else {
            switch (location) {
                case CITY:
                    locationCity.setColor(getResources().getColor(R.color.bright_purple));
                    break;
                case MONUMENTS:
                    locationMonuments.setColor(getResources().getColor(R.color.bright_purple));
                    break;
                case CUSTOM:
                    locationCustom.setColor(getResources().getColor(R.color.bright_purple));
                    break;
                case RANDOM:
                    locationRandom.setColor(getResources().getColor(R.color.bright_purple));
                    break;
            }

            game.setLocation(location);
        }
    }

    private void createLocationCityDialog() {
        locationCityDialog = new Dialog(this);
        locationCityDialog.setCanceledOnTouchOutside(false);
        locationCityDialog.setContentView(R.layout.dialog_location_city);

        cityLatLng = null;

        cityEditText = (AppCompatEditText) locationCityDialog.findViewById(R.id.cityEditText);
        checkForLatLng = (CircleButton) locationCityDialog.findViewById(R.id.checkForLatLng);
        confirmLocation = (CircleButton) locationCityDialog.findViewById(R.id.confirmLocation);

        checkForLatLng.setOnClickListener(this);
        confirmLocation.setOnClickListener(this);

        locationCityDialog.show();
    }
}
