# Esempio07basicAuth

Microservizio di autenticazione basato su Spring Boot 3.5, impiegando **Basic Authentication**, **controlli di accesso con `@PreAuthorize`**, documentazione API tramite **Swagger UI (OpenAPI 3)** e **Spring Boot Actuator** esposto senza protezione.

## Descrizione

Il progetto realizza un'applicazione REST sicura, dove le rotte sono protette per ruolo utente e documentate automaticamente. Swagger è configurato per supportare l’autenticazione Basic all'interno dell'interfaccia web. L'infrastruttura di sicurezza si basa esclusivamente su credenziali statiche in memoria. Actuator è configurato per essere accessibile liberamente.

I Componenti principali sono: 
- **Spring Boot 3.5**
- **Spring Security 6**
- **Springdoc OpenAPI 2.x** (per Swagger UI)
- **Spring Boot Actuator**
- **Controlli di accesso a livello di metodo** con `@PreAuthorize("hasRole(...)")`


Le funzionalità principali sono:
- Proteggere le API REST con Basic Authentication
- Definire utenti e ruoli in memoria
- Applicare sicurezza per ruolo mediante `@PreAuthorize`
- Visualizzare e testare le API tramite Swagger con autenticazione integrata
- Esportare gli endpoint di monitoraggio di Actuator senza autenticazione


Note sulla sicurezza:
- Il sistema usa @EnableMethodSecurity per abilitare l'uso delle annotazioni @PreAuthorize.
- Swagger è protetto da Basic Auth.
- Gli endpoint Actuator (/actuator/**) sono pubblici per facilitare l'integrazione in ambienti di test o monitoraggio.


Le rotte esposte sono:

| Metodo | Path           | Accesso richiesto | Descrizione                     |
|--------|----------------|-------------------|---------------------------------|
| GET    | `/`            | Nessuna autenticazione | Endpoint pubblico          |
| GET    | `/home`        | Ruolo `USER`      | Accesso riservato agli utenti   |
| GET    | `/admin/hello` | Ruolo `ADMIN`     | Accesso riservato agli admin    |
| GET    | `/hello`       | Ruolo `USER`      | Endpoint generico autenticato   |
| GET    | `swagger-ui.html` | Ruolo `ADMIN`     | Accesso rivervato agli admin |
| GET    | `/actuator/**` | Nessuna autenticazione | Monitoraggio e metriche    |


## Autenticazione

Gli utenti e i ruoli sono definiti in memoria.

Esempio di configurazione:

```
User.withUsername("alnao").password("{noop}bellissimo").roles("USER")
User.withUsername("admin").password("{noop}potenza").roles("ADMIN")
```
# Comandi 
- Compilare ed eseguire il progetto con Maven:
    ```
    mvn clean install
    mvn spring-boot:run
    ```
- Endpoint delle api:
    ```
    curl localhost:8047

    curl http://localhost:8047/hello
    curl -u alnao:bellissimo http://localhost:8047/hello

    curl http://localhost:8047/admin/hello
    curl -u admin:potente http://localhost:8047/admin/hello

    curl http://localhost:8047/home
    curl -u alnao:bellissimo http://localhost:8047/home
    curl -u admin:potente http://localhost:8047/home
    ```
- Url di actuator
    ```
    http://localhost:8047/actuator
    http://localhost:8047/actuator/health
    ```
- Documentazione API (Swagger)
    Accedere alla documentazione interattiva tramite:
    ```
    http://localhost:8080/swagger-ui.html
    ```
    Autenticarsi tramite pulsante **Authorize** con credenziali definite nel sistema.


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
