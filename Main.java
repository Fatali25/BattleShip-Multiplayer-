import java.io.*;
import java.util.*;

public class Main {

    private static final String quit = "quit";
    private static final String menu = "menu";
    private static final String score = "score";
    private static final String fleet = "fleet";
    private static final String shoot = "shoot";
    private static final String player = "player";
    private static final String playersCmd = "players";
    private static final String scoresCmd = "scores";
    private static int steps = 0;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        List<Fleet> fleetList = new ArrayList<>();
        List<Player> players = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("fleets.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split(" ");
                int rows = Integer.parseInt(parts[0]);
                int cols = Integer.parseInt(parts[1]);

                List<String> grid = new ArrayList<>();
                for (int i = 0; i < rows; i++) {
                    grid.add(reader.readLine().trim().toUpperCase());
                }
                fleetList.add(new Fleet(rows, cols, grid));
            }
        } catch (IOException e) {
            System.out.println("Error reading fleets.txt: " + e.getMessage());
            return;
        }

        if (fleetList.isEmpty()) {
            System.out.println("No fleets found in fleets.txt.");
            return;
        }

        System.out.print("Enter number of players: ");
        int numPlayers = Integer.parseInt(in.nextLine());
        List<String> playerNames = new ArrayList<>();

        for (int i = 0; i < numPlayers; i++) {
            String name;
            while (true) {
                System.out.print("Enter name of player " + (i + 1) + ": ");
                name = in.nextLine().trim();
                if (playerNames.contains(name)) {
                    System.out.println("This name is already taken. Please choose a different name.");
                } else if (name.isEmpty()) {
                    System.out.println("Name cannot be empty. Please enter a valid name.");
                } else {
                    playerNames.add(name);
                    break;
                }
            }

            System.out.println("Available fleets:");
            for (int j = 0; j < fleetList.size(); j++) {
                System.out.println((j + 1) + ":");
                Fleet fleet = fleetList.get(j);
                for (String row : fleet.getGrid()) {
                    System.out.println(row);
                }
                System.out.println();
            }

            int chosenFleet = -1;
            while (chosenFleet < 1 || chosenFleet > fleetList.size()) {
                System.out.print("Choose fleet number (1-" + fleetList.size() + "): ");
                try {
                    chosenFleet = Integer.parseInt(in.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number, try again.");
                }
            }

            Fleet selectedFleet = deepCopyFleet(fleetList.get(chosenFleet - 1));
            players.add(new Player(name, selectedFleet));
        }

        Game game = new Game(players);
        interpreter(in, game);
        in.close();
    }

    private static void menu() {
        System.out.println(quit + " - Quit the program (Terminate execution)");
        System.out.println(menu + " - List all the commands");
        System.out.println(score + " (name) - Show the score of player");
        System.out.println(fleet + " (name) - Show the state of the fleet of the player");
        System.out.println(shoot + " (row) (col) - Shoot at position");
        System.out.println(player + " - Indicates the next player");
        System.out.println(playersCmd + " - Show players still in game");
        System.out.println(scoresCmd + " - Show score ranking");
    }

    private static void interpreter(Scanner in, Game game) {
        menu();
        while (true) {
            System.out.print("Input command: ");
            String command = in.nextLine();
            String[] cmd = command.split(" ");
            if (cmd[0].equals(quit)) Quit(game);
            else if (cmd[0].equals(menu)) menu();
            else if (cmd[0].equals(score) && cmd.length >= 2)
                game.showScore(command.substring(cmd[0].length() + 1));
            else if (cmd[0].equals(fleet) && cmd.length >= 2)
                game.showFleet(command.substring(cmd[0].length() + 1));
            else if (cmd[0].equals(shoot)) {
                if (cmd.length < 3) {
                    System.out.println("Usage: shoot <row> <col>");
                    continue;
                }
                try {
                    int row = Integer.parseInt(cmd[1]);
                    int col = Integer.parseInt(cmd[2]);

                    boolean success = game.shootAutoTarget(row, col, steps);
                    if (success) {
                        steps++;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid coordinates");
                }
            } else if (cmd[0].equals(player)) game.showNextPlayer(steps);
            else if (cmd[0].equals(playersCmd)) game.showPlayersStillInGame();
            else if (cmd[0].equals(scoresCmd)) game.showScoreRanking();
            else System.out.println("Invalid command");
        }
    }

    private static void Quit(Game game) {
        if (game.isGameOver()) {
            String winner = game.getWinnerName();
            System.out.println(winner + " won the game!");
        } else {
            System.out.println("The game was not over yet...");
        }
        System.exit(0);
    }

    private static Fleet deepCopyFleet(Fleet original) {
        List<String> newGrid = new ArrayList<>();
        for (String row : original.getGrid()) {
            newGrid.add(new String(row));
        }
        return new Fleet(original.getRows(), original.getCols(), newGrid);
    }
}