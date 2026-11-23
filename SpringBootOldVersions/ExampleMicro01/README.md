## JavaSpringBootExample ExampleMicro1

To compile
 
```
mvn -version
mvn clean install
```

To run in local

```
java -jar target/*.jar

```

To access 

```
http://localhost:5051/api/demoms/riposta
```


### Docker


Create Dockerfile

```
FROM openjdk:11-jre-slim 
COPY target/ExampleMicro1-0.0.1-SNAPSHOT.jar /ExampleMicro1.jar
CMD ["java", "-jar", "/ExampleMicro1.jar"]
```


To create docker image

```
docker build -t examplemicro1:1.0-SNAPSHOT .
```


To run image 

```
docker run -d -p 5052:5051 examplemicro1:1.0-SNAPSHOT
```

url is on 5052 port (port mapping in run command) 

```
http://localhost:5052/api/demoms/response
```

To check docker-container

```
docker ps
docker logs <container_id>
docker port <container_id>
docker stop <container_id>
docker rm <container_id>
docker container prune 
docker image ls
docker image rm <image_id>
```


## Changelog
| Data | Descrizione |
|------|-------------|
| 2026-11-23 | Aggiunti changelog e footer standard |


# AlNao.it
Tutti i codici sorgente e le informazioni presenti in questo repository sono frutto di un attento e paziente lavoro di sviluppo da parte di Alberto Nao, che si è impegnato a verificarne la correttezza nella misura massima possibile. Qualora parte del codice o dei contenuti sia stato tratto da fonti esterne, la relativa provenienza viene sempre citata, nel rispetto della trasparenza e della proprietà intellettuale. 


Alcuni contenuti e porzioni di codice presenti in questo repository sono stati realizzati anche grazie al supporto di strumenti di intelligenza artificiale, il cui contributo ha permesso di arricchire e velocizzare la produzione del materiale. Ogni informazione e frammento di codice è stato comunque attentamente verificato e validato, con l’obiettivo di garantire la massima qualità e affidabilità dei contenuti offerti. 


Per ulteriori dettagli, approfondimenti o richieste di chiarimento, si invita a consultare il sito [alnao.it](https://www.alnao.it/).


## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*

E' garantito il permesso di copiare, distribuire e/o modificare questo documento in base ai termini della GNU Free Documentation License, Versione 1.2 o ogni versione successiva pubblicata dalla Free Software Foundation. Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.2 or any later version published by the Free Software Foundation.
