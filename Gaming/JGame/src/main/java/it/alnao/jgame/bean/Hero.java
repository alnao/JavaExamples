package it.alnao.jgame.bean;

import it.alnao.jgame.Renderer;

public class Hero {
    private int x;
    private int y;
    public int getX(){return x;}
    public int getY(){return y;}

    public Hero (int x,int y){
        this.x=x;
        this.y=y;
    }
    public Hero(){
        this(0,0);
    }
    public void setSprite(String spriteName){
        this.spriteName = spriteName;
    }
    String spriteName;

//metodo per disegnare l'erore
    public void draw(Renderer r ){
        //g.fill(g.color(255,0,0)); //forse g.color non serve
        //g.circle(x, y, 10);
        //g.image(sprite,x  ,y);
        r.drowSprite(spriteName, x, y);
    }
    public void setPosition(int x, int y){
        this.x=x;
        this.y=y;
    }
//metodi per spostare l'erore
/* old version
    private void move (int dx,int dy){
        this.x-=dx;
        if (x<0){x=0;}
        if (x>App.DIMENSION){x=App.DIMENSION;}
        this.y-=dy;
        if (y<0){y=0;}
        if (y>App.DIMENSION){y=App.DIMENSION;}
    }
    public void moveLeft(int dx){
        this.move(dx,0);
    }
    public void moveRight(int dx){
        this.move(-1* dx,0);
    }
    public void moveUp(int dy){
        this.move(0,dy);
    }
    public void moveDown(int dy){
        this.move(0,-1 * dy);
    }
 */

}
