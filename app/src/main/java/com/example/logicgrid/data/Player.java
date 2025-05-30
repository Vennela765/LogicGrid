package com.example.logicgrid.data;

public class Player {
    private int id;
    private String name;
    private int currentLevel;
    private int starsEarned;
    private String lastPlayedDate;

    public Player() {
    }

    public Player(String name) {
        this.name = name;
        this.currentLevel = 1;
        this.starsEarned = 0;
    }

    public Player(int id, String name, int currentLevel, int starsEarned, String lastPlayedDate) {
        this.id = id;
        this.name = name;
        this.currentLevel = currentLevel;
        this.starsEarned = starsEarned;
        this.lastPlayedDate = lastPlayedDate;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public int getStarsEarned() {
        return starsEarned;
    }

    public void setStarsEarned(int starsEarned) {
        this.starsEarned = starsEarned;
    }

    public String getLastPlayedDate() {
        return lastPlayedDate;
    }

    public void setLastPlayedDate(String lastPlayedDate) {
        this.lastPlayedDate = lastPlayedDate;
    }
} 