package com.example.logicgrid;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LevelSelectActivity extends AppCompatActivity {
    private RecyclerView easyLevelsRecyclerView;
    private RecyclerView mediumLevelsRecyclerView;
    private RecyclerView hardLevelsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_select);

        // Initialize RecyclerViews
        easyLevelsRecyclerView = findViewById(R.id.easyLevelsRecyclerView);
        mediumLevelsRecyclerView = findViewById(R.id.mediumLevelsRecyclerView);
        hardLevelsRecyclerView = findViewById(R.id.hardLevelsRecyclerView);

        // Set up RecyclerViews
        setupRecyclerView(easyLevelsRecyclerView, "EASY", 6);
        setupRecyclerView(mediumLevelsRecyclerView, "MEDIUM", 6);
        setupRecyclerView(hardLevelsRecyclerView, "HARD", 6);
    }

    private void setupRecyclerView(RecyclerView recyclerView, String difficulty, int levelCount) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(new LevelAdapter(difficulty, levelCount, (difficulty1, level) -> {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("difficulty", difficulty1);
            intent.putExtra("level", level);
            startActivity(intent);
        }));
    }

    private static class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.LevelViewHolder> {
        private final String difficulty;
        private final int levelCount;
        private final OnLevelSelectedListener listener;

        public LevelAdapter(String difficulty, int levelCount, OnLevelSelectedListener listener) {
            this.difficulty = difficulty;
            this.levelCount = levelCount;
            this.listener = listener;
        }

        @NonNull
        @Override
        public LevelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_level, parent, false);
            return new LevelViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LevelViewHolder holder, int position) {
            int level = position + 1;
            holder.levelNumberText.setText(
                    holder.itemView.getContext().getString(R.string.level_number, level));
            holder.itemView.setOnClickListener(v -> listener.onLevelSelected(difficulty, level));
        }

        @Override
        public int getItemCount() {
            return levelCount;
        }

        static class LevelViewHolder extends RecyclerView.ViewHolder {
            TextView levelNumberText;

            LevelViewHolder(View itemView) {
                super(itemView);
                levelNumberText = itemView.findViewById(R.id.levelNumberText);
            }
        }
    }

    interface OnLevelSelectedListener {
        void onLevelSelected(String difficulty, int level);
    }
} 