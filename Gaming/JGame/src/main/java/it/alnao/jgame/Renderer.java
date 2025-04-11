package it.alnao.jgame;
import it.alnao.jgame.bean.Spritesheet;
import processing.core.PGraphics;
import processing.core.PImage;

public class Renderer {
    public static final int TILE_SIZE=16; //16 grandezza di ogni elementi nello sheet
    public static int FATTORE_SCALA = 1;
    
    private PGraphics g ;
    private Spritesheet spritesheet;
    public Renderer ( PGraphics g,Spritesheet spritesheet){
        this.g=g;
        this.spritesheet=spritesheet;
    }
    public void drowSprite(String spriteName, int x, int y) {
        PImage sprite = spritesheet.getByName( spriteName ); 
        g.image(sprite, FATTORE_SCALA * x * TILE_SIZE, FATTORE_SCALA * y * TILE_SIZE , FATTORE_SCALA * TILE_SIZE , FATTORE_SCALA * TILE_SIZE );
    }

}
