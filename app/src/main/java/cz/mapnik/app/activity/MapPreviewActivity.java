package cz.mapnik.app.activity;

import android.os.Bundle;
import android.view.View;

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

public class MapPreviewActivity extends BaseActivity implements OnMapReadyCallback, View.OnClickListener {

    public static final String PREVIEW_PIN_LAT_LNG = "preview_pin_lat_lng";
    private MapFragment mapFragment;
    private GoogleMap map;
    private Bundle extras;
    private CircleButton close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_preview);

        extras = getIntent().getExtras();

        getUI();

        setupUI();
    }


    private void getUI() {
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        map = mapFragment.getMap();
        close = (CircleButton) findViewById(R.id.closeButton);
    }

    private void setupUI() {
        close.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closeButton:
                finish();
                overridePendingTransition(0 ,0);
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng previewPin = extras.getParcelable(PREVIEW_PIN_LAT_LNG);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(previewPin, 5));
        map.addMarker(new MarkerOptions()
                .position(previewPin));

    }
}
