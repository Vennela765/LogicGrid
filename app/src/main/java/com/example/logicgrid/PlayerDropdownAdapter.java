package com.example.logicgrid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.logicgrid.data.Player;
import java.util.List;

public class PlayerDropdownAdapter extends ArrayAdapter<Player> {
    private final LayoutInflater inflater;
    private final List<Player> players;
    private final OnPlayerDeleteListener deleteListener;

    public interface OnPlayerDeleteListener {
        void onPlayerDelete(Player player);
    }

    public PlayerDropdownAdapter(Context context, List<Player> players, OnPlayerDeleteListener listener) {
        super(context, R.layout.player_dropdown_item, players);
        this.inflater = LayoutInflater.from(context);
        this.players = players;
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.player_dropdown_item, parent, false);
        }

        Player player = players.get(position);
        TextView nameText = view.findViewById(R.id.playerNameText);
        TextView deleteButton = view.findViewById(R.id.deleteButton);

        nameText.setText(player.getName());
        
        deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onPlayerDelete(player);
            }
        });

        return view;
    }
} 