package com.example.logicgrid;

public class GameLogic {
    public static final int EMPTY = 0;
    public static final int YES = 1;
    public static final int NO = 2;
    
    private final int size;
    private final int[][] grid;
    private final String[][] categories;
    private final String[] clues;
    private final int[][] solution;

    public GameLogic(int size, String[][] categories, String[] clues, int[][] solution) {
        this.size = size;
        this.grid = new int[size][size];
        this.categories = categories;
        this.clues = clues;
        this.solution = solution;
    }

    public boolean toggleCell(int row, int col) {
        grid[row][col] = (grid[row][col] + 1) % 3;
        return validateMove(row, col);
    }

    private boolean validateMove(int row, int col) {
        // Rule 1: Each row must have exactly one YES
        int yesCount = 0;
        for (int j = 0; j < size; j++) {
            if (grid[row][j] == YES) yesCount++;
        }
        if (yesCount > 1) return false;

        // Rule 2: Each column must have exactly one YES
        yesCount = 0;
        for (int i = 0; i < size; i++) {
            if (grid[i][col] == YES) yesCount++;
        }
        if (yesCount > 1) return false;

        // Rule 3: If a cell is marked YES, corresponding cells must be NO
        if (grid[row][col] == YES) {
            for (int i = 0; i < size; i++) {
                if (i != row && grid[i][col] == YES) return false;
                if (i != col && grid[row][i] == YES) return false;
            }
        }

        return true;
    }

    public boolean checkSolution() {
        // Check if the current grid matches the solution
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (solution[i][j] == YES && grid[i][j] != YES) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isComplete() {
        // Check if all cells are filled
        int yesCount = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] == YES) yesCount++;
            }
        }
        return yesCount == size;
    }

    public void clearGrid() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = EMPTY;
            }
        }
    }

    public String[] getClues() {
        return clues;
    }

    public String[][] getCategories() {
        return categories;
    }

    public int getCellState(int row, int col) {
        return grid[row][col];
    }

    public static class PuzzleData {
        public final String[][] categories;
        public final String[] clues;
        public final int[][] solution;

        public PuzzleData(String[][] categories, String[] clues, int[][] solution) {
            this.categories = categories;
            this.clues = clues;
            this.solution = solution;
        }
    }

    public static int getDifficultySize(String difficulty) {
        switch (difficulty) {
            case "EASY":
                return 3;
            case "MEDIUM":
                return 4;
            case "HARD":
                return 5;
            default:
                return 3;
        }
    }

    public static PuzzleData generatePuzzle(String difficulty, int level) {
        int size = getDifficultySize(difficulty);
        
        // EASY LEVELS - Focus on direct relationships and simple deductions
        if (difficulty.equals("EASY")) {
            switch (level) {
                case 1:
                    return new PuzzleData(
                        new String[][] {
                            {"Bird", "Cat", "Dog"},
                            {"Brown", "White", "Black"}
                        },
                        new String[] {
                            "The Bird is not Brown",
                            "The Cat is White",
                            "The Dog is darker than the other pets"
                        },
                        new int[][] {
                            {0, 0, 1},  // Bird - Black
                            {0, 1, 0},  // Cat - White
                            {1, 0, 0}   // Dog - Brown
                        }
                    );
                case 2:
                    return new PuzzleData(
                        new String[][] {
                            {"Pizza", "Burger", "Salad"},
                            {"Monday", "Tuesday", "Wednesday"}
                        },
                        new String[] {
                            "Pizza was ordered after Burger",
                            "Salad was not ordered on Wednesday",
                            "The Tuesday order was not Pizza"
                        },
                        new int[][] {
                            {0, 0, 1},  // Pizza - Wednesday
                            {1, 0, 0},  // Burger - Monday
                            {0, 1, 0}   // Salad - Tuesday
                        }
                    );
                case 3:
                    return new PuzzleData(
                        new String[][] {
                            {"Red", "Blue", "Green"},
                            {"Car", "Bike", "Bus"}
                        },
                        new String[] {
                            "The Blue vehicle has two wheels",
                            "The Car is not Green",
                            "The Red vehicle is larger than the Blue one"
                        },
                        new int[][] {
                            {0, 0, 1},  // Red - Bus
                            {0, 1, 0},  // Blue - Bike
                            {1, 0, 0}   // Green - Car
                        }
                    );
                case 4:
                    return new PuzzleData(
                        new String[][] {
                            {"Alice", "Bob", "Carol"},
                            {"Apple", "Banana", "Cherry"}
                        },
                        new String[] {
                            "Alice's fruit is red",
                            "Bob's fruit is yellow",
                            "Carol's fruit starts with the same letter as her name"
                        },
                        new int[][] {
                            {1, 0, 0},  // Alice - Apple
                            {0, 1, 0},  // Bob - Banana
                            {0, 0, 1}   // Carol - Cherry
                        }
                    );
                case 5:
                    return new PuzzleData(
                        new String[][] {
                            {"Math", "Science", "Art"},
                            {"9AM", "10AM", "11AM"}
                        },
                        new String[] {
                            "Math is not the last class",
                            "Science is after Art",
                            "The 10AM class involves numbers"
                        },
                        new int[][] {
                            {0, 1, 0},  // Math - 10AM
                            {0, 0, 1},  // Science - 11AM
                            {1, 0, 0}   // Art - 9AM
                        }
                    );
                case 6:
                    return new PuzzleData(
                        new String[][] {
                            {"Hat", "Scarf", "Gloves"},
                            {"Red", "Blue", "Green"}
                        },
                        new String[] {
                            "The Hat matches the sky",
                            "The Gloves are not Green",
                            "The Red item goes around your neck"
                        },
                        new int[][] {
                            {0, 1, 0},  // Hat - Blue
                            {1, 0, 0},  // Scarf - Red
                            {0, 0, 1}   // Gloves - Green
                        }
                    );
            }
        }
        
        // MEDIUM LEVELS - 4x4 grid with conditional logic and multiple-step deductions
        if (difficulty.equals("MEDIUM")) {
            switch (level) {
                case 1:
                    return new PuzzleData(
                        new String[][] {
                            {"Math", "Science", "History", "Art"},
                            {"Monday", "Tuesday", "Wednesday", "Thursday"}
                        },
                        new String[] {
                            "Math is not on Thursday",
                            "Science is taught after History",
                            "Art is not on consecutive days with Math",
                            "History is not on Wednesday",
                            "The Tuesday class involves calculations"
                        },
                        new int[][] {
                            {0, 1, 0, 0},  // Math - Tuesday
                            {0, 0, 1, 0},  // Science - Wednesday
                            {1, 0, 0, 0},  // History - Monday
                            {0, 0, 0, 1}   // Art - Thursday
                        }
                    );
                case 2:
                    return new PuzzleData(
                        new String[][] {
                            {"Dog", "Cat", "Bird", "Fish"},
                            {"Park", "Home", "Garden", "Shop"}
                        },
                        new String[] {
                            "The Dog is either at the Park or Garden",
                            "The Cat stays indoors",
                            "The Fish is at the Shop",
                            "The Bird is not in the Garden",
                            "If the Dog is at the Park, the Bird is at Home"
                        },
                        new int[][] {
                            {1, 0, 0, 0},  // Dog - Park
                            {0, 1, 0, 0},  // Cat - Home
                            {0, 0, 0, 1},  // Bird - Shop
                            {0, 0, 1, 0}   // Fish - Garden
                        }
                    );
                case 3:
                    return new PuzzleData(
                        new String[][] {
                            {"Piano", "Guitar", "Drums", "Bass", "Violin"},
                            {"Jazz", "Rock", "Blues", "Classical", "Folk"}
                        },
                        new String[] {
                            "The Piano is featured in Jazz music",
                            "Rock music needs both Guitar and Drums",
                            "The Bass provides foundation in Blues",
                            "Classical music prominently features the Violin",
                            "Folk music uses stringed instruments",
                            "The Guitar isn't used in Classical music",
                            "The Drums create rhythm in energetic genres",
                            "String instruments are versatile across genres"
                        },
                        new int[][] {
                            {1, 0, 0, 0, 0},  // Piano - Jazz
                            {0, 1, 0, 0, 0},  // Guitar - Rock
                            {0, 0, 1, 0, 0},  // Drums - Blues
                            {0, 0, 0, 1, 0},  // Bass - Classical
                            {0, 0, 0, 0, 1}   // Violin - Folk
                        }
                    );
                case 4:
                    return new PuzzleData(
                        new String[][] {
                            {"Coffee", "Tea", "Juice", "Milk"},
                            {"Small", "Medium", "Large", "Extra Large"}
                        },
                        new String[] {
                            "The largest drink contains caffeine",
                            "If Tea is Medium, then Juice must be Small",
                            "Coffee isn't served in the smallest size",
                            "The Medium drink isn't as sweet as the Small one",
                            "If Coffee is Large, then Tea can't be Small",
                            "The Extra Large drink is not milk"
                        },
                        new int[][] {
                            {0, 0, 0, 1},  // Coffee - Large
                            {0, 0, 1, 0},  // Tea - Medium
                            {0, 1, 0, 0},  // Juice - Small
                            {1, 0, 0, 0}   // Milk - Extra Large
                        }
                    );
                case 5:
                    return new PuzzleData(
                        new String[][] {
                            {"Soccer", "Tennis", "Chess", "Basketball", "Volleyball"},
                            {"Park", "Gym", "Club", "Beach", "Stadium"}
                        },
                        new String[] {
                            "The outdoor sport is played in the Park",
                            "If Tennis is at the Club, Chess isn't at the Gym",
                            "The mind sport is played indoors",
                            "The Gym activity requires a net",
                            "If Soccer is outdoors, then Tennis must be at the Gym",
                            "The Beach activity is not played in the Stadium",
                            "The Volleyball activity is not played in the Park"
                        },
                        new int[][] {
                            {1, 0, 0, 0, 0},  // Soccer - Park
                            {0, 1, 0, 0, 0},  // Tennis - Gym
                            {0, 0, 1, 0, 0},  // Chess - Club
                            {0, 0, 0, 1, 0},  // Basketball - Beach
                            {0, 0, 0, 0, 1}   // Volleyball - Stadium
                        }
                    );
                case 6:
                    return new PuzzleData(
                        new String[][] {
                            {"Cake", "Bread", "Cookie", "Pastry", "Pie"},
                            {"Morning", "Noon", "Night", "Afternoon", "Evening"}
                        },
                        new String[] {
                            "The sweet treats are made at opposite times of day",
                            "If Bread is made at Noon, Cookie must be made in the Morning",
                            "The Night baking isn't Bread",
                            "The Morning item needs the least baking time",
                            "If Cake is made at Night, then Bread can't be made in the Morning",
                            "The Afternoon baking isn't Bread",
                            "The Pastry is not made in the Morning"
                        },
                        new int[][] {
                            {0, 0, 0, 1, 0},  // Cake - Night
                            {0, 0, 1, 0, 0},  // Bread - Noon
                            {0, 1, 0, 0, 0},  // Cookie - Morning
                            {1, 0, 0, 0, 0},  // Pastry - Afternoon
                            {0, 0, 0, 0, 1}   // Pie - Evening
                        }
                    );
            }
        }
        
        // HARD LEVELS - 5x5 grid with complex logical relationships
        if (difficulty.equals("HARD")) {
            switch (level) {
                case 1:
                    return new PuzzleData(
                        new String[][] {
                            {"Python", "Java", "Ruby", "C++", "Go"},
                            {"Web", "Mobile", "Data", "Game", "Cloud"},
                            {"London", "Paris", "Rome", "Berlin", "Tokyo"}
                        },
                        new String[] {
                            "Python is used in either Web or Data projects",
                            "Java is perfect for Mobile development",
                            "Ruby isn't used for Game development",
                            "C++ is used where performance matters most",
                            "Go is designed for Cloud applications",
                            "The Web project uses a scripting language",
                            "The Game project needs high performance",
                            "The Data project uses an interpreted language",
                            "The city visited in Winter is further north than the one visited in Summer",
                            "If Paris is visited in Spring, then Rome must be visited in Summer",
                            "London isn't visited in the warmest season",
                            "The Spring destination is in a different country than the Winter one",
                            "If Rome isn't visited in Spring, then Paris must be visited in Winter",
                            "The Summer destination is known for its ancient history"
                        },
                        new int[][] {
                            {1, 0, 0, 0, 0},  // Python - Web
                            {0, 1, 0, 0, 0},  // Java - Mobile
                            {0, 0, 1, 0, 0},  // Ruby - Data
                            {0, 0, 0, 1, 0},  // C++ - Game
                            {0, 0, 0, 0, 1},  // Go - Cloud
                            {0, 1, 0, 0, 0},  // London - Summer
                            {0, 0, 0, 1, 0},  // Paris - Winter
                            {1, 0, 0, 0, 0},  // Rome - Spring
                            {0, 0, 1, 0, 0},  // Berlin - Autumn
                            {0, 0, 0, 0, 1}   // Tokyo - Autumn
                        }
                    );
                case 2:
                    return new PuzzleData(
                        new String[][] {
                            {"Piano", "Guitar", "Drums", "Bass", "Violin"},
                            {"Jazz", "Rock", "Blues", "Classical", "Folk"},
                            {"Expert", "Amateur", "Beginner", "Amateur", "Beginner"}
                        },
                        new String[] {
                            "The Piano is featured in Jazz music",
                            "Rock music needs both Guitar and Drums",
                            "The Bass provides foundation in Blues",
                            "Classical music prominently features the Violin",
                            "Folk music uses stringed instruments",
                            "The Guitar isn't used in Classical music",
                            "The Drums create rhythm in energetic genres",
                            "String instruments are versatile across genres",
                            "The Expert player prefers games of pure strategy",
                            "If the Amateur plays Cards, then the Beginner doesn't play Dice",
                            "The Dice player has more experience than the Cards player",
                            "If Chess isn't played by the Expert, then Cards must be played by the Amateur",
                            "The Beginner doesn't play a game involving chance",
                            "If the Amateur plays Dice, then the Expert must play Chess",
                            "The Cards player isn't the most experienced"
                        },
                        new int[][] {
                            {1, 0, 0, 0, 0},  // Piano - Jazz
                            {0, 1, 0, 0, 0},  // Guitar - Rock
                            {0, 0, 1, 0, 0},  // Drums - Blues
                            {0, 0, 0, 1, 0},  // Bass - Classical
                            {0, 0, 0, 0, 1},  // Violin - Folk
                            {1, 0, 0, 0, 0},  // Expert - Expert
                            {0, 1, 0, 0, 0},  // Amateur - Amateur
                            {0, 0, 1, 0, 0},  // Beginner - Beginner
                            {0, 0, 0, 1, 0},  // Amateur - Beginner
                            {0, 0, 0, 0, 1}   // Beginner - Expert
                        }
                    );
                case 3:
                    return new PuzzleData(
                        new String[][] {
                            {"Mystery", "Romance", "Fantasy", "Thriller", "Horror"},
                            {"Morning", "Afternoon", "Evening", "Night", "Midnight"}
                        },
                        new String[] {
                            "The Evening reading session isn't for light-hearted stories",
                            "If Romance is read in the Morning, then Fantasy isn't read in the Afternoon",
                            "The Mystery book is read when the sun is up",
                            "If Fantasy is read in the Evening, then Romance must be read in the Afternoon",
                            "The Morning reader prefers plots with magical elements",
                            "The book read in the Afternoon has more dialogue than action",
                            "If Mystery isn't read in the Afternoon, then Fantasy must be read in the Morning",
                            "The Evening reading requires the most concentration",
                            "The Thriller is not a horror story",
                            "The Horror story is not a thriller"
                        },
                        new int[][] {
                            {0, 0, 0, 1, 0},  // Mystery - Evening
                            {0, 0, 1, 0, 0},  // Romance - Afternoon
                            {0, 1, 0, 0, 0},  // Fantasy - Morning
                            {1, 0, 0, 0, 0},  // Thriller - Morning
                            {0, 0, 0, 0, 1}   // Horror - Midnight
                        }
                    );
                case 4:
                    return new PuzzleData(
                        new String[][] {
                            {"Painting", "Sculpture", "Photo", "Drawing", "Print"},
                            {"Gallery", "Museum", "Studio", "Beach", "Street"},
                            {"Morning", "Noon", "Night", "Afternoon", "Evening"}
                        },
                        new String[] {
                            "The Museum piece is three-dimensional",
                            "If the Painting is in the Gallery, then the Photo isn't in the Studio",
                            "The Studio work was created with modern technology",
                            "If the Sculpture isn't in the Museum, then the Painting must be in the Studio",
                            "The Gallery piece can be reproduced easily",
                            "The oldest art form is not displayed in the newest venue",
                            "If the Photo is in the Gallery, then the Sculpture must be in the Museum",
                            "The Studio doesn't display traditional art forms",
                            "The Morning item needs the least baking time",
                            "The Afternoon baking isn't Bread",
                            "The Night baking isn't Bread"
                        },
                        new int[][] {
                            {0, 0, 0, 1, 0},  // Painting - Gallery
                            {0, 0, 1, 0, 0},  // Sculpture - Museum
                            {1, 0, 0, 0, 0},  // Photo - Gallery
                            {0, 1, 0, 0, 0},  // Drawing - Studio
                            {0, 0, 0, 0, 1},  // Print - Beach
                            {0, 0, 0, 0, 1},  // Morning - Morning
                            {0, 0, 0, 1, 0},  // Noon - Afternoon
                            {0, 0, 1, 0, 0},  // Night - Afternoon
                            {0, 0, 0, 0, 1}   // Evening - Evening
                        }
                    );
                case 5:
                    return new PuzzleData(
                        new String[][] {
                            {"Violin", "Piano", "Flute", "Cello", "Bass"},
                            {"Classical", "Jazz", "Folk", "Rock", "Blues"},
                            {"Expert", "Amateur", "Beginner", "Amateur", "Beginner"}
                        },
                        new String[] {
                            "The Classical piece features a string instrument",
                            "If Piano plays Jazz, then Flute must play Folk",
                            "The instrument in Folk music is not the largest one",
                            "If Violin isn't in Classical, then Piano must be in Folk",
                            "The Jazz performance uses a percussion-capable instrument",
                            "If the Flute plays Classical, then Violin can't play Folk",
                            "The instrument in Jazz can play both melody and harmony",
                            "The Folk music uses the most portable instrument",
                            "The Expert player prefers games of pure strategy",
                            "If the Amateur plays Cards, then the Beginner doesn't play Dice",
                            "The Dice player has more experience than the Cards player",
                            "If Chess isn't played by the Expert, then Cards must be played by the Amateur",
                            "The Beginner doesn't play a game involving chance",
                            "If the Amateur plays Dice, then the Expert must play Chess",
                            "The Cards player isn't the most experienced"
                        },
                        new int[][] {
                            {1, 0, 0, 0, 0},  // Violin - Classical
                            {0, 1, 0, 0, 0},  // Piano - Jazz
                            {0, 0, 1, 0, 0},  // Flute - Folk
                            {0, 0, 0, 1, 0},  // Cello - Classical
                            {0, 0, 0, 0, 1},  // Bass - Classical
                            {1, 0, 0, 0, 0},  // Expert - Expert
                            {0, 1, 0, 0, 0},  // Amateur - Amateur
                            {0, 0, 1, 0, 0},  // Beginner - Beginner
                            {0, 0, 0, 1, 0},  // Amateur - Beginner
                            {0, 0, 0, 0, 1}   // Beginner - Expert
                        }
                    );
            }
        }
        
        return null;
    }
}