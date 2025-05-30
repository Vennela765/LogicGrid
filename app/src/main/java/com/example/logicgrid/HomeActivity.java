package com.example.logicgrid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.example.logicgrid.data.DatabaseHelper;
import com.example.logicgrid.data.Player;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements PlayersAdapter.OnPlayerClickListener {
    private DrawerLayout drawerLayout;
    private CardView leaderboardOverlay;
    private ImageButton fingerprintButton;
    private ImageButton closeLeaderboardButton;
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
        // Setup instruction button
        ImageButton instructionsButton = findViewById(R.id.instructionsButton);
        instructionsButton.setOnClickListener(v -> showHowToPlayDialog());

        // Setup play button
        MaterialButton playButton = findViewById(R.id.playButton);
        playButton.setOnClickListener(v -> showPlayerNameDialog());

        // Setup leaderboard
        leaderboardOverlay = findViewById(R.id.leaderboardOverlay);
        fingerprintButton = findViewById(R.id.fingerprintButton);
        closeLeaderboardButton = findViewById(R.id.closeLeaderboardButton);
        playersRecyclerView = findViewById(R.id.playersRecyclerView);

        playersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        fingerprintButton.setOnClickListener(v -> showLeaderboard());
        closeLeaderboardButton.setOnClickListener(v -> hideLeaderboard());

        updatePlayersList();
    }

    private void showPlayerNameDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_player_name, null);
        TextInputLayout textInputLayout = dialogView.findViewById(R.id.playerNameInput);
        EditText playerNameEditText = textInputLayout.getEditText();

        new MaterialAlertDialogBuilder(this)
            .setTitle(R.string.enter_name_prompt)
            .setView(dialogView)
            .setPositiveButton(R.string.start_game, (dialog, which) -> {
                String playerName = playerNameEditText != null ? playerNameEditText.getText().toString().trim() : "";
                if (!playerName.isEmpty()) {
                    Player player = dbHelper.getPlayerByName(playerName);
                    if (player == null) {
                        // Create new player
                        player = new Player(playerName);
                        dbHelper.addPlayer(player);
                    }
                    startLevelSelect(playerName);
                } else {
                    Toast.makeText(this, R.string.invalid_name, Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton(android.R.string.cancel, null)
            .show();
    }

    private void startLevelSelect(String playerName) {
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
        players.sort((p1, p2) -> p2.getCurrentLevel() - p1.getCurrentLevel());
        PlayersAdapter adapter = new PlayersAdapter(players, this);
        playersRecyclerView.setAdapter(adapter);
    }

    private void showHowToPlayDialog() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.how_to_play)
            .setMessage(R.string.how_to_play_instructions)
            .setPositiveButton(android.R.string.ok, null)
            .show();
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
        startLevelSelect(player.getName());
    }
} 