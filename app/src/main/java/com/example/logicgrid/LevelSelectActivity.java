package com.example.logicgrid;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import java.util.Random;

public class LevelSelectActivity extends AppCompatActivity {
    private RecyclerView easyLevelsList;
    private RecyclerView mediumLevelsList;
    private RecyclerView hardLevelsList;
    private static final int LEVELS_PER_DIFFICULTY = 100; // Increased to 100 levels per difficulty

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

        setupRecyclerViews();
    }

    private void setupRecyclerViews() {
        easyLevelsList = findViewById(R.id.easyLevelsList);
        mediumLevelsList = findViewById(R.id.mediumLevelsList);
        hardLevelsList = findViewById(R.id.hardLevelsList);

        // Set layout managers with horizontal scrolling
        easyLevelsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mediumLevelsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        hardLevelsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Set adapters with infinite levels
        setupRecyclerView(easyLevelsList, "EASY", LEVELS_PER_DIFFICULTY, R.color.easy_level);
        setupRecyclerView(mediumLevelsList, "MEDIUM", LEVELS_PER_DIFFICULTY, R.color.medium_level);
        setupRecyclerView(hardLevelsList, "HARD", LEVELS_PER_DIFFICULTY, R.color.hard_level);
    }

    private void setupRecyclerView(RecyclerView recyclerView, String difficulty, int levelCount, int colorRes) {
        recyclerView.setAdapter(new LevelAdapter(this, difficulty, levelCount, colorRes, (diff, level) -> {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("difficulty", diff);
            intent.putExtra("level", level);
            // Add random seed based on difficulty and level
            long seed = generateSeed(diff, level);
            intent.putExtra("seed", seed);
            startActivity(intent);
        }));
    }

    private long generateSeed(String difficulty, int level) {
        String seedString = difficulty + "_" + level;
        long seed = 0;
        for (char c : seedString.toCharArray()) {
            seed = 31 * seed + c;
        }
        return seed;
    }

    private static class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.LevelViewHolder> {
        private final String difficulty;
        private final int levelCount;
        private final OnLevelSelectedListener listener;
        private final int buttonColor;
        private final AppCompatActivity activity;

        public interface OnLevelSelectedListener {
            void onLevelSelected(String difficulty, int level);
        }

        LevelAdapter(AppCompatActivity activity, String difficulty, int levelCount, int colorRes, OnLevelSelectedListener listener) {
            this.activity = activity;
            this.difficulty = difficulty;
            this.levelCount = levelCount;
            this.listener = listener;
            this.buttonColor = ContextCompat.getColor(activity, colorRes);
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
            holder.levelButton.setText(String.valueOf(level));
            holder.levelButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(buttonColor));
            holder.levelButton.setOnClickListener(v -> listener.onLevelSelected(difficulty, level));
        }

        @Override
        public int getItemCount() {
            return levelCount;
        }

        static class LevelViewHolder extends RecyclerView.ViewHolder {
            MaterialButton levelButton;

            LevelViewHolder(View itemView) {
                super(itemView);
                levelButton = itemView.findViewById(R.id.levelButton);
            }
        }
    }
} 