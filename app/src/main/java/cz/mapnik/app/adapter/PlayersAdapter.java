package cz.mapnik.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

import cz.mapnik.app.R;
import cz.mapnik.app.model.Player;

/**
 * Created by chaemil on 3.2.16.
 */
public class PlayersAdapter extends ArrayAdapter<Player> {

    public PlayersAdapter(Context context, ArrayList<Player> players) {
        super(context, 0, players);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Player player = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.player_list, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.name);
        ImageView avatar = (ImageView) convertView.findViewById(R.id.avatar);

        name.setText(player.getName());

        String prefix = "android.resource://" + getContext().getPackageName() + "/";
        Ion.with(getContext())
                .load(prefix + getContext().getResources()
                        .getIdentifier(player.getAvatar(), "drawable", getContext().getPackageName()))
                .intoImageView(avatar);

        return convertView;
    }
}
