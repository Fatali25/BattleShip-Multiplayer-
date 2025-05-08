import java.util.List;

public class Fleet {
    private int rows;
    private int cols;
    private List<String> grid;
    private List<String> originalGrid;

    public Fleet(int rows, int cols, List<String> grid) {
        this.rows = rows;
        this.cols = cols;
        this.grid = grid;
        this.originalGrid = List.copyOf(grid);
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public List<String> getGrid() {
        return grid;
    }

    public List<String> getOriginalGrid() {
        return originalGrid;
    }

    public void setGrid(List<String> grid) {
        this.grid = grid;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String row : grid) {
            sb.append(row).append("\n");
        }
        return sb.toString();
    }
}