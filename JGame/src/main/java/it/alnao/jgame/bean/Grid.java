package it.alnao.jgame.bean;

import java.util.ArrayList;

import it.alnao.jgame.Renderer;

public class Grid {
    private int width;
    private int height;
    private ArrayList<Tile> tiles;

    public Grid(int width,int height){
        this.width=width;
        this.height=height;
        this.tiles=new ArrayList<>();
        for (int i=0;i<this.width*this.height;i++){
            this.tiles.add ( null );
        }
    }
    public int getWidth(){return width; }
    public int getSite(){ return this.tiles.size();}
    public void setTile(int col,int row,Tile t){
        tiles.set ( col*width + row,t );
    }
    public Tile getTile(int row, int col){
        return this.tiles.get( col * width + row);
    }
    public void drow(Renderer renderer){ //PGraphics g, Spritesheet spritesheet){
        for (int i=0;i<tiles.size();i++){
            if ( this.tiles.get(i)!=null ){ //evito le tail vuote
                int x = (int) i % this.getWidth();
                int y = (int) i / this.getWidth();
                String spriteName = this.tiles.get(i).getName();
                if (spriteName != null){
                    renderer.drowSprite(spriteName,x,y);
                }
            }
        }
    }
    

    
}
