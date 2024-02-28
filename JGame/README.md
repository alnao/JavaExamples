# Alnao RogueLike 

See https://www.youtube.com/watch?v=kumZYVKhn6I


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

## TODO
    https://www.youtube.com/watch?v=kumZYVKhn6I
    Riprendere dal 27:11