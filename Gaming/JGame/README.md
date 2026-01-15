# Alnao JGame RogueLike 

## Comandi
Per la compilazione e l'avvio
```bash
mvn -DskipTests package
java -jar ./target/JGame-1.0-SNAPSHOT.jar
```

## Fonti
- Fonte degli esempi: video https://www.youtube.com/watch?v=kumZYVKhn6I
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
- Modificata successivamente con l'aiuto della IA con i prompt
    - quando si calcola il mondo floor/wall fai in modo che esista un tragitto di floor dal punto in alto a sinistra al punto in basso a destra, se non toglie un wall in modo che ci sia il percorso, aggiungi X mostri che si muovono a caso ma sempre verso il giocatore, se il mostro è vicino al personaggio è gameover, il giocatore vince se raggiunge il punto in basso a destra
    - modifica un po' la grafica: la scritta deve essere fuori dalla mappa, la mappa deve essere tutta visibile, modifica il disegno dei mostri in blu
    -  i mostri se non possono muoversi verso il giocatore, fanno un movimento randomico possibile, al game over o alla vittoria mostra un bel messaggio tutto schermo e un bottone per fare un'altra partita con un mostro in più
    - al game over voglio un giocatore in meno e si ogni volta che si fa game-over voglio anche che togli un mostro dalla partita successiva

# AlNao.it
Nessun contenuto in questo repository è stato creato con IA oppure è chiaramente indicato dove sono state usate IA generative, tutto il codice è stato scritto con molta pazienza da Alberto Nao. Se il codice è stato preso da altri siti/progetti è sempre indicata la fonte. Per maggior informazioni visitare il sito [alnao.it](https://www.alnao.it/).

## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*