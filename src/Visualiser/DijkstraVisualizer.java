package Visualiser;

import Algorithm.DijkstraAlgorithm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DijkstraVisualizer extends JFrame {
    private final int rows = 20;
    private final int cols = 20;
    private final int cellSize = 30;
    private Cell[][] grid = new Cell[rows][cols];
    private Point start = null;
    private Point end = null;
    private boolean settingStart = false;
    private boolean settingEnd = false;
    private boolean settingWalls = false;

    public DijkstraVisualizer() {
        setTitle("Dijkstra Pathfinding");
        setSize(cols * cellSize, rows * cellSize);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel gridPanel = new JPanel(new GridLayout(rows, cols));
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                grid[row][col] = new Cell(row, col);
                gridPanel.add(grid[row][col]);
            }
        }

        add(gridPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        JButton setStartButton = new JButton("Add Source");
        JButton setEndButton = new JButton("Add Destination");
        JButton setWallButton = new JButton("Add Obstacles");
        JButton runButton = new JButton("Run Dijkstra");
        JButton resetButton = new JButton("Reset");

        controlPanel.add(setStartButton);
        controlPanel.add(setEndButton);
        controlPanel.add(setWallButton);
        controlPanel.add(runButton);
        controlPanel.add(resetButton);

        add(controlPanel, BorderLayout.SOUTH);

        setStartButton.addActionListener(e -> {
            resetModes();
            settingStart = true;
        });

        setEndButton.addActionListener(e -> {
            resetModes();
            settingEnd = true;
        });

        setWallButton.addActionListener(e -> {
            resetModes();
            settingWalls = true;
        });

        runButton.addActionListener(e -> {
            if (start != null && end != null) {
                new Thread(this::runDijkstra).start();
            } else {
                JOptionPane.showMessageDialog(this, "Please set start and end points!");
            }
        });

        resetButton.addActionListener(e -> resetGrid());

        gridPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Component clickedComponent = gridPanel.getComponentAt(e.getPoint());
                if (clickedComponent instanceof Cell) {
                    Cell clickedCell = (Cell) clickedComponent;

                    if (settingStart) {
                        setStart(clickedCell);
                    } else if (settingEnd) {
                        setEnd(clickedCell);
                    } else if (settingWalls) {
                        setWall(clickedCell);
                    }
                }
            }
        });

        setVisible(true);
    }

    private void runDijkstra() {
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(grid, rows, cols);
        DijkstraAlgorithm.Result result = dijkstra.findShortestPath(start, end);

        if (result != null) {
            for (Point p : result.exploredNodes) {
                if (!p.equals(start) && !p.equals(end)) {
                    SwingUtilities.invokeLater(() -> grid[p.x][p.y].setBackground(Color.CYAN));
                    try {
                        Thread.sleep(100); // Adjust the delay as needed
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            if (result.path != null) {
                for (Point p : result.path) {
                    if (!p.equals(start) && !p.equals(end)) {
                        SwingUtilities.invokeLater(() -> grid[p.x][p.y].setBackground(Color.YELLOW));
                        try {
                            Thread.sleep(100); // Adjust the delay as needed
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            } else {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "No path found!"));
            }
        }
    }

    private void setStart(Cell cell) {
        if (start != null) {
            grid[start.x][start.y].setBackground(null);
        }
        start = new Point(cell.getRow(), cell.getCol());
        cell.setBackground(Color.GREEN);
        settingStart = false;
    }

    private void setEnd(Cell cell) {
        if (end != null) {
            grid[end.x][end.y].setBackground(null);
        }
        end = new Point(cell.getRow(), cell.getCol());
        cell.setBackground(Color.RED);
        settingEnd = false;
    }

    private void setWall(Cell cell) {
        if (!cell.equals(start) && !cell.equals(end)) {
            cell.setWall(true);
            cell.setBackground(Color.BLACK);
        }
    }

    private boolean isInBounds(int row, int col) {
        return row >= 0 && col >= 0 && row < rows && col < cols;
    }

    private void resetGrid() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                grid[row][col].setBackground(null);
                grid[row][col].setWall(false);
            }
        }
        start = null;
        end = null;
    }

    private void resetModes() {
        settingStart = false;
        settingEnd = false;
        settingWalls = false;
    }

    public static void main(String[] args) {
        new DijkstraVisualizer();
    }

    public static class Cell extends JPanel {
        private final int row;
        private final int col;
        private boolean isWall = false;

        public Cell(int row, int col) {
            this.row = row;
            this.col = col;
            setBorder(BorderFactory.createLineBorder(Color.GRAY));
        }

        public void setWall(boolean isWall) {
            this.isWall = isWall;
        }

        public boolean isWall() {
            return isWall;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }
    }
}