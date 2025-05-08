public class Player {
    private String name;
    private Fleet fleet;
    private int score;
    private boolean alive;

    public Player(String name, Fleet fleet) {
        this.name = name;
        this.fleet = fleet;
        this.score = 0;
        this.alive = true;
    }

    public String getName() {
        return name;
    }

    public Fleet getFleet() {
        return fleet;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int points) {
        this.score += points;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}