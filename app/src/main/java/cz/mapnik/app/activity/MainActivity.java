package cz.mapnik.app.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.koushikdutta.ion.Ion;

import java.util.Map;

import at.markushi.ui.CircleButton;
import cz.mapnik.app.Mapnik;
import cz.mapnik.app.R;
import cz.mapnik.app.adapter.PlayersAdapter;
import cz.mapnik.app.model.Player;
import cz.mapnik.app.utils.SmartLog;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private CircleButton playButton;
    private CircleButton closeButton;
    private CircleButton singleplayerButton;
    private CircleButton multiplayerButton;
    private CircleButton addPlayerButton;
    private ImageView avatar_m_cyan;
    private ImageView avatar_m_green;
    private ImageView avatar_m_grey;
    private ImageView avatar_m_red;
    private ImageView avatar_m_yellow;
    private ImageView avatar_m_yellow2;
    private ImageView avatar_w_cyan;
    private ImageView avatar_w_cyan2;
    private ImageView avatar_w_green;
    private ImageView avatar_w_purple;
    private ImageView avatar_w_purple2;
    private ImageView avatar_w_red;
    private String avatar;
    private CircleButton createPlayerButton;
    private AppCompatEditText playerNameEditText;
    private GridLayout avatars;
    private Dialog createPlayerDialog;
    private ListView playersList;
    private PlayersAdapter playersAdapter;

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
        playersAdapter = new PlayersAdapter(this, ((Mapnik) getApplication()).getPlayers());
    }

    @Override
    public void onClick(View v) {

        if (v.getTag() != null) {
            SmartLog.Log(SmartLog.LogLevel.DEBUG, "avatar", v.getTag().toString());
            avatar = v.getTag().toString();
            resetAvatarsAlpha(0.5f);
            v.setAlpha(1.0f);
        }

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
            case R.id.createPlayerButton:
                int cont = 0;
                if (playerNameEditText.getText().length() >= 3) {
                    cont += 1;
                } else {
                    playerNameEditText.setError("");
                }
                if (avatar != null) {
                    cont += 1;
                } else {
                    YoYo.with(Techniques.Pulse).duration(150).playOn(avatars);
                }
                if (cont >= 2) {
                    addPlayer(new Player(playerNameEditText.getText().toString(), avatar));
                    createPlayerDialog.dismiss();
                    SmartLog.Log(SmartLog.LogLevel.DEBUG, "players", String.valueOf(((Mapnik) getApplication()).getPlayers().size()));
                }
                break;
        }
    }

    private void addPlayer(Player player) {

        ((Mapnik) getApplication()).addPlayer(player);
        playersAdapter.notifyDataSetChanged();
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
        createPlayerDialog = new Dialog(this);
        createPlayerDialog.setContentView(R.layout.player_dialog);

        avatar_m_cyan = (ImageView) createPlayerDialog.findViewById(R.id.avatar_m_cyan);
        avatar_m_green = (ImageView) createPlayerDialog.findViewById(R.id.avatar_m_green);
        avatar_m_grey = (ImageView) createPlayerDialog.findViewById(R.id.avatar_m_grey);
        avatar_m_red = (ImageView) createPlayerDialog.findViewById(R.id.avatar_m_red);
        avatar_m_yellow = (ImageView) createPlayerDialog.findViewById(R.id.avatar_m_yellow);
        avatar_m_yellow2 = (ImageView) createPlayerDialog.findViewById(R.id.avatar_m_yellow2);
        avatar_w_cyan = (ImageView) createPlayerDialog.findViewById(R.id.avatar_w_cyan);
        avatar_w_cyan2 = (ImageView) createPlayerDialog.findViewById(R.id.avatar_w_cyan2);
        avatar_w_green = (ImageView) createPlayerDialog.findViewById(R.id.avatar_w_green);
        avatar_w_purple = (ImageView) createPlayerDialog.findViewById(R.id.avatar_w_purple);
        avatar_w_purple2 = (ImageView) createPlayerDialog.findViewById(R.id.avatar_w_purple2);
        avatar_w_red = (ImageView) createPlayerDialog.findViewById(R.id.avatar_w_red);

        avatar_m_cyan.setOnClickListener(this);
        avatar_m_green.setOnClickListener(this);
        avatar_m_grey.setOnClickListener(this);
        avatar_m_red.setOnClickListener(this);
        avatar_m_yellow.setOnClickListener(this);
        avatar_m_yellow2.setOnClickListener(this);
        avatar_w_cyan.setOnClickListener(this);
        avatar_w_cyan2.setOnClickListener(this);
        avatar_w_green.setOnClickListener(this);
        avatar_w_purple.setOnClickListener(this);
        avatar_w_purple2.setOnClickListener(this);
        avatar_w_red.setOnClickListener(this);

        String prefix = "android.resource://" + getPackageName() + "/";
        Ion.with(this).load(prefix + R.drawable.avatar_m_cyan).intoImageView(avatar_m_cyan);
        Ion.with(this).load(prefix + R.drawable.avatar_m_green).intoImageView(avatar_m_green);
        Ion.with(this).load(prefix + R.drawable.avatar_m_grey).intoImageView(avatar_m_grey);
        Ion.with(this).load(prefix + R.drawable.avatar_m_red).intoImageView(avatar_m_red);
        Ion.with(this).load(prefix + R.drawable.avatar_m_yellow).intoImageView(avatar_m_yellow);
        Ion.with(this).load(prefix + R.drawable.avatar_m_yellow2).intoImageView(avatar_m_yellow2);
        Ion.with(this).load(prefix + R.drawable.avatar_w_cyan).intoImageView(avatar_w_cyan);
        Ion.with(this).load(prefix + R.drawable.avatar_w_cyan2).intoImageView(avatar_w_cyan2);
        Ion.with(this).load(prefix + R.drawable.avatar_w_green).intoImageView(avatar_w_green);
        Ion.with(this).load(prefix + R.drawable.avatar_w_purple).intoImageView(avatar_w_purple);
        Ion.with(this).load(prefix + R.drawable.avatar_w_purple2).intoImageView(avatar_w_purple2);
        Ion.with(this).load(prefix + R.drawable.avatar_w_red).intoImageView(avatar_w_red);

        createPlayerButton = (CircleButton) createPlayerDialog.findViewById(R.id.createPlayerButton);
        playerNameEditText = (AppCompatEditText) createPlayerDialog.findViewById(R.id.playerNameEditText);
        avatars = (GridLayout) createPlayerDialog.findViewById(R.id.avatars);

        createPlayerButton.setOnClickListener(this);
        playerNameEditText.setOnClickListener(this);

        resetAvatarsAlpha(0.5f);
        avatar = null;

        createPlayerDialog.show();
    }

    private void createPlayersDialog(boolean multiplayer) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.players_dialog);

        addPlayerButton = (CircleButton) dialog.findViewById(R.id.addPlayerButton);
        playersList = (ListView) dialog.findViewById(R.id.playersList);
        playersList.setAdapter(playersAdapter);

        if (multiplayer) {
            addPlayerButton.setVisibility(View.VISIBLE);
            addPlayerButton.setOnClickListener(this);
        }

        dialog.show();
    }

    private void resetAvatarsAlpha(float alpha) {
        avatar_m_cyan.setAlpha(alpha);
        avatar_m_green.setAlpha(alpha);
        avatar_m_grey.setAlpha(alpha);
        avatar_m_red.setAlpha(alpha);
        avatar_m_yellow.setAlpha(alpha);
        avatar_m_yellow2.setAlpha(alpha);
        avatar_w_cyan.setAlpha(alpha);
        avatar_w_cyan2.setAlpha(alpha);
        avatar_w_green.setAlpha(alpha);
        avatar_w_purple.setAlpha(alpha);
        avatar_w_purple2.setAlpha(alpha);
        avatar_w_red.setAlpha(alpha);
    }


}
