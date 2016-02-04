package cz.mapnik.app.activity;

import android.os.Bundle;

import java.util.ArrayList;

import cz.mapnik.app.Mapnik;
import cz.mapnik.app.R;
import cz.mapnik.app.model.Game;
import cz.mapnik.app.model.Guess;

/**
 * Created by chaemil on 2.2.16.
 */
public class GuessActivity extends BaseActivity {


    private ArrayList<Guess> guesses;
    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess);

        game = ((Mapnik) getApplication()).getCurrentGame();
        guesses = game.getGuesses();

    }
}
