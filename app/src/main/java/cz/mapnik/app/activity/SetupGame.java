package cz.mapnik.app.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import at.markushi.ui.CircleButton;
import cz.mapnik.app.Mapnik;
import cz.mapnik.app.R;
import cz.mapnik.app.model.Game;
import cz.mapnik.app.model.GameLocation;
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
    private CircleButton confirmLocation;
    private LatLng cityLatLng;
    private ProgressBar geocoderProgress;
    private CircleButton previewLocation;
    private CircleButton close;
    private CircleButton done;
    private LinearLayout wrapper;

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
        wrapper = (LinearLayout) findViewById(R.id.wrapper);
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
        locationText = (TextView) findViewById(R.id.locationText);
        previewLocation = (CircleButton) findViewById(R.id.previewLocationButton);
        close = (CircleButton) findViewById(R.id.closeButton);
        done = (CircleButton) findViewById(R.id.doneButton);
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
        previewLocation.setOnClickListener(this);
        close.setOnClickListener(this);
        done.setOnClickListener(this);
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
                locationText.setText("");
                game.setLocationType(null);
                createLocationCityDialog();
                break;
            case R.id.location_monuments:
                setLocation(Game.LocationType.MONUMENTS);
                game.setGameLocation(new GameLocation(getString(R.string.monuments), new LatLng(0, 0)));
                break;
            case R.id.location_custom:
                Intent selectLocation = new Intent(this, MapSelectLocationActivity.class);
                setLocation(Game.LocationType.CUSTOM);
                startActivityForResult(selectLocation, MapSelectLocationActivity.SELECT_LOCATION);
                break;
            case R.id.location_random:
                setLocation(Game.LocationType.RANDOM);
                game.setGameLocation(new GameLocation(getString(R.string.random), new LatLng(0, 0)));
                break;
            case R.id.confirmLocation:
                if (game.getGameLocation() == null) {
                    if (cityEditText.getText().length() > 0) {
                        cityEditText.removeTextChangedListener(cityEditTextWatcher);
                        geocoderProgress.setVisibility(View.VISIBLE);
                        cityEditText.setEnabled(false);
                        MapnikGeocoder.getCityFromAddress(cityEditText.getText().toString().trim(), this);
                    } else {
                        locationCityError(true);
                    }
                } else {
                    locationCityDialog.dismiss();
                }
                break;
            case R.id.previewLocationButton:
                if (game.getGameLocation() != null) {
                    Intent previewMap = new Intent(this, MapPreviewActivity.class);
                    previewMap.putExtra(MapPreviewActivity.PREVIEW_PIN_LAT_LNG, game.getGameLocation().getCenter());
                    startActivity(previewMap);
                    overridePendingTransition(0 ,0);
                }
                break;
            case R.id.closeButton:
                finish();
                overridePendingTransition(0 ,0);
                break;
            case R.id.doneButton:
                if (game != null
                        &&game.getGameLocation() != null
                        && game.getType() != null
                        && game.getLocationType() != null
                        && game.getTime() != null) {

                    Intent prepareGame = new Intent(this, PrepareGameActivity.class);
                    startActivity(prepareGame);
                    finish();

                } else {
                    YoYo.with(Techniques.Pulse).duration(150).playOn(wrapper);
                }
                break;
        }
    }

    TextWatcher cityEditTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            locationCityError(false);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void geocodingFinished(String strAddress, GameLocation gameLocation) {
        super.geocodingFinished(strAddress, gameLocation);

        if (gameLocation != null) {
            confirmLocation.setColor(getResources().getColor(R.color.bright_green));
            confirmLocation.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_done));
            locationText.setText(gameLocation.getName());
            cityEditText.setText(gameLocation.getName());
            cityEditText.selectAll();

            cityEditText.addTextChangedListener(cityEditTextWatcher);
            game.setGameLocation(gameLocation);
            setLocation(Game.LocationType.CITY);
        } else {
            locationCityError(true);
        }
        cityEditText.setEnabled(true);
        geocoderProgress.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case MapSelectLocationActivity.SELECT_LOCATION:
                    setLocation(Game.LocationType.CUSTOM);
                    GameLocation gameLocation = new GameLocation(getString(R.string.custom),
                            (LatLng) data.getParcelableExtra(MapSelectLocationActivity.LOCATION));
                    game.setGameLocation(gameLocation);
                    break;
            }
        } else {
            switch (requestCode) {
                case MapSelectLocationActivity.SELECT_LOCATION:
                    setLocation(null);
                    game.setGameLocation(null);
                    break;
            }
        }
    }

    private void locationCityError(boolean animate) {
        if (cityEditText != null) {
            if (animate) {
                YoYo.with(Techniques.Tada).duration(150).playOn(cityEditText);
            }
            confirmLocation.setColor(getResources().getColor(R.color.red));
            confirmLocation.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_search));
            locationText.setText("");
            game.setGameLocation(null);
            setLocation(null);
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

    private void setLocation(Game.LocationType locationType) {
        locationCity.setColor(getResources().getColor(R.color.bright_green));
        locationMonuments.setColor(getResources().getColor(R.color.bright_green));
        locationCustom.setColor(getResources().getColor(R.color.bright_green));
        locationRandom.setColor(getResources().getColor(R.color.bright_green));

        if (locationType == null) {
            game.setGameLocation(null);
            locationText.setText("");
        } else {
            switch (locationType) {
                case CITY:
                    locationCity.setColor(getResources().getColor(R.color.bright_purple));
                    break;
                case MONUMENTS:
                    locationMonuments.setColor(getResources().getColor(R.color.bright_purple));
                    locationText.setText(getString(R.string.monuments));
                    break;
                case CUSTOM:
                    locationCustom.setColor(getResources().getColor(R.color.bright_purple));
                    locationText.setText(getString(R.string.custom));
                    break;
                case RANDOM:
                    locationRandom.setColor(getResources().getColor(R.color.bright_purple));
                    locationText.setText(getString(R.string.random));
                    break;
            }

            game.setLocationType(locationType);
        }
    }

    private void createLocationCityDialog() {
        locationCityDialog = new Dialog(this);
        locationCityDialog.setCanceledOnTouchOutside(false);
        locationCityDialog.setContentView(R.layout.dialog_location_city);

        cityLatLng = null;

        cityEditText = (AppCompatEditText) locationCityDialog.findViewById(R.id.cityEditText);
        confirmLocation = (CircleButton) locationCityDialog.findViewById(R.id.confirmLocation);
        geocoderProgress = (ProgressBar) locationCityDialog.findViewById(R.id.progress);

        confirmLocation.setOnClickListener(this);

        locationCityDialog.show();
    }
}
