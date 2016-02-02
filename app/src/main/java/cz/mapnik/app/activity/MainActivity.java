package cz.mapnik.app.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import at.markushi.ui.CircleButton;
import cz.mapnik.app.Mapnik;
import cz.mapnik.app.R;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private CircleButton playButton;
    private CircleButton closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getUI();

        setupUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((Mapnik) getApplication()).resetGame();
    }

    private void getUI() {
        playButton = (CircleButton) findViewById(R.id.playButton);
        closeButton = (CircleButton) findViewById(R.id.closeButton);
    }

    private void setupUI() {
        playButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playButton:
                createPlayDialog();
                break;
            case R.id.closeButton:
                finish();
                break;
        }
    }

    private void createPlayDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.play_dialog);

        dialog.show();
    }


}
