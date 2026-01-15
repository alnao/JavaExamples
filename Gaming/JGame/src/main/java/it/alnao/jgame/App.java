package it.alnao.jgame;

import it.alnao.jgame.bean.Grid;
import it.alnao.jgame.bean.Hero;
import it.alnao.jgame.bean.Monster;
import it.alnao.jgame.bean.Spritesheet;
import it.alnao.jgame.bean.Tile;
import processing.core.PApplet;
import processing.core.PGraphics;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * AlNao RougeLike
 * @author Alberto Nao (alnao.it)
 * @see https://www.youtube.com/watch?v=kumZYVKhn6I
 */
public class App extends PApplet{
    public static final int DIMENSION = 42;
    public static final int MAP_PIXEL_DIMENSION = Renderer.TILE_SIZE * DIMENSION;
    public static final int HUD_HEIGHT = 32;
    public static final int PADDING = 25;
    public static final int MONSTER_COUNT = 1;
    public static final int START_PLAYERS = 3;
    private static final int MONSTER_STEP_EVERY_N_FRAMES = 25;
     
    private Hero hero; //eroe
    private PGraphics g; //finestra 
    private Spritesheet spritesheet;
    private Grid grid;
    private Renderer renderer;
    private final Random random = new Random();
    private final List<Monster> monsters = new ArrayList<>();
    private boolean isGameOver = false;
    private boolean isWin = false;
    private int currentMonsterCount = MONSTER_COUNT;
    private int playersLeft = START_PLAYERS;
    private boolean gameOverHandled = false;

    private float restartBtnX;
    private float restartBtnY;
    private float restartBtnW;
    private float restartBtnH;

    public App(){
        this.hero=new Hero(0,0); //start position
        this.grid=new Grid(DIMENSION,DIMENSION);
    }
    public static void main( String[] args ){
        PApplet.main("it.alnao.jgame.App");
        System.out.println( "Hello Alnao-jgame!" );
    }

    @Override
    public void settings(){
        size(MAP_PIXEL_DIMENSION + PADDING * 2, MAP_PIXEL_DIMENSION + HUD_HEIGHT + PADDING * 2 );
    }
    @Override
    public void setup(){
        this.g=getGraphics(); //questo non funziona in settings
        frameRate(60);
        //carico l'immagine sprite
        this.spritesheet = new Spritesheet( loadImage("assets/colored-transparent.png") );
        this.renderer=new Renderer(g,spritesheet);
        this.renderer.setOffset(PADDING, HUD_HEIGHT + PADDING);
        //preparo l'eroe
        this.spritesheet.defineSprite("hero",0,27); //1,1=Albero
        hero.setSprite( "hero" ); //PImage hesoSprite = this.spritesheet.get(1,1);//vedere immagine griglia
        //preparo pavimento e muri
        //PImage floowSprite = this.spreedsheet.get(2,2);//vedere immagine griglia
        //PImage wallSprite =    this.spreedsheet.get(3,3);//vedere immagine griglia
        this.spritesheet.defineSprite("floor",0,7);
        this.spritesheet.defineSprite("wall",1,1); //13,0 = muretto
        this.spritesheet.defineSprite("monster",8,19); //13,0 = muretto

        playersLeft = START_PLAYERS;
        startNewGame(MONSTER_COUNT);
    }
    @Override
    public void draw(){
        background(66);
        this.grid.drow(renderer);
        //circle(100,100,20);
        //image(spreedsheet,0,0); //,DIMENSION,DIMENSION
        drawGoalMarker();
        drawMonsters();
        this.hero.draw(renderer);

        if (!isGameOver && !isWin) {
            if (frameCount % MONSTER_STEP_EVERY_N_FRAMES == 0) {
                stepMonsters();
                updateEndConditions();
            }
        }

        if (isGameOver || isWin) {
            drawEndOverlay();
        } else {
            drawStatusText();
        }
    }

    @Override
    public void mousePressed() {
        if (!isGameOver && !isWin) {
            return;
        }
        if (playersLeft <= 0) {
            return;
        }
        if (mouseX >= restartBtnX && mouseX <= restartBtnX + restartBtnW && mouseY >= restartBtnY && mouseY <= restartBtnY + restartBtnH) {
            int nextMonsters;
            if (isWin) {
                nextMonsters = currentMonsterCount + 1;
            } else {
                nextMonsters = Math.max(1, currentMonsterCount - 1);
            }
            startNewGame(nextMonsters);
        }
    }
    @Override
    public void keyTyped(){
        if (isGameOver || isWin) {
            return;
        }
        int x=hero.getX();
        int y=hero.getY();
        //System.out.println(key);
        if (key=='a' || key =='z' || key =='q'){ x--; /*hero.moveLeft(1);*/ }
        if (key=='d' || key =='e' || key =='c'){ x++; /*hero.moveRight(1);*/ }
        if (key=='w' || key =='q' || key =='e'){ y--; /*hero.moveUp(1);*/ }
        if (key=='s' || key =='z' || key =='c'){ y++; /*hero.moveDown(1);*/ }
        if (x>=DIMENSION || y>=DIMENSION || x<0 || y<0){ return; } 
        if (grid.getTile(x,y).isSolid() ) { return ;}
        hero.setPosition(x,y);

        updateEndConditions();
    }

    private void ensureStartAndGoalAreFloor() {
        setFloor(0, 0);
        setFloor(DIMENSION - 1, DIMENSION - 1);
    }

    private void ensurePathFromStartToGoal() {
        if (isReachable(0, 0, DIMENSION - 1, DIMENSION - 1)) {
            return;
        }

        // Carve a simple monotonic path (right/down) and remove walls along it.
        int x = 0;
        int y = 0;
        setFloor(x, y);
        while (x != DIMENSION - 1 || y != DIMENSION - 1) {
            if (x == DIMENSION - 1) {
                y++;
            } else if (y == DIMENSION - 1) {
                x++;
            } else {
                if (random.nextBoolean()) {
                    x++;
                } else {
                    y++;
                }
            }
            setFloor(x, y);
        }
    }

    private boolean isReachable(int startX, int startY, int goalX, int goalY) {
        boolean[][] visited = new boolean[DIMENSION][DIMENSION];
        Queue<int[]> q = new ArrayDeque<>();

        if (grid.getTile(startX, startY) == null || grid.getTile(startX, startY).isSolid()) {
            return false;
        }
        q.add(new int[] { startX, startY });
        visited[startX][startY] = true;

        while (!q.isEmpty()) {
            int[] cur = q.remove();
            int x = cur[0];
            int y = cur[1];
            if (x == goalX && y == goalY) {
                return true;
            }

            tryVisit(x + 1, y, visited, q);
            tryVisit(x - 1, y, visited, q);
            tryVisit(x, y + 1, visited, q);
            tryVisit(x, y - 1, visited, q);
        }
        return false;
    }

    private void tryVisit(int x, int y, boolean[][] visited, Queue<int[]> q) {
        if (x < 0 || y < 0 || x >= DIMENSION || y >= DIMENSION) {
            return;
        }
        if (visited[x][y]) {
            return;
        }
        Tile t = grid.getTile(x, y);
        if (t == null || t.isSolid()) {
            return;
        }
        visited[x][y] = true;
        q.add(new int[] { x, y });
    }

    private void setFloor(int x, int y) {
        grid.setTile(x, y, new Tile("floor", false));
    }

    private void spawnMonsters(int count) {
        monsters.clear();
        int attempts = 0;
        while (monsters.size() < count && attempts < 10_000) {
            attempts++;
            int x = random.nextInt(DIMENSION);
            int y = random.nextInt(DIMENSION);
            if (x == 0 && y == 0) {
                continue;
            }
            if (x == DIMENSION - 1 && y == DIMENSION - 1) {
                continue;
            }
            Tile t = grid.getTile(x, y);
            if (t == null || t.isSolid()) {
                continue;
            }
            if (Monster.manhattan(x, y, hero.getX(), hero.getY()) <= 2) {
                continue;
            }
            boolean occupied = false;
            for (Monster m : monsters) {
                if (m.getX() == x && m.getY() == y) {
                    occupied = true;
                    break;
                }
            }
            if (occupied) {
                continue;
            }

            // Reuse hero sprite to avoid relying on unknown tiles in the spritesheet.
            monsters.add(new Monster(x, y, "monster"));
        }
    }

    private void stepMonsters() {
        boolean[][] occupied = new boolean[DIMENSION][DIMENSION];
        for (Monster m : monsters) {
            occupied[m.getX()][m.getY()] = true;
        }
        for (Monster m : monsters) {
            m.stepToward(hero.getX(), hero.getY(), grid, occupied, random);
        }
    }

    private void updateEndConditions() {
        if (hero.getX() == DIMENSION - 1 && hero.getY() == DIMENSION - 1) {
            isWin = true;
            return;
        }
        for (Monster m : monsters) {
            int d = Monster.manhattan(m.getX(), m.getY(), hero.getX(), hero.getY());
            if (d <= 1) {
                triggerGameOver();
                return;
            }
        }
    }

    private void triggerGameOver() {
        isGameOver = true;
        if (!gameOverHandled) {
            playersLeft = Math.max(0, playersLeft - 1);
            gameOverHandled = true;
        }
    }

    private void drawMonsters() {
        g.pushStyle();
        g.tint(0, 0, 255); //blue!
        for (Monster m : monsters) {
            renderer.drowSprite(m.getSpriteName(), m.getX(), m.getY());
        }
        g.noTint();
        g.popStyle();
    }

    private void startNewGame(int monsterCount) {
        this.currentMonsterCount = monsterCount;
        this.isGameOver = false;
        this.isWin = false;
        this.gameOverHandled = false;

        this.hero.setPosition(0, 0);
        this.grid = new Grid(DIMENSION, DIMENSION);

        for (int i = 0; i < grid.getSite(); i++) {
            int col = i % grid.getWidth();
            int row = (int) i / grid.getWidth();
            double r = Math.random();
            boolean isSolid = r < 0.3 && (col > 2 || row > 2) && (col < DIMENSION - 2 || row < DIMENSION - 2);
            String name = isSolid ? "wall" : "floor";
            Tile t = new Tile(name, isSolid);
            grid.setTile(col, row, t);
        }

        ensureStartAndGoalAreFloor();
        ensurePathFromStartToGoal();
        spawnMonsters(this.currentMonsterCount);
        updateEndConditions();
    }

    private void drawGoalMarker() {
        int gx = DIMENSION - 1;
        int gy = DIMENSION - 1;
        g.pushStyle();
        g.noFill();
        g.stroke(255);
        g.strokeWeight(2);
        g.rect(renderer.getOffsetX() + gx * Renderer.TILE_SIZE, renderer.getOffsetY() + gy * Renderer.TILE_SIZE, Renderer.TILE_SIZE, Renderer.TILE_SIZE);
        g.popStyle();
    }

    private void drawStatusText() {
        g.pushStyle();
        g.fill(255);
        g.textSize(16);
        g.textAlign(LEFT, TOP);
        g.text("Players: " + playersLeft + "  |  Monsters: " + monsters.size(), PADDING, PADDING);
        g.popStyle();
    }

    private void drawEndOverlay() {
        g.pushStyle();

        g.noStroke();
        g.fill(0, 0, 0, 200);
        g.rect(0, 0, width, height);

        String title = isWin ? "YOU WIN!" : "GAME OVER";
        String subtitle = isWin ? "You reached the exit." : "A monster got too close.";

        g.fill(255);
        g.textAlign(CENTER, CENTER);
        g.textSize(48);
        g.text(title, width / 2f, height / 2f - 80);

        g.textSize(18);
        g.text(subtitle, width / 2f, height / 2f - 45);

        g.textSize(18);
        g.text("Players left: " + playersLeft, width / 2f, height / 2f - 20);

        String btnLabel;
        if (playersLeft <= 0) {
            btnLabel = "No players left";
        } else if (isWin) {
            btnLabel = "Another match (+1 monster)";
        } else {
            btnLabel = "Another match (-1 monster)";
        }
        g.textSize(20);
        restartBtnW = Math.min(360, width - 2f * PADDING);
        restartBtnH = 52;
        restartBtnX = (width - restartBtnW) / 2f;
        restartBtnY = height / 2f + 20;

        g.stroke(255);
        g.strokeWeight(2);
        if (playersLeft > 0) {
            g.fill(20, 20, 20);
        } else {
            g.fill(60, 60, 60);
        }
        g.rect(restartBtnX, restartBtnY, restartBtnW, restartBtnH, 8);

        g.noStroke();
        g.fill(255);
        g.text(btnLabel, width / 2f, restartBtnY + restartBtnH / 2f);

        g.popStyle();
    }

}
