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
    private static final String DATABASE_NAME = "LogicGrid.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_PLAYERS = "players";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_LEVEL = "level";
    private static final String COLUMN_STARS = "stars";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_PLAYERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT UNIQUE, "
                + COLUMN_LEVEL + " INTEGER DEFAULT 1, "
                + COLUMN_STARS + " INTEGER DEFAULT 0)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYERS);
        onCreate(db);
    }

    public void addPlayer(Player player) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, player.getName());
        values.put(COLUMN_LEVEL, player.getCurrentLevel());
        values.put(COLUMN_STARS, player.getStarsEarned());
        db.insert(TABLE_PLAYERS, null, values);
        db.close();
    }

    public Player getPlayerByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PLAYERS,
                new String[]{COLUMN_NAME, COLUMN_LEVEL, COLUMN_STARS},
                COLUMN_NAME + "=?",
                new String[]{name},
                null, null, null);

        Player player = null;
        if (cursor != null && cursor.moveToFirst()) {
            player = new Player(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            player.setCurrentLevel(cursor.getInt(cursor.getColumnIndex(COLUMN_LEVEL)));
            player.setStarsEarned(cursor.getInt(cursor.getColumnIndex(COLUMN_STARS)));
            cursor.close();
        }
        db.close();
        return player;
    }

    public List<Player> getAllPlayers() {
        List<Player> players = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PLAYERS + " ORDER BY " + COLUMN_LEVEL + " DESC, " + COLUMN_STARS + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Player player = new Player(
                    cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                );
                player.setCurrentLevel(cursor.getInt(cursor.getColumnIndex(COLUMN_LEVEL)));
                player.setStarsEarned(cursor.getInt(cursor.getColumnIndex(COLUMN_STARS)));
                players.add(player);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return players;
    }

    public void updatePlayer(Player player) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LEVEL, player.getCurrentLevel());
        values.put(COLUMN_STARS, player.getStarsEarned());
        db.update(TABLE_PLAYERS, values, COLUMN_NAME + "=?", new String[]{player.getName()});
        db.close();
    }

    public void updatePlayerLevel(String playerName, int newLevel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LEVEL, newLevel);
        db.update(TABLE_PLAYERS, values, COLUMN_NAME + "=?", new String[]{playerName});
        db.close();
    }

    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
} 