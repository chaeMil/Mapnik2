package cz.mapnik.app.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cz.mapnik.app.Mapnik;
import cz.mapnik.app.R;
import cz.mapnik.app.model.Player;

/**
 * Created by chaemil on 3.2.16.
 */
public class SetupGame extends BaseActivity {

    private ArrayList<Player> players;
    private TextView playersText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_game);

        getData();

        getUI();

        setupUI();
    }

    private void getData() {
        players = ((Mapnik) getApplication()).getPlayers();
    }

    private void getUI() {
        playersText = (TextView) findViewById(R.id.players);
    }

    private void setupUI() {
        String playersString = "";
        for(int c = 0; c < players.size(); c++) {
            playersString += players.get(c).getName() + "  ";
        }
        playersText.setText(playersString);
    }
}
