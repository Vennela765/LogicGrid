package com.example.logicgrid.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    public static final String DATABASE_NAME = "LogicGrid.db";
    private static final int DATABASE_VERSION = 1; // Keep this at 1 unless you need to upgrade schema

    private static final String TABLE_PLAYERS = "players";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_LEVEL = "level";
    private static final String COLUMN_STARS = "stars";

    private Context context;
    private SQLiteDatabase mDatabase;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            createTables(db);
            Log.d(TAG, "Database created successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error creating database", e);
            throw e;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            Log.i(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
            handleDatabaseUpgrade(db, oldVersion, newVersion);
        } catch (Exception e) {
            Log.e(TAG, "Error upgrading database", e);
            throw e;
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            Log.i(TAG, "Downgrading database from version " + oldVersion + " to " + newVersion);
            handleDatabaseUpgrade(db, oldVersion, newVersion);
        } catch (Exception e) {
            Log.e(TAG, "Error downgrading database", e);
            throw e;
        }
    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
        if (mDatabase != null && mDatabase.isOpen() && !mDatabase.isReadOnly()) {
            return mDatabase;
        }
        try {
            mDatabase = super.getWritableDatabase();
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting writable database", e);
            if (context.deleteDatabase(DATABASE_NAME)) {
                Log.i(TAG, "Deleted corrupted database, trying to recreate.");
                try {
                    mDatabase = super.getWritableDatabase();
                } catch (SQLiteException e2) {
                    Log.e(TAG, "Error creating new database after deletion", e2);
                    throw e2;
                }
            } else {
                Log.e(TAG, "Failed to delete corrupted database.");
                throw e;
            }
        }
        return mDatabase;
    }

    @Override
    public synchronized SQLiteDatabase getReadableDatabase() {
        if (mDatabase != null && mDatabase.isOpen()) {
            return mDatabase;
        }
        try {
            mDatabase = super.getReadableDatabase();
        } catch (SQLiteException e) {
            Log.e(TAG, "Error getting readable database, attempting to get writable to recover.", e);
             // If readable fails, try to open/create writable, which has recovery logic
            try {
                 mDatabase = getWritableDatabase();
            } catch (SQLiteException e2) {
                 Log.e(TAG, "Failed to recover readable database by opening writable.", e2);
                 throw e2; // Or rethrow e if preferred
            }
        }
        return mDatabase;
    }

    @Override
    public synchronized void close() {
        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }
        super.close();
    }

    private void handleDatabaseUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Backup existing data using the provided db instance
        List<Player> players = getAllPlayers(db); // Pass db instance
        
        // Drop and recreate tables using the provided db instance
        dropTables(db);
        createTables(db);
        
        // Restore data using the provided db instance
        for (Player player : players) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, player.getName().trim());
            values.put(COLUMN_LEVEL, player.getCurrentLevel());
            values.put(COLUMN_STARS, player.getStarsEarned());
            db.insert(TABLE_PLAYERS, null, values);
        }
        Log.i(TAG, "Database upgrade/downgrade complete from v" + oldVersion + " to v" + newVersion);
    }

    private void createTables(SQLiteDatabase db) {
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_PLAYERS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT UNIQUE NOT NULL, "
                + COLUMN_LEVEL + " INTEGER DEFAULT 1, "
                + COLUMN_STARS + " INTEGER DEFAULT 0)";
        db.execSQL(createTable);
    }

    private void dropTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYERS);
    }

    public void addPlayer(Player player) {
        if (player == null || player.getName() == null || player.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Player or player name cannot be null or empty");
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, player.getName().trim());
        values.put(COLUMN_LEVEL, player.getCurrentLevel());
        values.put(COLUMN_STARS, player.getStarsEarned());
        
        try {
            long result = db.insertWithOnConflict(TABLE_PLAYERS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            if (result == -1) {
                Log.w(TAG, "Failed to add player (conflict or error): " + player.getName());
                // Optional: query to check if player exists to differentiate conflict from other errors
                // For now, we assume CONFLICT_IGNORE handles unique constraint violations silently
            } else {
                Log.d(TAG, "Player added successfully: " + player.getName());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error adding player", e);
            throw e;
        }
        // Note: We don't close db here as it's managed by getWritableDatabase and close() method of DatabaseHelper
    }

    public Player getPlayerByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be null or empty");
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Player player = null;
        try {
            cursor = db.query(TABLE_PLAYERS,
                    new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_LEVEL, COLUMN_STARS}, // Added COLUMN_ID for completeness
                    COLUMN_NAME + "=?",
                    new String[]{name.trim()},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                player = new Player(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                player.setCurrentLevel(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LEVEL)));
                player.setStarsEarned(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STARS)));
                // player.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))); // If Player class has setId
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting player by name: " + name, e);
            // Not re-throwing, returning null if player not found or error occurs
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return player;
    }

    // Public method that uses the managed mDatabase instance
    public List<Player> getAllPlayers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return getAllPlayers(db);
    }

    // Private helper method that takes a SQLiteDatabase instance
    private List<Player> getAllPlayers(SQLiteDatabase db) {
        List<Player> players = new ArrayList<>();
        Cursor cursor = null;
        try {
            String selectQuery = "SELECT * FROM " + TABLE_PLAYERS +
                " ORDER BY " + COLUMN_LEVEL + " DESC, " + COLUMN_STARS + " DESC";
            cursor = db.rawQuery(selectQuery, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Player player = new Player(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                    );
                    player.setCurrentLevel(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LEVEL)));
                    player.setStarsEarned(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STARS)));
                    // player.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))); // If Player class has setId
                    players.add(player);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting all players", e);
            // Not re-throwing, returning empty list or list collected so far
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return players;
    }

    public void updatePlayer(Player player) {
        if (player == null || player.getName() == null || player.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Player or player name cannot be null or empty");
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LEVEL, player.getCurrentLevel());
        values.put(COLUMN_STARS, player.getStarsEarned());
        
        try {
            int rowsAffected = db.update(TABLE_PLAYERS, values,
                COLUMN_NAME + "=?", new String[]{player.getName().trim()});
                
            if (rowsAffected == 0) {
                Log.w(TAG, "No player found to update: " + player.getName());
            } else {
                Log.d(TAG, "Player updated successfully: " + player.getName());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating player", e);
            throw e;
        }
    }

    public void updatePlayerLevel(String playerName, int newLevel) {
        if (playerName == null || playerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Player name cannot be null or empty");
        }
        if (newLevel < 1) {
            throw new IllegalArgumentException("Level must be greater than 0");
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LEVEL, newLevel);
        
        try {
            int rowsAffected = db.update(TABLE_PLAYERS, values,
                COLUMN_NAME + "=?", new String[]{playerName.trim()});
                
            if (rowsAffected == 0) {
                 Log.w(TAG, "No player found to update level: " + playerName);
            } else {
                Log.d(TAG, "Player level updated successfully: " + playerName + " to level " + newLevel);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating player level", e);
            throw e;
        }
    }

    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
} 