package com.example.logicgrid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.logicgrid.data.Player;
import java.util.List;
import android.graphics.Color;

public class RecentPlayersAdapter extends RecyclerView.Adapter<RecentPlayersAdapter.ViewHolder> {
    private final List<Player> players;
    private final OnPlayerSelectedListener listener;

    public interface OnPlayerSelectedListener {
        void onPlayerSelected(Player player);
    }

    public RecentPlayersAdapter(List<Player> players, OnPlayerSelectedListener listener) {
        this.players = players;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView textView = new TextView(parent.getContext());
        textView.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setPadding(48, 32, 48, 32);
        textView.setTextSize(16);
        textView.setTextColor(Color.parseColor("#212121"));
        return new ViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Player player = players.get(position);
        holder.textView.setText(String.format("%s (Level %d)", 
            player.getName(), player.getHighestLevel()));
        
        // Set background selector for ripple effect
        holder.textView.setBackgroundResource(android.R.drawable.list_selector_background);
        holder.itemView.setOnClickListener(v -> listener.onPlayerSelected(player));
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textView;

        ViewHolder(View view) {
            super(view);
            textView = (TextView) view;
        }
    }
} 