package data;

public class Score {
    private int points;

    public Score() {
        points = 0;
    }

    public void add(double value) {
        points += value;
    }

    public int getPoints() {
        return points;
    }
}
