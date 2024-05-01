# Alnao RogueLike 

## Fonti

- Base e Spritesheet: https://www.youtube.com/watch?v=kumZYVKhn6I

- Spritesheet from https://kenney.nl/assets/1-bit-pack file "colored-transparent.png" > 

- Tail e hero con indici e renderer https://www.youtube.com/watch?v=2yR5qEDRpgc

- Movimenti hero https://www.youtube.com/watch?v=eaGwgPI27Ao riprendere dal minuto 25

- Parte 3 https://www.youtube.com/watch?v=7CfJ0Z5ZhAA

- Parte 4 https://www.youtube.com/watch?v=coca2rx86vM

## Create project

- In Visual studio code > Create new java project with maven

- Import with maven version 4 with https://github.com/micycle1/processing-core-4
    - or download processing from https://processing.org and use ```code/library/core.jar```
    - not work with official maven https://mvnrepository.com/artifact/org.processing/core/3.3.6
    - Note: core version 4.1.1 and onwards require Java 17+; prior versions require Java 11+.

- First main class 
    ```
    import processing.core.PApplet;
    public class App extends PApplet{
        public static void main( String[] args ){
            PApplet.main("it.alnao.roguelike.App");
            System.out.println( "Hello World!" );
        }
    }
    ```
- First painted
    ```
    public void settings(){
        size(1000,800);
    }
    public void draw(){
        background(66);
        circle(100,100,20);
    }
    ```


