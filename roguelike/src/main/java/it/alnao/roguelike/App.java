package it.alnao.roguelike;

import it.alnao.roguelike.bean.Hero;
import processing.core.PApplet;
import processing.core.PGraphics;

/**
 * AlNao RougeLike
 * @author Alberto Nao (alnao.it)
 * @see https://www.youtube.com/watch?v=kumZYVKhn6I
 */
public class App extends PApplet{
    public static final int DIMENSION = 500;
     
    private Hero hero; //eroe
    private PGraphics g; //finestra 
    public App(){
        this.hero=new Hero(20,40); //start position
    }
    public static void main( String[] args ){
        PApplet.main("it.alnao.roguelike.App");
        System.out.println( "Hello World!" );
    }
    @Override
    public void settings(){
        size(DIMENSION,DIMENSION);
    }
    @Override
    public void setup(){
        g=getGraphics(); //questo non funziona in settings
    }
    @Override
    public void draw(){
        background(66);
        //circle(100,100,20);
        this.hero.draw(g);
    }
    @Override
    public void keyTyped(){
        //System.out.println(key);
        if (key=='a'){ hero.moveLeft(1); }
        if (key=='d'){ hero.moveRight(1); }
        if (key=='w'){ hero.moveUp(1); }
        if (key=='s'){ hero.moveDown(1); }
    }

}
