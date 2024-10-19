package Algorithm;

import Visualiser.DijkstraVisualizer.Cell;

import java.awt.*;
import java.util.*;
import java.util.List;

public class DijkstraAlgorithm {
    private final int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    private final int rows;
    private final int cols;
    private final Cell[][] grid;

    public DijkstraAlgorithm(Cell[][] grid, int rows, int cols) {
        this.grid = grid;
        this.rows = rows;
        this.cols = cols;
    }

    public Result findShortestPath(Point start, Point end) {
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));
        int[][] distances = new int[rows][cols];
        for (int[] row : distances) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        distances[start.x][start.y] = 0;

        pq.offer(new Node(start.x, start.y, 0));
        boolean[][] visited = new boolean[rows][cols];
        Map<Point, Point> previous = new HashMap<>();
        List<Point> exploredNodes = new ArrayList<>();

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            if (visited[current.row][current.col]) continue;
            visited[current.row][current.col] = true;
            exploredNodes.add(new Point(current.row, current.col));

            if (current.row == end.x && current.col == end.y) {
                return new Result(reconstructPath(previous, start, end), exploredNodes);
            }

            for (int[] dir : directions) {
                int newRow = current.row + dir[0];
                int newCol = current.col + dir[1];
                if (isInBounds(newRow, newCol) && !grid[newRow][newCol].isWall()) {
                    int newDist = current.distance + 1;
                    if (newDist < distances[newRow][newCol]) {
                        distances[newRow][newCol] = newDist;
                        pq.offer(new Node(newRow, newCol, newDist));
                        previous.put(new Point(newRow, newCol), new Point(current.row, current.col));
                    }
                }
            }
        }
        return new Result(null, exploredNodes); // No path found
    }

    private List<Point> reconstructPath(Map<Point, Point> previous, Point start, Point end) {
        List<Point> path = new ArrayList<>();
        for (Point at = end; at != null; at = previous.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }

    private boolean isInBounds(int row, int col) {
        return row >= 0 && col >= 0 && row < rows && col < cols;
    }

    static class Node {
        int row, col, distance;

        public Node(int row, int col, int distance) {
            this.row = row;
            this.col = col;
            this.distance = distance;
        }
    }

    public static class Result {
        public final List<Point> path;
        public final List<Point> exploredNodes;

        public Result(List<Point> path, List<Point> exploredNodes) {
            this.path = path;
            this.exploredNodes = exploredNodes;
        }
    }
}