package com.example.logicgrid.data;

public class Player {
    private String name;
    private int currentLevel;
    private int starsEarned;

    public Player(String name) {
        this.name = name;
        this.currentLevel = 1;
        this.starsEarned = 0;
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
} 