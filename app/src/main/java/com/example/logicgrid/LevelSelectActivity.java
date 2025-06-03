package com.example.logicgrid;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import java.util.Random;
import android.widget.ImageView;

import com.example.logicgrid.data.DatabaseHelper;
import com.example.logicgrid.data.Player;

public class LevelSelectActivity extends AppCompatActivity {
    private RecyclerView easyLevelsList;
    private RecyclerView mediumLevelsList;
    private RecyclerView hardLevelsList;
    private static final int LEVELS_PER_DIFFICULTY = 50;
    private static final int COLUMNS = 10;
    private DatabaseHelper dbHelper;
    private Player currentPlayer;
    private String playerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_select);

        // Setup toolbar with back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        dbHelper = new DatabaseHelper(this);
        
        // Get player information
        playerName = getIntent().getStringExtra("player_name");
        if (playerName != null) {
            currentPlayer = dbHelper.getPlayerByName(playerName);
        }

        if (currentPlayer == null) {
            // If somehow we got here without a valid player, go back to home screen
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        setupRecyclerViews();
    }

    private void setupRecyclerViews() {
        easyLevelsList = findViewById(R.id.easyLevelsList);
        mediumLevelsList = findViewById(R.id.mediumLevelsList);
        hardLevelsList = findViewById(R.id.hardLevelsList);

        // Set layout managers with grid layout
        easyLevelsList.setLayoutManager(new GridLayoutManager(this, COLUMNS));
        mediumLevelsList.setLayoutManager(new GridLayoutManager(this, COLUMNS));
        hardLevelsList.setLayoutManager(new GridLayoutManager(this, COLUMNS));

        // Set fixed size to true for better performance
        easyLevelsList.setHasFixedSize(true);
        mediumLevelsList.setHasFixedSize(true);
        hardLevelsList.setHasFixedSize(true);

        setupRecyclerView(easyLevelsList, "EASY", LEVELS_PER_DIFFICULTY, R.color.easy_level);
        setupRecyclerView(mediumLevelsList, "MEDIUM", LEVELS_PER_DIFFICULTY, R.color.medium_level);
        setupRecyclerView(hardLevelsList, "HARD", LEVELS_PER_DIFFICULTY, R.color.hard_level);
    }

    private void setupRecyclerView(RecyclerView recyclerView, String difficulty, int levelCount, int colorRes) {
        LevelAdapter adapter = new LevelAdapter(this, difficulty, levelCount, colorRes, (diff, level) -> {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("difficulty", diff);
            intent.putExtra("level", level);
            intent.putExtra("player_name", playerName);
            long seed = generateSeed(diff, level);
            intent.putExtra("seed", seed);
            startActivity(intent);
        }, currentPlayer);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private long generateSeed(String difficulty, int level) {
        String seedString = difficulty + "_" + level;
        long seed = 0;
        for (char c : seedString.toCharArray()) {
            seed = 31L * seed + c;
        }
        return seed;
    }

    private static class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.LevelViewHolder> {
        private final String difficulty;
        private final int levelCount;
        private final OnLevelSelectedListener listener;
        private final int buttonColor;
        private final AppCompatActivity activity;
        private final Player currentPlayer;

        public interface OnLevelSelectedListener {
            void onLevelSelected(String difficulty, int level);
        }

        LevelAdapter(AppCompatActivity activity, String difficulty, int levelCount, int colorRes, OnLevelSelectedListener listener, Player player) {
            this.activity = activity;
            this.difficulty = difficulty;
            this.levelCount = levelCount;
            this.listener = listener;
            this.buttonColor = ContextCompat.getColor(activity, colorRes);
            this.currentPlayer = player;
        }

        @Override
        public LevelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_level, parent, false);
            return new LevelViewHolder(view);
        }

        @Override
        public void onBindViewHolder(LevelViewHolder holder, int position) {
            int level = position + 1;
            // Format level number to show all numbers in a single line
            String levelText = String.valueOf(level);
            
            // Adjust text size based on number length
            if (level >= 100) {
                holder.levelButton.setTextSize(12); // Smaller text for 3 digits
            } else if (level >= 10) {
                holder.levelButton.setTextSize(14); // Medium text for 2 digits
            } else {
                holder.levelButton.setTextSize(16); // Larger text for 1 digit
            }
            
            holder.levelButton.setText(levelText);
            holder.levelButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(buttonColor));
            
            // Show star for completed levels in this difficulty
            if (level < currentPlayer.getCurrentLevel(difficulty)) {
                holder.starIcon.setVisibility(View.VISIBLE);
            } else {
                holder.starIcon.setVisibility(View.GONE);
            }
            
            // Enable/disable button based on level availability for this difficulty
            holder.levelButton.setEnabled(level <= currentPlayer.getCurrentLevel(difficulty));
            
            // Set click listener
            holder.levelButton.setOnClickListener(v -> listener.onLevelSelected(difficulty, level));
        }

        @Override
        public int getItemCount() {
            return levelCount;
        }

        static class LevelViewHolder extends RecyclerView.ViewHolder {
            MaterialButton levelButton;
            ImageView starIcon;

            LevelViewHolder(View itemView) {
                super(itemView);
                levelButton = itemView.findViewById(R.id.levelButton);
                starIcon = itemView.findViewById(R.id.starIcon);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh player data and UI when returning to this screen
        if (playerName != null) {
            currentPlayer = dbHelper.getPlayerByName(playerName);
            setupRecyclerViews(); // Refresh the level displays
        }
    }
} 