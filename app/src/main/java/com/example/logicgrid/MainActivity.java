package com.example.logicgrid;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.example.logicgrid.data.DatabaseHelper;
import com.example.logicgrid.data.Player;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PlayerAdapter.OnPlayerClickListener {
    private DatabaseHelper dbHelper;
    private TextInputEditText playerNameInput;
    private RecyclerView recentPlayersRecyclerView;
    private PlayerAdapter playerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        
        // Initialize views
        playerNameInput = findViewById(R.id.playerNameInput);
        recentPlayersRecyclerView = findViewById(R.id.recentPlayersRecyclerView);
        
        // Setup RecyclerView
        recentPlayersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        playerAdapter = new PlayerAdapter(this);
        recentPlayersRecyclerView.setAdapter(playerAdapter);
        
        // Load recent players
        loadRecentPlayers();

        // Setup play button
        findViewById(R.id.playButton).setOnClickListener(v -> handleStartGame());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecentPlayers();
    }

    private void loadRecentPlayers() {
        List<Player> players = dbHelper.getAllPlayers();
        playerAdapter.setPlayers(players);
    }

    private void handleStartGame() {
        String playerName = playerNameInput.getText().toString().trim();
        
        if (TextUtils.isEmpty(playerName)) {
            Toast.makeText(this, R.string.enter_name_prompt, Toast.LENGTH_SHORT).show();
            return;
        }

        // Get or create player
        Player player = dbHelper.getPlayerByName(playerName);
        if (player == null) {
            // Create new player
            player = new Player(playerName);
            long id = dbHelper.addPlayer(player);
            player.setId((int) id);
            Toast.makeText(this, getString(R.string.new_player), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.welcome_back, playerName), Toast.LENGTH_SHORT).show();
        }

        // Start the game with this player
        startGame(player);
    }

    @Override
    public void onPlayerClick(Player player) {
        playerNameInput.setText(player.getName());
        handleStartGame();
    }

    private void startGame(Player player) {
        // Save the current player's name to SharedPreferences
        getSharedPreferences("LogicGridPrefs", MODE_PRIVATE)
                .edit()
                .putString("current_player", player.getName())
                .apply();

        // Start LevelSelectActivity
        Intent intent = new Intent(this, LevelSelectActivity.class);
        intent.putExtra("player_name", player.getName());
        intent.putExtra("current_level", player.getCurrentLevel());
        startActivity(intent);
    }
}