# Esempio02db Minikube SOLO Mysql

In questo documento viene descritto come configurare e avviare un'istanza di MySQL 8 all'interno di un cluster Kubernetes locale creato con Minikube .
Prompt iniziale per IA ```Dammi un esempio per far partire un servizio su minikube con mysql```.

* Requisiti minimi
    - Minikube
    - kubectl
    - Docker (opzionale ma consigliato)


* Componenti
    - Deployment
    - Service
    - ConfigMap (per la configurazione)
    - PersistentVolumeClaim (per dati persistenti)

* Comando iniziale per avviare minukube
    ```
    minikube delete --all --purge
    minikube start --driver=docker --memory=2048 --cpus=2
    minikube status
    kubectl get nodes
    ```
* File creati
    - mysql-deployment.yaml
    - mysql-pvc.yaml
    - mysql-service.yaml
* Comandi per l'avvio 
    ```
    kubectl apply -f mysql-pvc.yaml
    kubectl apply -f mysql-deployment.yaml
    kubectl apply -f mysql-service.yaml
    ```
* Comandi di verifica:
    ```
    kubectl get pods,pvc,services
    minikube ip
    ```
* Comando per collegarsi al DB
    ``` 
    kubectl exec -it pod/mysql-8dfb596b-b659s -- mysql -u root -p
    ```
    Inserisci la password (password123) e sarai dentro il prompt di MySQL.
* Client da riga di comando
    ```
    kubectl run mysql-client --image=mysql:8 -it --rm --restart=Never -- mysql -h mysql -u root -p
    ```    
* Pulizia finale
    ```
    kubectl delete service mysql
    kubectl delete deployment mysql
    kubectl delete pvc mysql-pvc
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
