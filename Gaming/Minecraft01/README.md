# Minecraft 01


Progetto creato con Claude.ia:
- vorrei sviluppare un gioco simile a minecraft in java, quindi scrivimi un gioco in java 17 che permetta ad un giocatore di piazzare e rimuovere blocchi, di possono essere 2 tipi di blocco (terra e pietra), un pavimento di un materiale che non si può rimuovere e un limite in altezza di 10 blocchi
- comandi per la compilazione
    ```
    mvn clean package
    java -jar target/minecraft-clone-1.0-SNAPSHOT.jar
    ```

## Progetto
La classe MinecraftClone contiene tutta la logica del gioco
- Il metodo init() inizializza GLFW, crea la finestra e configura i controlli
- Il metodo loop() contiene il ciclo principale del gioco
- processInput() gestisce il movimento del giocatore
- updateRayCast() determina quale blocco stai guardando
- placeBlock() e removeBlock() gestiscono il posizionamento e la rimozione dei blocchi
- renderWorld() disegna tutti i blocchi nel mondo
- I metodi setup3DView() e setupOrthoView() configurano le viste 3D e 2D


Possibili Miglioramenti
- Aggiungere texture ai blocchi invece di semplici colori
- Implementare la fisica di gravità per il giocatore
- Aggiungere più tipi di blocchi con proprietà diverse
- Implementare la generazione procedurale del terreno
- Aggiungere illuminazione dinamica
- Implementare un sistema di collisione più avanzato
- Aggiungere un inventario e un sistema di crafting
- Implementare modalità multiplayer

# AlNao.it
Nessun contenuto in questo repository è stato creato con IA oppure è chiaramente indicato dove sono state usate IA generative, tutto il codice è stato scritto con molta pazienza da Alberto Nao. Se il codice è stato preso da altri siti/progetti è sempre indicata la fonte. Per maggior informazioni visitare il sito [alnao.it](https://www.alnao.it/).

## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*