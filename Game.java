import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Game {

    private final List<Player> players;

    public Game(List<Player> players) {
        this.players = players;
    }

    public void showScore(String name) {
        Player p = getPlayerByName(name);
        if (p != null) {
            System.out.println(p.getName() + " has " + p.getScore() + " points.");
        } else {
            System.out.println("Player not found.");
        }
    }

    public void showFleet(String name) {
        Player p = getPlayerByName(name);
        if (p != null) {
            for (String row : p.getFleet().getGrid()) {
                System.out.println(row);
            }
        } else {
            System.out.println("Player not found.");
        }
    }

    public void showPlayersStillInGame() {
        for (Player p : players) {
            if (p.isAlive()) {
                System.out.println(p.getName());
            }
        }
    }

    public void showScoreRanking() {
        List<Player> sorted = new ArrayList<>(players);
        sorted.sort(Comparator.comparing(Player::getScore).reversed());
        for (Player p : sorted) {
            System.out.println(p.getName() + ": " + p.getScore() + " points");
        }
    }

    public boolean isGameOver() {
        int aliveCount = 0;
        for (Player p : players) {
            if (p.isAlive()) {
                aliveCount++;
            }
        }
        return aliveCount <= 1;
    }

    public String getWinnerName() {
        for (Player p : players) {
            if (p.isAlive()) {
                return p.getName();
            }
        }
        return "No winner";
    }

    private Player getPlayerByName(String name) {
        for (Player p : players) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    private int getCurrentAlivePlayerIndex(int steps) {
        int count = 0;
        int index = -1;
        while (count < players.size()) {
            index = (steps + count) % players.size();
            if (players.get(index).isAlive()) {
                return index;
            }
            count++;
        }
        return -1;
    }

    public void showNextPlayer(int steps) {
        if (isGameOver()) {
            System.out.println("The game is over");
        } else {
            int nextPlayerIndex = getCurrentAlivePlayerIndex(steps);
            if (nextPlayerIndex == -1) {
                System.out.println("The game is over");
            } else {
                System.out.println("Next player: " + players.get(nextPlayerIndex).getName());
            }
        }
    }

    public boolean shootAutoTarget(int row, int col, int steps) {
        if (isGameOver()) {
            System.out.println("The game is over");
            return false;
        }

        int currentIndex = getCurrentAlivePlayerIndex(steps);
        if (currentIndex == -1) {
            System.out.println("No alive players left");
            return false;
        }

        Player shooter = players.get(currentIndex);

        if (!shooter.isAlive()) {
            System.out.println("Eliminated player");
            return false;
        }

        // Знайти наступного живого гравця для атаки
        int targetIndex = (currentIndex + 1) % players.size();
        while (!players.get(targetIndex).isAlive() || targetIndex == currentIndex) {
            targetIndex = (targetIndex + 1) % players.size();
        }

        Player target = players.get(targetIndex);
        Fleet fleet = target.getFleet();

        if (row < 1 || row > fleet.getRows() || col < 1 || col > fleet.getCols()) {
            System.out.println("Invalid shot position");
            return false;
        }

        List<String> grid = fleet.getGrid();
        String currentRow = grid.get(row - 1);
        char cell = currentRow.charAt(col - 1);

        if (Character.isLetter(cell)) {
            char ship = cell;
            int count = 0;
            List<String> newGrid = new ArrayList<>();
            for (String r : grid) {
                count += r.chars().filter(c -> c == ship).count();
            }
            for (String r : grid) {
                newGrid.add(r.replace(ship, '*'));
            }
            fleet.setGrid(newGrid);
            int pointsGained = 100 * count;
            shooter.addScore(pointsGained);
            System.out.println(shooter.getName() + " destroyed part of " + target.getName() + "'s fleet and gains +" + pointsGained + " points");

            boolean isAllDestroyed = true;
            for (String r : fleet.getGrid()) {
                if (r.chars().anyMatch(Character::isLetter)) {
                    isAllDestroyed = false;
                    break;
                }
            }
            if (isAllDestroyed) {
                target.setAlive(false);
                System.out.println(target.getName() + " has been eliminated!");
            }

        } else if (cell == '.') {
            System.out.println("Hits the water.");
        } else if (cell == '*') {
            List<String> originalGrid = fleet.getOriginalGrid();
            char originalShip = originalGrid.get(row - 1).charAt(col - 1);
            if (Character.isLetter(originalShip)) {
                int count = 0;
                for (String r : originalGrid) {
                    count += r.chars().filter(c -> c == originalShip).count();
                }
                int penalty = 30 * count;
                shooter.addScore(-penalty);
                System.out.println("Already destroyed.\n-" + penalty + " points for " + shooter.getName());
            } else {
                shooter.addScore(-30);
                System.out.println("Already destroyed (water).\n-30 points for " + shooter.getName());
            }
        }

        return true;
    }
}