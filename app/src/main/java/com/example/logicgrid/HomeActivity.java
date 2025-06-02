package com.example.logicgrid;

import androidx.activity.OnBackPressedCallback;
//import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
//import com.google.android.material.dialog.MaterialAlertDialogBuilder;
//import com.google.android.material.textfield.TextInputLayout;
import com.example.logicgrid.data.DatabaseHelper;
import com.example.logicgrid.data.Player;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements PlayersAdapter.OnPlayerClickListener {
    private DrawerLayout drawerLayout;
    private CardView leaderboardOverlay;
    private RecyclerView playersRecyclerView;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbHelper = new DatabaseHelper(this);
        setupViews();

    }

    private void setupViews() {
        // Initialize buttons
        MaterialButton playButton = findViewById(R.id.playButton);
        MaterialButton newPlayerButton = findViewById(R.id.newPlayerButton);
        MaterialButton existingPlayerButton = findViewById(R.id.existingPlayerButton);

        // Hide the main play button as we're using new/existing player buttons
        playButton.setVisibility(View.GONE);

        // Set click listeners
        newPlayerButton.setOnClickListener(v -> showNewPlayerDialog());
        existingPlayerButton.setOnClickListener(v -> showExistingPlayerDialog());

        // Setup leaderboard
        leaderboardOverlay = findViewById(R.id.leaderboardOverlay);
        ImageButton fingerprintButton = findViewById(R.id.fingerprintButton);
        ImageButton closeLeaderboardButton = findViewById(R.id.closeLeaderboardButton);
        playersRecyclerView = findViewById(R.id.playersRecyclerView);

        playersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        fingerprintButton.setOnClickListener(v -> showLeaderboard());
        closeLeaderboardButton.setOnClickListener(v -> hideLeaderboard());

        updatePlayersList();
    }

    private void showNewPlayerDialog() {
        // Create and show dialog with custom layout
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(LayoutInflater.from(this).inflate(R.layout.dialog_new_player, null))
                .create();

        // Remove the default title
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.show();

        // Get references to views
        EditText nameInput = dialog.findViewById(R.id.nameInput);
        MaterialButton notYetButton = dialog.findViewById(R.id.notYetButton);
        MaterialButton letsGoButton = dialog.findViewById(R.id.letsGoButton);

        // Set click listeners
        notYetButton.setOnClickListener(v -> dialog.dismiss());

        letsGoButton.setOnClickListener(v -> {
            String playerName = nameInput.getText().toString().trim();
            if (playerName.isEmpty()) {
                Toast.makeText(this, "Please enter your name 👋", Toast.LENGTH_SHORT).show();
            } else {
                // Create new player
                Player newPlayer = new Player(playerName);
                dbHelper.addPlayer(newPlayer);
                startGame(playerName);
                dialog.dismiss();
            }
        });
    }

    private void showExistingPlayerDialog() {
        // Get all players from database
        String[] players = dbHelper.getAllPlayerNames();
        
        if (players == null || players.length == 0) {
            new AlertDialog.Builder(this)
                .setTitle("No Existing Players")
                .setMessage("There are no existing players. Would you like to create a new player?")
                .setPositiveButton("Create New Player", (dialog, which) -> showNewPlayerDialog())
                .setNegativeButton("Cancel", null)
                .show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Select Your Profile")
                .setItems(players, (dialog, which) -> {
                    String selectedPlayer = players[which];
                    startGame(selectedPlayer);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void startGame(String playerName) {
        Intent intent = new Intent(this, LevelSelectActivity.class);
        intent.putExtra("player_name", playerName);
        startActivity(intent);
    }

    private void showLeaderboard() {
        leaderboardOverlay.setVisibility(View.VISIBLE);
        updatePlayersList();
    }

    private void hideLeaderboard() {
        leaderboardOverlay.setVisibility(View.GONE);
    }

    private void updatePlayersList() {
        List<Player> players = dbHelper.getAllPlayers();
        players.sort((p1, p2) -> p2.getHighestLevel() - p1.getHighestLevel());
        PlayersAdapter adapter = new PlayersAdapter(players, this);
        playersRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        if (leaderboardOverlay.getVisibility() == View.VISIBLE) {
            hideLeaderboard();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onPlayerClick(Player player) {
        hideLeaderboard();
        startGame(player.getName());
    }
} 