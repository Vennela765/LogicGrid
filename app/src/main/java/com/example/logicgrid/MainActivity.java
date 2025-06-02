package com.example.logicgrid;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.logicgrid.data.DatabaseHelper;
import com.example.logicgrid.data.Player;
import com.google.android.material.button.MaterialButton;
import java.util.List;
import android.app.Dialog;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private RecyclerView recentPlayersRecyclerView;
    private EditText playerNameInput;
    private Button playButton;
    private Button confirmPlayerButton;
    private Button cancelButton;
    private LinearLayout playerSelectionOverlay;
    private LinearLayout newPlayerDialog;
    private ImageButton fingerprintButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        setupToolbar();
        initializeViews();
        setupListeners();
        setupRecentPlayers();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ImageButton menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(v -> showInstructionsDialog());
    }

    private void initializeViews() {
        recentPlayersRecyclerView = findViewById(R.id.recentPlayersRecyclerView);
        playerNameInput = findViewById(R.id.playerNameInput);
        playButton = findViewById(R.id.playButton);
        confirmPlayerButton = findViewById(R.id.confirmPlayerButton);
        cancelButton = findViewById(R.id.cancelButton);
        playerSelectionOverlay = findViewById(R.id.playerSelectionOverlay);
        newPlayerDialog = findViewById(R.id.newPlayerDialog);
        fingerprintButton = findViewById(R.id.fingerprintButton);
    }

    private void setupListeners() {
        fingerprintButton.setOnClickListener(v -> togglePlayerList());
        
        playButton.setOnClickListener(v -> showNewPlayerDialog());

        confirmPlayerButton.setOnClickListener(v -> {
            String playerName = playerNameInput.getText().toString().trim();
            if (!playerName.isEmpty()) {
                createOrSelectPlayer(playerName);
            } else {
                Toast.makeText(this, R.string.invalid_name, Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> hideNewPlayerDialog());
    }

    private void setupRecentPlayers() {
        recentPlayersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        updateRecentPlayersList();
    }

    private void updateRecentPlayersList() {
        List<Player> recentPlayers = dbHelper.getAllPlayers();
        RecentPlayersAdapter adapter = new RecentPlayersAdapter(recentPlayers, this::selectPlayer);
        recentPlayersRecyclerView.setAdapter(adapter);
    }

    private void togglePlayerList() {
        if (playerSelectionOverlay.getVisibility() == View.VISIBLE) {
            playerSelectionOverlay.setVisibility(View.GONE);
        } else {
            playerSelectionOverlay.setVisibility(View.VISIBLE);
            updateRecentPlayersList();
            // Hide new player dialog if it's visible
            newPlayerDialog.setVisibility(View.GONE);
        }
    }

    private void showNewPlayerDialog() {
        newPlayerDialog.setVisibility(View.VISIBLE);
        playerNameInput.setText("");
        // Hide player list if it's visible
        playerSelectionOverlay.setVisibility(View.GONE);
    }

    private void hideNewPlayerDialog() {
        newPlayerDialog.setVisibility(View.GONE);
        playerNameInput.setText("");
    }

    private void selectPlayer(Player player) {
        Intent intent = new Intent(this, LevelSelectActivity.class);
        intent.putExtra("player_name", player.getName());
        startActivity(intent);
    }

    private void createOrSelectPlayer(String playerName) {
        Player player = dbHelper.getPlayerByName(playerName);
        if (player == null) {
            player = new Player(playerName);
            dbHelper.addPlayer(player);
        }
        selectPlayer(player);
        hideNewPlayerDialog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.instructions_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_instructions) {
            showInstructionsDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showInstructionsDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_instructions);

        if (dialog.getWindow() != null) {
            // Make dialog background transparent
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // Set dialog width to match parent with margins
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(params);
        }

        // Set up close button
        MaterialButton closeButton = dialog.findViewById(R.id.closeButton);
        if (closeButton != null) {
            closeButton.setOnClickListener(v -> dialog.dismiss());
        }

        dialog.show();
    }
}