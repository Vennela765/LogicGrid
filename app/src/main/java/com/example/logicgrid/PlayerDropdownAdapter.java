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
        View view = createItemView(position, convertView, parent, true);
        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = createItemView(position, convertView, parent, false);
        // Ensure delete button is visible in dropdown
        TextView deleteButton = view.findViewById(R.id.deleteButton);
        deleteButton.setVisibility(View.VISIBLE);
        return view;
    }

    private View createItemView(int position, View convertView, ViewGroup parent, boolean isMainView) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.player_dropdown_item, parent, false);
        }

        Player player = players.get(position);
        TextView nameText = view.findViewById(R.id.playerNameText);
        TextView deleteButton = view.findViewById(R.id.deleteButton);

        nameText.setText(player.getName());
        
        // Hide delete button in main view, show in dropdown
        deleteButton.setVisibility(isMainView ? View.GONE : View.VISIBLE);
        
        // Make the whole item clickable for selection, except the delete button
        nameText.setOnClickListener(v -> {
            if (selectListener != null) {
                selectListener.onPlayerSelect(player);
            }
        });
        
        // Handle delete button click
        deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                v.setPressed(true);  // Show visual feedback
                deleteListener.onPlayerDelete(player);
            }
        });

        return view;
    }
} 