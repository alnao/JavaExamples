# Azure Manager JavaFX

Console Azure sviluppata con JavaFX e Azure SDK per Java che permette di visualizzare e gestire risorse Azure.

## Caratteristiche

- **Dashboard grafica** con menu laterale fisso per navigare tra i servizi Azure
- **Interfaccia intuitiva** con sidebar menu per accesso rapido ai servizi
- **Supporto multi-subscription** Azure
- **Visualizzazione di diverse risorse Azure**:
  - Resource Groups
  - Virtual Machines
  - Virtual Networks
  - Storage Accounts
  - SQL Servers
  - Web Apps
  - Function Apps
  - Azure Kubernetes Service (AKS)
  - Cosmos DB

## Prerequisiti

- **Java 17** o superiore
- **Maven 3.8+**
- **Credenziali Azure** configurate tramite:
  - Azure CLI (`az login`)
  - Variabili d'ambiente Azure
  - File di configurazione Azure
- **JavaFX** (gestito automaticamente da Maven)

## Struttura del Progetto

```
Azure/ManagerFx/
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/
        │   └── it/alnao/azure/managerfx/
        │       ├── Main.java                      # Classe principale
        │       ├── controller/
        │       │   └── MainController.java        # Controller JavaFX
        │       └── service/
        │           ├── AzureResourceService.java  # Servizio centrale Azure
        │           └── impl/                      # Servizi specifici (futuro)
        └── resources/
            ├── fxml/
            │   └── main.fxml                      # Layout interfaccia
            └── css/
                └── style.css                      # Stili CSS
```

## Dipendenze Principali

- **JavaFX 21.0.1** - Framework per l'interfaccia grafica
- **Azure Resource Manager 1.2.19** - SDK Azure principale
- **Azure Identity 1.11.1** - Autenticazione Azure
- **SLF4J 2.0.7** - Logging

## Configurazione Azure

### 1. Autenticazione tramite Azure CLI (Consigliato)

```bash
az login
az account set --subscription "YOUR_SUBSCRIPTION_ID"
```

### 2. Caricamento automatico delle Subscription

L'applicazione **carica automaticamente** tutte le subscription Azure disponibili al primo avvio.

Non è necessaria alcuna configurazione manuale! Le subscription vengono recuperate dinamicamente da Azure.

Se il caricamento automatico fallisce, puoi inserire manualmente il Subscription ID nella combobox.

Puoi ottenere il tuo Subscription ID con:

```bash
az account list --output table
```

## Compilazione ed Esecuzione

### Compilare il progetto

```bash
cd Azure/ManagerFx
mvn clean compile
```

### Eseguire l'applicazione

```bash
mvn javafx:run
```

### Creare un JAR eseguibile

```bash
mvn clean package
```

## Utilizzo

1. **Avvia l'applicazione** con `mvn javafx:run`
2. **Seleziona la Subscription** dalla tendina in alto
3. **Naviga tra i servizi** usando il menu laterale sulla sinistra
4. **Visualizza le risorse** nelle tabelle
5. **Aggiorna i dati** cliccando sul pulsante "Refresh"

## Servizi Azure Supportati

### Compute
- **Virtual Machines**: Visualizza tutte le VM con nome, dimensione, stato e location
- **AKS**: Azure Kubernetes Service clusters

### Networking
- **Virtual Networks**: Reti virtuali con address spaces

### Storage & Database
- **Storage Accounts**: Account di storage con SKU
- **SQL Servers**: Server SQL con versione
- **Cosmos DB**: Account Cosmos DB con tipo

### App Services
- **Web Apps**: Applicazioni web con stato
- **Function Apps**: Azure Functions con stato

### Management
- **Resource Groups**: Gruppi di risorse con location

## Architettura

### Service Layer
- `AzureResourceService`: Servizio centrale che gestisce:
  - Connessione ad Azure tramite DefaultAzureCredential
  - Recupero risorse per subscription
  - Gestione del ResourceManager

### Controller Layer
- `MainController`: Controller JavaFX che:
  - Gestisce l'interfaccia utente
  - Popola le tabelle con i dati
  - Gestisce il menu laterale e i pannelli
  - Esegue operazioni in background con Task

### View Layer
- `main.fxml`: Layout FXML con:
  - Sidebar menu per navigazione
  - StackPane con pannelli per ogni servizio
  - Tabelle per visualizzare le risorse
- `style.css`: Stili CSS personalizzati

## Autenticazione Azure

L'applicazione utilizza `DefaultAzureCredentialBuilder` che supporta:

1. **Environment Variables**: 
   - AZURE_CLIENT_ID
   - AZURE_TENANT_ID
   - AZURE_CLIENT_SECRET

2. **Managed Identity**: Per esecuzione su Azure

3. **Azure CLI**: `az login`

4. **Visual Studio Code**: Autenticazione VSCode

5. **IntelliJ**: Autenticazione IntelliJ

## Note

- Le **credenziali Azure** devono essere configurate prima di eseguire l'applicazione (tramite `az login`)
- Le **Subscription** vengono caricate automaticamente all'avvio
- Alcune risorse potrebbero richiedere permessi specifici
- L'applicazione è **read-only** (solo visualizzazione)

## Sviluppi Futuri

Questo progetto è stato creato come esempio di integrazione tra JavaFX e Azure SDK.
È ispirato al progetto Python Flask disponibile in `/PythonExamples/AWS/Managers/PanoramicResources/`.

- [ ] Operazioni su Virtual Machines (Start/Stop/Restart)
- [ ] Visualizzazione dettagli risorse
- [ ] Supporto per più subscription contemporaneamente
- [ ] Export dati in CSV/JSON
- [ ] Filtri e ricerca nelle tabelle
- [ ] Grafici e statistiche
- [ ] Gestione Container Instances
- [ ] Azure Monitor e Log Analytics
- [ ] Azure Key Vault
- [ ] Azure Service Bus





# AlNao.it
Tutti i codici sorgente e le informazioni presenti in questo repository sono frutto di un attento e paziente lavoro di sviluppo da parte di Alberto Nao, che si è impegnato a verificarne la correttezza nella misura massima possibile. Qualora parte del codice o dei contenuti sia stato tratto da fonti esterne, la relativa provenienza viene sempre citata, nel rispetto della trasparenza e della proprietà intellettuale. 


Alcuni contenuti e porzioni di codice presenti in questo repository sono stati realizzati anche grazie al supporto di strumenti di intelligenza artificiale, il cui contributo ha permesso di arricchire e velocizzare la produzione del materiale. Ogni informazione e frammento di codice è stato comunque attentamente verificato e validato, con l’obiettivo di garantire la massima qualità e affidabilità dei contenuti offerti. 


Per ulteriori dettagli, approfondimenti o richieste di chiarimento, si invita a consultare il sito [alnao.it](https://www.alnao.it/).


## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*

E' garantito il permesso di copiare, distribuire e/o modificare questo documento in base ai termini della GNU Free Documentation License, Versione 1.2 o ogni versione successiva pubblicata dalla Free Software Foundation. Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.2 or any later version published by the Free Software Foundation.