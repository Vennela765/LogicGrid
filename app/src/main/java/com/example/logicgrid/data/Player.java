package com.example.logicgrid.data;

public class Player {
    private String name;
    private int easyLevel;
    private int mediumLevel;
    private int hardLevel;
    private int easyStars;
    private int mediumStars;
    private int hardStars;

    public Player(String name) {
        this.name = name;
        this.easyLevel = 1;
        this.mediumLevel = 1;
        this.hardLevel = 1;
        this.easyStars = 0;
        this.mediumStars = 0;
        this.hardStars = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCurrentLevel(String difficulty) {
        switch (difficulty.toUpperCase()) {
            case "EASY":
                return easyLevel;
            case "MEDIUM":
                return mediumLevel;
            case "HARD":
                return hardLevel;
            default:
                return 1;
        }
    }

    public void setCurrentLevel(String difficulty, int level) {
        switch (difficulty.toUpperCase()) {
            case "EASY":
                this.easyLevel = level;
                break;
            case "MEDIUM":
                this.mediumLevel = level;
                break;
            case "HARD":
                this.hardLevel = level;
                break;
        }
    }

    public int getStarsEarned(String difficulty) {
        switch (difficulty.toUpperCase()) {
            case "EASY":
                return easyStars;
            case "MEDIUM":
                return mediumStars;
            case "HARD":
                return hardStars;
            default:
                return 0;
        }
    }

    public void setStarsEarned(String difficulty, int stars) {
        switch (difficulty.toUpperCase()) {
            case "EASY":
                this.easyStars = stars;
                break;
            case "MEDIUM":
                this.mediumStars = stars;
                break;
            case "HARD":
                this.hardStars = stars;
                break;
        }
    }

    // For total stars across all difficulties
    public int getTotalStars() {
        return easyStars + mediumStars + hardStars;
    }

    // For displaying highest level reached
    public int getHighestLevel() {
        return Math.max(Math.max(easyLevel, mediumLevel), hardLevel);
    }
} 