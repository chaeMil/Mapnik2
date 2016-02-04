package cz.mapnik.app.activity;

import android.os.Bundle;

import cz.mapnik.app.R;

/**
 * Created by chaemil on 2.2.16.
 */
public class GuessActivity extends BaseActivity {

    public static final String GUESSES = "guesses";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess);

    }
}
