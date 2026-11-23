# Esempio02db
Progetto base creato e scaricato dal sito [start.spring.io](https://start.spring.io/) selezionando la versione "3.3.0" con "Maven" e "Java17".
Poi aggiunte le classi Controller, Repository, Entity e Service.

Per questo progetto è indispensabile avere la versione 17 di Java, con precedenti versioni non funziona.


## Comandi base
* per compilare
    ```
    mvn -version
    mvn clean install
    ```
* per eseguire in locale usando maven
    ```
    mvn spring-boot:run
    ```
* per eseguire in locale usando il comando java
    ```
    java -jar target/*.jar
    ```
* per eseguire test di chiamata alla API esposta
    ```
    curl http://localhost:5051/api/articoli/check
    curl http://localhost:5051/api/articoli/all
    ```
* per eseguire la sequenza di test-unit
    ```
    mvn test
    ```

## Docker 
* definizione nel docker-compose
    ```
    services:
    mysql:
        image: mysql:8.0
        environment:
        MYSQL_ROOT_PASSWORD: xxxxxx
        MYSQL_DATABASE: MyWeb
        MYSQL_USER: root
        MYSQL_PASSWORD: xxxxxx
        ports:
        - "3306:3306"
        volumes:
        - mysql_data:/var/lib/mysql

    app:
        build:
        context: .
        dockerfile: Dockerfile
        ports:
        - "5555:5051"
        depends_on:
        - mysql
        environment:
        SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/MyWeb
        SPRING_DATASOURCE_USERNAME: root
        SPRING_DATASOURCE_PASSWORD: xxxx

    volumes:
    mysql_data:
    ```
* nota: la porta 3306 del sistema deve essere libera cioè non deve essere in esecuzione nessun server mysql
* definizione del **Dockerfile**
    ```
    FROM eclipse-temurin:17-jdk-alpine
    WORKDIR /app
    COPY target/*.jar app.jar
    ENTRYPOINT ["java","-jar","app.jar"]
    ```
* per creare l'immagine
    ```
    docker build -t esempio01base:1.0-SNAPSHOT .
    ```
* per eseguire l'immagine esponendo il servizio su porta 5555
    ```
    docker-compose up --build
    curl http://localhost:5555/api/articoli/check
    curl http://localhost:5555/api/articoli/all
    ```
* comando docker utili
    ```
    docker-compose down
    docker-compose logs
    docker-compose exec mysql mysql -u your_username -p
    ```


## Changelog
| Data | Descrizione |
|------|-------------|
| 2026-11-23 | Aggiunto changelog, footer allineato alla versione standard |


# AlNao.it
Tutti i codici sorgente e le informazioni presenti in questo repository sono frutto di un attento e paziente lavoro di sviluppo da parte di Alberto Nao, che si è impegnato a verificarne la correttezza nella misura massima possibile. Qualora parte del codice o dei contenuti sia stato tratto da fonti esterne, la relativa provenienza viene sempre citata, nel rispetto della trasparenza e della proprietà intellettuale. 


Alcuni contenuti e porzioni di codice presenti in questo repository sono stati realizzati anche grazie al supporto di strumenti di intelligenza artificiale, il cui contributo ha permesso di arricchire e velocizzare la produzione del materiale. Ogni informazione e frammento di codice è stato comunque attentamente verificato e validato, con l’obiettivo di garantire la massima qualità e affidabilità dei contenuti offerti. 


Per ulteriori dettagli, approfondimenti o richieste di chiarimento, si invita a consultare il sito [alnao.it](https://www.alnao.it/).


## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*

E' garantito il permesso di copiare, distribuire e/o modificare questo documento in base ai termini della GNU Free Documentation License, Versione 1.2 o ogni versione successiva pubblicata dalla Free Software Foundation. Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.2 or any later version published by the Free Software Foundation.
