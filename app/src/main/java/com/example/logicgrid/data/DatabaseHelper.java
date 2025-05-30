package com.example.logicgrid.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "LogicGridDB";
    private static final int DATABASE_VERSION = 1;

    // Table name
    private static final String TABLE_PLAYERS = "players";

    // Column names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_CURRENT_LEVEL = "current_level";
    private static final String KEY_STARS_EARNED = "stars_earned";
    private static final String KEY_LAST_PLAYED = "last_played";

    // Create table statement
    private static final String CREATE_TABLE_PLAYERS = "CREATE TABLE " + TABLE_PLAYERS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_NAME + " TEXT UNIQUE,"
            + KEY_CURRENT_LEVEL + " INTEGER,"
            + KEY_STARS_EARNED + " INTEGER,"
            + KEY_LAST_PLAYED + " TEXT"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PLAYERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYERS);
        onCreate(db);
    }

    // Add new player
    public long addPlayer(Player player) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(KEY_NAME, player.getName());
        values.put(KEY_CURRENT_LEVEL, player.getCurrentLevel());
        values.put(KEY_STARS_EARNED, player.getStarsEarned());
        values.put(KEY_LAST_PLAYED, getCurrentDateTime());

        long id = db.insert(TABLE_PLAYERS, null, values);
        db.close();
        return id;
    }

    // Get player by name
    public Player getPlayerByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_PLAYERS,
                new String[]{KEY_ID, KEY_NAME, KEY_CURRENT_LEVEL, KEY_STARS_EARNED, KEY_LAST_PLAYED},
                KEY_NAME + "=?", new String[]{name},
                null, null, null);

        Player player = null;
        if (cursor != null && cursor.moveToFirst()) {
            player = new Player(
                    cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                    cursor.getInt(cursor.getColumnIndex(KEY_CURRENT_LEVEL)),
                    cursor.getInt(cursor.getColumnIndex(KEY_STARS_EARNED)),
                    cursor.getString(cursor.getColumnIndex(KEY_LAST_PLAYED))
            );
            cursor.close();
        }
        db.close();
        return player;
    }

    // Update player progress
    public int updatePlayer(Player player) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_CURRENT_LEVEL, player.getCurrentLevel());
        values.put(KEY_STARS_EARNED, player.getStarsEarned());
        values.put(KEY_LAST_PLAYED, getCurrentDateTime());

        int rowsAffected = db.update(TABLE_PLAYERS,
                values,
                KEY_NAME + "=?",
                new String[]{player.getName()});
        db.close();
        return rowsAffected;
    }

    // Get all players
    public List<Player> getAllPlayers() {
        List<Player> players = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PLAYERS + " ORDER BY " + KEY_LAST_PLAYED + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Player player = new Player(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                        cursor.getInt(cursor.getColumnIndex(KEY_CURRENT_LEVEL)),
                        cursor.getInt(cursor.getColumnIndex(KEY_STARS_EARNED)),
                        cursor.getString(cursor.getColumnIndex(KEY_LAST_PLAYED))
                );
                players.add(player);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return players;
    }

    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
} 