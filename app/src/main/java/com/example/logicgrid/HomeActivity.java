package com.example.logicgrid;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.example.logicgrid.data.DatabaseHelper;
import com.example.logicgrid.data.Player;
import java.util.List;
import android.widget.TextView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import java.util.Locale;
import android.widget.EditText;
import androidx.activity.OnBackPressedCallback;

public class HomeActivity extends AppCompatActivity implements PlayersAdapter.OnPlayerClickListener {
    private CardView leaderboardOverlay;
    private RecyclerView playersRecyclerView;
    private DatabaseHelper dbHelper;
    private PlayerDropdownAdapter dropdownAdapter;
    private PlayersAdapter playersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbHelper = new DatabaseHelper(this);
        setupViews();
        
        // Setup back press handling
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (leaderboardOverlay.getVisibility() == View.VISIBLE) {
                    hideLeaderboard();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    private void setupViews() {
        // Initialize buttons
        MaterialButton playButton = findViewById(R.id.playButton);
        MaterialButton newPlayerButton = findViewById(R.id.newPlayerButton);
        MaterialButton existingPlayerButton = findViewById(R.id.existingPlayerButton);
        ImageButton instructionsButton = findViewById(R.id.instructionsButton);

        if (playButton != null) {
            playButton.setVisibility(View.GONE);
        }

        // Set click listeners
        if (newPlayerButton != null) {
            newPlayerButton.setOnClickListener(v -> showNewPlayerDialog());
        }
        if (existingPlayerButton != null) {
            existingPlayerButton.setOnClickListener(v -> showExistingPlayerDialog());
        }
        if (instructionsButton != null) {
            instructionsButton.setOnClickListener(v -> showInstructionsDialog());
        }

        // Setup leaderboard
        leaderboardOverlay = findViewById(R.id.leaderboardOverlay);
        ImageButton fingerprintButton = findViewById(R.id.fingerprintButton);
        ImageButton closeLeaderboardButton = findViewById(R.id.closeLeaderboardButton);
        playersRecyclerView = findViewById(R.id.playersRecyclerView);

        if (playersRecyclerView != null) {
            playersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        if (fingerprintButton != null) {
            fingerprintButton.setOnClickListener(v -> showLeaderboard());
        }
        if (closeLeaderboardButton != null) {
            closeLeaderboardButton.setOnClickListener(v -> hideLeaderboard());
        }

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
        List<Player> players = dbHelper.getAllPlayers();
        
        if (players == null || players.isEmpty()) {
            new AlertDialog.Builder(this)
                .setTitle(R.string.no_players_title)
                .setMessage(R.string.no_players_message)
                .setPositiveButton(R.string.create_new_player, (dialog, which) -> showNewPlayerDialog())
                .setNegativeButton(R.string.cancel, null)
                .show();
            return;
        }

        try {
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_player_name, null, false);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setView(dialogView)
                    .create();

            // Remove the default title
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.show();

            // Get references to views with null checks
            TextView welcomeTitle = dialogView.findViewById(R.id.welcomeTitle);
            TextView readySubtitle = dialogView.findViewById(R.id.readySubtitle);
            MaterialAutoCompleteTextView nameInput = dialogView.findViewById(R.id.nameInput);
            MaterialButton notYetButton = dialogView.findViewById(R.id.notYetButton);
            MaterialButton letsGoButton = dialogView.findViewById(R.id.letsGoButton);
            TextView gamesPlayedText = dialogView.findViewById(R.id.gamesPlayedText);
            TextView winRateText = dialogView.findViewById(R.id.winRateText);
            TextView diceIcon = dialogView.findViewById(R.id.diceIcon);

            if (nameInput == null || notYetButton == null || letsGoButton == null) {
                dialog.dismiss();
                Toast.makeText(this, R.string.error_loading_dialog, Toast.LENGTH_SHORT).show();
                return;
            }

            // Sort players by highest level
            players.sort((p1, p2) -> p2.getHighestLevel() - p1.getHighestLevel());

            // Create the final adapter with delete functionality
            final PlayerDropdownAdapter adapter = new PlayerDropdownAdapter(
                this,
                players,
                player -> {
                    // Show confirmation dialog
                    new AlertDialog.Builder(this)
                        .setTitle(R.string.delete_player_title)
                        .setMessage(String.format(Locale.getDefault(), getString(R.string.delete_player_message), player.getName()))
                        .setPositiveButton(R.string.delete, (deleteDialog, which) -> {
                            // Delete player from database
                            dbHelper.deletePlayer(player.getName());
                            
                            // Remove from list
                            players.remove(player);
                            
                            // Update adapters
                            if (dropdownAdapter != null) {
                                dropdownAdapter.notifyDataSetChanged();
                            }
                            if (playersAdapter != null) {
                                playersAdapter.updatePlayers(players);
                                playersAdapter.notifyDataSetChanged();
                            }
                            
                            // Clear selection and reset stats
                            nameInput.setText("", false);
                            updatePlayerStats(null, diceIcon, gamesPlayedText, winRateText);
                            
                            // Show toast
                            Toast.makeText(this, R.string.player_deleted, Toast.LENGTH_SHORT).show();
                            
                            // If no players left, close dialog and show new player dialog
                            if (players.isEmpty()) {
                                dialog.dismiss();
                                showNewPlayerDialog();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .show();
                }
            );
            
            nameInput.setAdapter(adapter);
            
            // Clear the initial text
            nameInput.setText("", false);
            
            // Set initial selection and stats
            if (!players.isEmpty()) {
                updatePlayerStats(null, diceIcon, gamesPlayedText, winRateText);
            }

            // Customize dialog text
            if (welcomeTitle != null) {
                welcomeTitle.setText(String.format(Locale.getDefault(), getString(R.string.welcome_back), nameInput.getText().toString()));
            }
            if (readySubtitle != null) {
                readySubtitle.setText(R.string.ready_subtitle);
            }

            // Set click listeners
            notYetButton.setOnClickListener(v -> dialog.dismiss());

            nameInput.setOnItemClickListener((parent, view, position, id) -> {
                Player selectedPlayer = players.get(position);
                updatePlayerStats(selectedPlayer, diceIcon, gamesPlayedText, winRateText);
            });

            letsGoButton.setOnClickListener(v -> {
                String playerName = nameInput.getText().toString().trim();
                Player selectedPlayer = dbHelper.getPlayerByName(playerName);
                if (selectedPlayer != null) {
                    startGame(playerName);
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, R.string.select_valid_profile, Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, R.string.error_player_selection, Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePlayerStats(Player player, TextView diceIcon, TextView gamesPlayedText, TextView winRateText) {
        if (diceIcon != null && gamesPlayedText != null && winRateText != null) {
            if (player != null) {
                int gamesPlayed = player.getGamesPlayed();
                int totalStars = player.getTotalStars();
                
                // Update games played (orange circle)
                gamesPlayedText.setText(String.format(Locale.getDefault(), getString(R.string.games_played), gamesPlayed));
                
                // Update total stars (blue circle)
                winRateText.setText(String.format(Locale.getDefault(), getString(R.string.win_rate), totalStars));
            } else {
                // Reset stats when no player is selected
                gamesPlayedText.setText(getString(R.string.games_played, 0));
                winRateText.setText(getString(R.string.win_rate, 0));
            }
        }
    }

    private void startGame(String playerName) {
        Intent intent = new Intent(this, LevelSelectActivity.class);
        intent.putExtra("player_name", playerName);
        startActivity(intent);
    }

    private void showLeaderboard() {
        if (leaderboardOverlay != null) {
            leaderboardOverlay.setVisibility(View.VISIBLE);
            updatePlayersList();
        }
    }

    private void hideLeaderboard() {
        if (leaderboardOverlay != null) {
            leaderboardOverlay.setVisibility(View.GONE);
        }
    }

    private void updatePlayersList() {
        if (playersRecyclerView == null) {
            return;
        }
        
        List<Player> players = dbHelper.getAllPlayers();
        if (players != null) {
            players.sort((p1, p2) -> p2.getHighestLevel() - p1.getHighestLevel());
            if (playersAdapter == null) {
                playersAdapter = new PlayersAdapter(players, this);
                playersRecyclerView.setAdapter(playersAdapter);
            } else {
                playersAdapter.updatePlayers(players);
                playersAdapter.notifyDataSetChanged();
            }
        }
    }

    private void showInstructionsDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(LayoutInflater.from(this).inflate(R.layout.dialog_instructions, null))
                .create();

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();

        MaterialButton closeButton = dialog.findViewById(R.id.closeButton);
        if (closeButton != null) {
            closeButton.setOnClickListener(v -> dialog.dismiss());
        }
    }

    @Override
    public void onPlayerClick(Player player) {
        hideLeaderboard();
        startGame(player.getName());
    }
} 