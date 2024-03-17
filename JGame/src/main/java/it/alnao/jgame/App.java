package it.alnao.jgame;

import it.alnao.jgame.bean.Grid;
import it.alnao.jgame.bean.Hero;
import it.alnao.jgame.bean.Spritesheet;
import it.alnao.jgame.bean.Tile;
import processing.core.PApplet;
import processing.core.PGraphics;

/**
 * AlNao RougeLike
 * @author Alberto Nao (alnao.it)
 * @see https://www.youtube.com/watch?v=kumZYVKhn6I
 */
public class App extends PApplet{
    public static final int DIMENSION = 42;
    public static final int PIXEL_DIMENSION = Renderer.TILE_SIZE * DIMENSION; //quadrato di 42 tile
     
    private Hero hero; //eroe
    private PGraphics g; //finestra 
    private Spritesheet spritesheet;
    private Grid grid;
    private Renderer renderer;

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
        size(PIXEL_DIMENSION,PIXEL_DIMENSION);
    }
    @Override
    public void setup(){
        this.g=getGraphics(); //questo non funziona in settings
        //carico l'immagine sprite
        this.spritesheet = new Spritesheet( loadImage("/mnt/Dati/Workspace/JavaExamples/JGame/assets/colored-transparent.png") );
        this.renderer=new Renderer(g,spritesheet);
        //preparo l'eroe
        this.spritesheet.defineSprite("hero",0,27); //1,1=Albero
        hero.setSprite( "hero" ); //PImage hesoSprite = this.spritesheet.get(1,1);//vedere immagine griglia
        //preparo pavimento e muri
        //PImage floowSprite = this.spreedsheet.get(2,2);//vedere immagine griglia
        //PImage wallSprite =    this.spreedsheet.get(3,3);//vedere immagine griglia
        this.spritesheet.defineSprite("floor",0,7);
        this.spritesheet.defineSprite("wall",1,1); //13,0 = muretto
        for (int i=0;i<grid.getSite();i++){
            int col=i % grid.getWidth();
            int row=(int ) i/ grid.getWidth(); //non sarebbe da fare qui!
            //logica di cosa è floor e cosa è wall
            double random = Math.random()  ;            
            boolean isSolid=random < 0.3 && (col > 2 || row > 2) && (col < DIMENSION - 2 || row < DIMENSION - 2) ;
            String name=isSolid ? "wall" : "floor";
            Tile t = new Tile( name  ,   isSolid);
            grid.setTile(col,row,t);
        }
    }
    @Override
    public void draw(){
        background(66);
        this.grid.drow(renderer);
        //circle(100,100,20);
        //image(spreedsheet,0,0); //,DIMENSION,DIMENSION
        this.hero.draw(renderer);
    }
    @Override
    public void keyTyped(){
        int x=hero.getX();
        int y=hero.getY();
        //System.out.println(key);
        if (key=='a'){ x--; /*hero.moveLeft(1);*/ }
        if (key=='d'){ x++; /*hero.moveRight(1);*/ }
        if (key=='w'){ y--; /*hero.moveUp(1);*/ }
        if (key=='s'){ y++; /*hero.moveDown(1);*/ }
        if (x>=DIMENSION || y>=DIMENSION || x<0 || y<0){ return; } 
        if (grid.getTile(x,y).isSolid() ) { return ;}
        hero.setPosition(x,y);
    }

}
