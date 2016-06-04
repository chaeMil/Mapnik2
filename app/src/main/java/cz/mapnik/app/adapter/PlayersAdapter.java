package cz.mapnik.app.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

import at.markushi.ui.CircleButton;
import cz.mapnik.app.Mapnik;
import cz.mapnik.app.R;
import cz.mapnik.app.activity.MainActivity;
import cz.mapnik.app.model.Player;

/**
 * Created by chaemil on 3.2.16.
 */
public class PlayersAdapter extends ArrayAdapter<Player> {

    private final MainActivity mainActivity;
    private final Mapnik app;
    private final int layout;
    private final Context context;

    public PlayersAdapter(Context context, int layout, ArrayList<Player> players, Mapnik app, MainActivity mainActivity) {
        super(context, layout, players);
        this.mainActivity = mainActivity;
        this.app = app;
        this.layout = layout;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Player player = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(layout, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.name);
        ImageView avatar = (ImageView) convertView.findViewById(R.id.avatar);

        if (convertView.findViewById(R.id.removePlayer) != null) {
            CircleButton remove = (CircleButton) convertView.findViewById(R.id.removePlayer);

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    app.removePlayer(player, mainActivity);
                }
            });
        }

        if (convertView.findViewById(R.id.score) != null) {
            TextView score = (TextView) convertView.findViewById(R.id.score);

            Typeface myTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/RobotoSlab-Regular.ttf");
            score.setTypeface(myTypeface);

            score.setText(String.valueOf(player.getScore()));
        }

        name.setText(player.getName());

        String prefix = "android.resource://" + getContext().getPackageName() + "/";
        Ion.with(getContext())
                .load(prefix + getContext().getResources()
                        .getIdentifier(player.getAvatar(), "drawable", getContext().getPackageName()))
                .intoImageView(avatar);

        return convertView;
    }
}
