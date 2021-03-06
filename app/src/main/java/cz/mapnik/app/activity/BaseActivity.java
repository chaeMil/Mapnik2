package cz.mapnik.app.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import java.util.ArrayList;

import cz.mapnik.app.interfaces.GeocoderCallbacks;
import cz.mapnik.app.model.GameLocation;
import cz.mapnik.app.model.Guess;
import cz.mapnik.app.utils.SmartLog;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by chaemil on 2.2.16.
 */

public class BaseActivity extends AppCompatActivity implements GeocoderCallbacks {

    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getUI();

        setupUI();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.i("key pressed", String.valueOf(event.getKeyCode()));
        return super.dispatchKeyEvent(event);
    }

    private void getUI() {
        decorView = getWindow().getDecorView().getRootView();
    }

    private void setupUI() {

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

            } else {
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
            }
        }
    }

    @Override
    public void geocodingFinished(String strAddress, GameLocation gameLocation) {
        SmartLog.Log(SmartLog.LogLevel.DEBUG, "strAddress", strAddress);
        if (gameLocation != null) {
            SmartLog.Log(SmartLog.LogLevel.DEBUG, "gameLocation.name", gameLocation.getName());
        }
    }

    public void gamePreparationFinished(ArrayList<Guess> guesses) {
        SmartLog.Log(SmartLog.LogLevel.DEBUG, "guesses.size()", String.valueOf(guesses.size()));
    }

    public void setMaxPreparationSteps(int i) {
        SmartLog.Log(SmartLog.LogLevel.DEBUG, "setMaxPreparationSteps", String.valueOf(i));
    }

    public void increaseCurrentPreparationStep() {

    }
}
