package it.alnao.jgame.bean;

public class Sprite {
    private int row;
    private int col;
    private int width;
    private int height;

    public Sprite(int row,int col, int width, int height){
        this.row=row;
        this.col=col;
        this.width=width;
        this.height=height;
    }

    public int getRow(){return row;}
    public int getCol(){return col;}
    public int getWidth(){return width;}
    public int getHeight(){return height;}
}
