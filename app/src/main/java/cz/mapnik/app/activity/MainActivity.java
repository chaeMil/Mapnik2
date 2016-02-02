package cz.mapnik.app.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import java.util.Map;

import at.markushi.ui.CircleButton;
import cz.mapnik.app.Mapnik;
import cz.mapnik.app.R;
import cz.mapnik.app.model.Player;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private CircleButton playButton;
    private CircleButton closeButton;
    private CircleButton singleplayerButton;
    private CircleButton multiplayerButton;
    private CircleButton addPlayerButton;

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
            case R.id.singlePlayerButton:

                break;
            case R.id.multiPlayerButton:
                createPlayersDialog(true);
                break;
            case R.id.addPlayerButton:
                createPlayerDialog();



                break;
        }
    }

    private void addPlayer(Player player) {

        ((Mapnik) getApplication()).addPlayer(player);
    }

    private void createPlayDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.play_dialog);

        singleplayerButton = (CircleButton) dialog.findViewById(R.id.singlePlayerButton);
        multiplayerButton = (CircleButton) dialog.findViewById(R.id.multiPlayerButton);

        singleplayerButton.setOnClickListener(this);
        multiplayerButton.setOnClickListener(this);

        dialog.show();
    }

    private void createPlayerDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.player_dialog);

        dialog.show();
    }

    private void createPlayersDialog(boolean multiplayer) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.players_dialog);

        addPlayerButton = (CircleButton) dialog.findViewById(R.id.addPlayerButton);

        if (multiplayer) {
            addPlayerButton.setVisibility(View.VISIBLE);
            addPlayerButton.setOnClickListener(this);
        }

        dialog.show();
    }


}
