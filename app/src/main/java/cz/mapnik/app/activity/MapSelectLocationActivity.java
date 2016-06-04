package cz.mapnik.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import at.markushi.ui.CircleButton;
import cz.mapnik.app.R;
import cz.mapnik.app.utils.ChromeOSUtils;
import cz.mapnik.app.utils.MapUtils;

/**
 * Created by chaemil on 3.2.16.
 */

public class MapSelectLocationActivity extends BaseActivity implements OnMapReadyCallback, View.OnClickListener {

    public static final int DEFAULT_RADIUS = 3000;
    public static final int MIN_RADIUS = 500;
    public static final int MAX_RADIUS = 100 * 1000;
    public static final String LOCATION = "location";
    public static final int SELECT_LOCATION = 5;
    public static final String RADIUS = "radius";
    private MapFragment mapFragment;
    private GoogleMap map;
    private CircleButton confirm;
    private LatLng customLocation;
    private CircleButton cancel;
    private int radius;
    private CircleButton expandButton;
    private CircleButton shrinkButton;
    private CircleButton zoomIn;
    private CircleButton zoomOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_select_location);

        radius = DEFAULT_RADIUS;

        getUI();

        setupUI();
    }


    private void getUI() {
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        map = mapFragment.getMap();
        confirm = (CircleButton) findViewById(R.id.confirm);
        cancel = (CircleButton) findViewById(R.id.closeButton);
        expandButton = (CircleButton) findViewById(R.id.expandButton);
        shrinkButton = (CircleButton) findViewById(R.id.shrinkButton);
        zoomIn = (CircleButton) findViewById(R.id.zoomIn);
        zoomOut = (CircleButton) findViewById(R.id.zoomOut);
    }

    private void setupUI() {
        confirm.setOnClickListener(this);
        cancel.setOnClickListener(this);
        expandButton.setOnClickListener(this);
        shrinkButton.setOnClickListener(this);
        zoomIn.setOnClickListener(this);
        zoomOut.setOnClickListener(this);

        if (ChromeOSUtils.isRunningInChromeOS()) {
            zoomIn.setVisibility(View.VISIBLE);
            zoomOut.setVisibility(View.VISIBLE);
        }
    }

    private void shrink() {
        if (customLocation != null) {
            if (radius >= MIN_RADIUS) {
                radius = radius / 2;
            }
            map.clear();
            addCircle();
        }
    }

    private void expand() {
        if (customLocation != null) {
            if (radius <= MAX_RADIUS) {
                radius = radius * 2;
            }
            map.clear();
            addCircle();
        }
    }

    private void zoomIn() {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(map.getCameraPosition().target)
                .zoom(map.getCameraPosition().zoom + 1)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void zoomOut() {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(map.getCameraPosition().target)
                .zoom(map.getCameraPosition().zoom - 1)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void addCircle() {
        Circle circle = map.addCircle(new CircleOptions()
                .center(customLocation)
                .radius(radius)
                .fillColor(getResources().getColor(R.color.bright_green_alpha))
                .strokeColor(getResources().getColor(R.color.bright_green))
                .strokeWidth(5));

        double radius = circle.getRadius();

        LatLngBounds bounds = MapUtils.convertCenterAndRadiusToBounds(customLocation, radius);

        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm:
                if (customLocation != null) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra(LOCATION, customLocation);
                    returnIntent.putExtra(RADIUS, radius);
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
            case R.id.expandButton:
                expand();
                break;
            case R.id.shrinkButton:
                shrink();
                break;
            case R.id.zoomIn:
                zoomIn();
                break;
            case R.id.zoomOut:
                zoomOut();
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
                        addCircle();

                        if (confirm.getVisibility() != View.VISIBLE) {
                            confirm.setVisibility(View.VISIBLE);
                            shrinkButton.setVisibility(View.VISIBLE);
                            expandButton.setVisibility(View.VISIBLE);

                            YoYo.with(Techniques.BounceInUp)
                                    .duration(250)
                                    .playOn(confirm);
                            YoYo.with(Techniques.BounceInUp)
                                    .duration(250)
                                    .playOn(shrinkButton);
                            YoYo.with(Techniques.BounceInUp)
                                    .duration(250)
                                    .playOn(expandButton);

                        }
                    }
                });
            }
        });

    }
}
