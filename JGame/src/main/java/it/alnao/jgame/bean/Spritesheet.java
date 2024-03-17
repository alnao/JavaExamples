package it.alnao.jgame.bean;

import java.util.HashMap;

import it.alnao.jgame.Renderer;
import processing.core.PImage;

public class Spritesheet {


    private PImage spritesheet;
    HashMap<String,PImage> sprites;

    public Spritesheet(PImage image){
        this.spritesheet=image;
        this.sprites=new HashMap<String,PImage> ();
    }
    public PImage getByName(String name){
        return this.sprites.get( name );
    }
    public void defineSprite(String name,int row,int col ){
        PImage i = spritesheet.get( col*Renderer.TILE_SIZE+col, row*Renderer.TILE_SIZE+row, Renderer.TILE_SIZE, Renderer.TILE_SIZE);
        this.sprites.put(name, i );
    }
        /*  old version
    public PImage get(int col,int row){
        return spritesheet.get( col*Renderer.TILE_SIZE+col, row*Renderer.TILE_SIZE+row, Renderer.TILE_SIZE, Renderer.TILE_SIZE);
    }
    public PImage get(int col,int row){
        return spritesheet.get( col*Renderer.TILE_SIZE+col, row*Renderer.TILE_SIZE+row, Renderer.TILE_SIZE, Renderer.TILE_SIZE);
    }
    public PImage getByName(String name){
        Sprite s = this.sprites.get( name );
        if (s==null) {return null ;}
        return get(s.getCol() , s.getRow() );
    }
    public void defineSprite(String name,int row,int col,int width,int height){
        this.sprites.put(name, new Sprite(row, col, width, height));
    }*/
    
}
