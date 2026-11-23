# Esempio01base
Progetto base creato e scaricato dal sito [start.spring.io](https://start.spring.io/) selezionando la versione "3.3.0" con "Maven" e "Java17".

Per questo progetto è indispensabile avere la versione 17 di Java, con precedenti versioni non funziona. Questo progetto è studiato per funzionare anche su Docker e Kubernetes.


## Comandi base
* Per compilare il progetto:
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
    curl http://localhost:5051/api/response
    ```
* per eseguire la sequenza di test-unit
    ```
    mvn test
    ```

## Docker (esempio01base)

* definizione del **Dockerfile**
    ```
    FROM openjdk:17.0.1-jdk-slim
    COPY target/esempio01base-0.0.1-SNAPSHOT.jar /esempio01base.jar
    CMD ["java", "-jar", "/esempio01base.jar"]
    ```
* per creare l'immagine
    ```
    docker build -t esempio01base:1.1 .
    ```
* per eseguire l'immagine esponendo il servizio su porta 5555
    ```
    docker run -d -p 5555:5051 esempio01base:1.1
    curl http://localhost:5555/api/response
    ```
* comando docker utili
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

### DockerHub
* Necessaria utenza e repository su **DockerHub**, per esempio:
    ```
    https://hub.docker.com/repositories/alnao
    ```
* Configurazione docker 
    ```
    docker login
	docker system info | grep -E 'Username|Registry'
	```
* Push dell'immagine
    ```
	docker build -t esempio01:1.1 .
	docker tag esempio01:1.1 alnao/esempio01:1.1
	docker push alnao/esempio01:1.1
    ```
* Per pulizia (solo alla fine)
    ```
	docker rmi alnao/esempio01:1.1
	docker logout
    ```

## Kubernetes
* Passo preliminare: corretta configurazione di Kubernetes in locale, qui si riassumo alcuni comandi utili per la configurazione, da eseguire solo all'installazione:
    ```
    $ swapoff -a
    $ systemctl status  kubelet
    $ systemctl enable kubelet.service
    $ kubeadm init --control-plane-endpoint=cirilla  --pod-network-cidr=10.16.84.0/16
    $ mkdir ~/.kube
    $ cp /etc/kubernetes/admin.conf ~/.kube/config -i
    $ kubectl apply -f https://github.com/flannel-io/flannel/releases/latest/download/kube-flannel.yml
    $ kubectl get nodes	
    ```
* Preparazione dell'immagine ed è necessario avere l'immagine caricata su un repository (per esempio su DockerHub)
    * Compilare *da zero* l'applicazione (può capitare di dimenticarselo, vero AlNao ?), poi ricordarsi di fermare le immagini in esecuzione
        ```
        $ mvn clean install package
        $ OPZIONALE: docker system prune -a
        $ docker build -t esempio01:1.1 .
        $ docker run -d -p 5555:5051 esempio01:1.1
        $ curl http://localhost:5555/api/
        $ curl http://localhost:5555/api/response
        $ docker ps
        $ docker stop xxxxxxx
        ```
    * Push dell'immagine nel repository (come sopra)
        ```
        $ docker build -t esempio01:1.1 .
        $ docker tag esempio01:1.1 alnao/esempio01:1.1
        $ docker push alnao/esempio01:1.1
        ```
    * Provare l'immagine direttamente 
        ```
        $ docker pull alnao/esempio01:1.1
        $ docker run -d -p 5556:5051 alnao/esempio01:1.1
        $ curl http://localhost:5556/api/
        $ curl http://localhost:5556/api/response
        $ docker logs 9c82e2c2f7d8
        $ docker ps
        $ docker stop xxxxxxx
        $ OPZIONALE: docker system prune -a
        ```

* Eseguire il deploy e lanciare il service su cluster kubernetes già esistente, funziona solo con utente *root* a meno di non usare minukube (e funziona):
    ```
	# kubectl apply -f deployment.yml 
	# kubectl apply -f service.yml 
	# kubectl get pods
	# kubectl get services
	# kubectl get deployments
    ```
    * Test di esecuzione, recuperare l'indirizzo dal comando `kubectl get services` e poi eseguire il comando
        ```
        $ curl 10.X.Y.Z:5081/api/response
        $ curl 10.X.Y.Z:5081/api/
        ```
    * Log e informazioni utili come gli endpoint interni
        ```
        # kubectl describe deployment esempio01-deployment
        # kubectl describe service esempio01-service
        # kubectl logs -f deployment/esempio01-deployment
        ```
    * Eseguire lo scale del servizio
        ```
        # kubectl scale deployment esempio01-app --replicas=3
        # kubectl get events
        # kubectl get events --sort-by='.lastTimestamp'
        ```
    * Rimuovere tutte le esecuzioni
        ```
        # kubectl get pods
        # kubectl get services 
        # kubectl get deployments
        # kubectl delete deployment esempio01-deployment
        # kubectl delete service esempio01-service
        # kubectl get all -l app=esempio01-app
        # docker system prune -a
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
