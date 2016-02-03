package cz.mapnik.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import at.markushi.ui.CircleButton;
import cz.mapnik.app.R;

/**
 * Created by chaemil on 3.2.16.
 */

public class MapSelectLocationActivity extends BaseActivity implements OnMapReadyCallback, View.OnClickListener {

    public static final String LOCATION = "location";
    public static final int SELECT_LOCATION = 5;
    private MapFragment mapFragment;
    private GoogleMap map;
    private CircleButton confirm;
    private LatLng customLocation;
    private CircleButton cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_select_location);

        getUI();

        setupUI();
    }


    private void getUI() {
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        map = mapFragment.getMap();
        confirm = (CircleButton) findViewById(R.id.confirm);
        cancel = (CircleButton) findViewById(R.id.closeButton);
    }

    private void setupUI() {
        confirm.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                if (customLocation != null) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(LOCATION, customLocation);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else {
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                }
                break;
            case R.id.closeButton:
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                map.getUiSettings().setMapToolbarEnabled(false);
                map.getUiSettings().setAllGesturesEnabled(false);
                map.getUiSettings().setZoomGesturesEnabled(true);
                map.getUiSettings().setScrollGesturesEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(false);

                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        customLocation = latLng;

                        map.clear();
                        map.addMarker(new MarkerOptions()
                                .position(latLng));

                        if (confirm.getVisibility() != View.VISIBLE) {
                            confirm.setVisibility(View.VISIBLE);
                            YoYo.with(Techniques.SlideInUp)
                                    .duration(400)
                                    .playOn(confirm);
                        }
                    }
                });
            }
        });

    }
}
