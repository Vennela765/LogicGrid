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
    private final OnPlayerSelectListener selectListener;

    public interface OnPlayerDeleteListener {
        void onPlayerDelete(Player player);
    }

    public interface OnPlayerSelectListener {
        void onPlayerSelect(Player player);
    }

    public PlayerDropdownAdapter(Context context, List<Player> players, 
                               OnPlayerDeleteListener deleteListener,
                               OnPlayerSelectListener selectListener) {
        super(context, R.layout.player_dropdown_item, players);
        this.inflater = LayoutInflater.from(context);
        this.players = players;
        this.deleteListener = deleteListener;
        this.selectListener = selectListener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = createItemView(position, convertView, parent);
        // For the selected view (main input field), only show the player name
        TextView nameText = view.findViewById(R.id.playerNameText);
        Player player = players.get(position);
        nameText.setText(player.getName());
        // Hide delete button in main view
        view.findViewById(R.id.deleteButton).setVisibility(View.GONE);
        return view;
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
        
        // Setup click listeners
        nameText.setOnClickListener(v -> {
            if (selectListener != null) {
                selectListener.onPlayerSelect(player);
            }
        });
        
        deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onPlayerDelete(player);
            }
        });

        return view;
    }
} 