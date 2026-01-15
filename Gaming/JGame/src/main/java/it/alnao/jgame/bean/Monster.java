package it.alnao.jgame.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Monster {
    private int x;
    private int y;
    private final String spriteName;

    public Monster(int x, int y, String spriteName) {
        this.x = x;
        this.y = y;
        this.spriteName = spriteName;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getSpriteName() {
        return spriteName;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void stepToward(int targetX, int targetY, Grid grid, boolean[][] occupied, Random random) {
        int currentDistance = manhattan(x, y, targetX, targetY);

        List<int[]> betterMoves = new ArrayList<>(4);
        List<int[]> anyMoves = new ArrayList<>(4);

        addMoves(betterMoves, anyMoves, x + 1, y, targetX, targetY, grid, occupied, currentDistance);
        addMoves(betterMoves, anyMoves, x - 1, y, targetX, targetY, grid, occupied, currentDistance);
        addMoves(betterMoves, anyMoves, x, y + 1, targetX, targetY, grid, occupied, currentDistance);
        addMoves(betterMoves, anyMoves, x, y - 1, targetX, targetY, grid, occupied, currentDistance);

        List<int[]> candidates = !betterMoves.isEmpty() ? betterMoves : anyMoves;
        if (candidates.isEmpty()) {
            return;
        }

        int[] chosen = candidates.get(random.nextInt(candidates.size()));
        occupied[x][y] = false;
        x = chosen[0];
        y = chosen[1];
        occupied[x][y] = true;
    }

    private static void addMoves(List<int[]> betterMoves, List<int[]> anyMoves, int nx, int ny, int targetX, int targetY, Grid grid, boolean[][] occupied, int currentDistance) {
        if (nx < 0 || ny < 0 || nx >= grid.getWidth() || ny >= grid.getWidth()) {
            return;
        }
        if (occupied[nx][ny]) {
            return;
        }
        Tile t = grid.getTile(nx, ny);
        if (t == null || t.isSolid()) {
            return;
        }
        int nd = manhattan(nx, ny, targetX, targetY);
        if (nd < currentDistance) {
            betterMoves.add(new int[] { nx, ny });
        }
        anyMoves.add(new int[] { nx, ny });
    }

    public static int manhattan(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }
}
