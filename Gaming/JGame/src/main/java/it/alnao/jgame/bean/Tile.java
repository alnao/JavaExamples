package it.alnao.jgame.bean;

//import processing.core.PImage;

public class Tile {
    private String spriteName;
    private boolean isSolid;

    public Tile(String name, boolean isSolid){
        this.spriteName=name;
        this.isSolid=isSolid;
    }
    public String getName(){
        return this.spriteName;
    }
    public boolean isSolid(){
        return this.isSolid;
    }
    /* 
    private PImage sprite ;
    public Tile(){
        sprite=null; // ???
    }
    public Tile(PImage image){
        sprite=image;
    }
    public PImage getSprite(){
        return sprite;
    }*/

}
