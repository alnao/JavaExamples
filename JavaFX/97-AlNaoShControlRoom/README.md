# 97 - AlNao Sh Control Room

A JavaFX **Control Room** application for monitoring server environments and executing shell scripts with real-time output.

## Features

- **Status Monitoring Header**: Red/green LED indicators show live availability of configured services (HTTP health checks)
- **Tabbed Script Execution**: Each tab groups related shell scripts with one-click execution
- **Real-time Output**: Script stdout/stderr is streamed live into a terminal-style text area
- **Concurrent Tabs**: Each tab runs independently — you can run scripts in multiple tabs simultaneously
- **Single-script per Tab**: Prevents accidental double-execution within the same tab
- **Stop Button**: Forcibly kills a running script and its child processes
- **Configurable**: Manage monitors, tabs, and scripts via the built-in Settings UI (saved to `~/.alnaoShControlRoom/config.json`).

## Requirements

- Java 21+
- Maven 3.8+
- JavaFX 21 (managed by Maven)

## Configuration

Configuration is now managed directly from the application's interface. 
Click the **⚙ Settings** button in the top right corner to open the Settings UI.

From there you can:
- **Monitors**: Add, update, or remove status indicators (HTTP health checks).
- **Tabs & Scripts**: Manage your tabs and the shell scripts associated with them.
- **General**: Update global settings like the polling refresh interval in seconds.

Your configuration is automatically saved in JSON format at:
`~/.alnaoShControlRoom/config.json`

## Build & Run

```bash
# Build
mvn clean package -DskipTests

# Run
./run.sh

# Or directly with Maven
mvn javafx:run
```

## Architecture

```
ControlRoomApp          → JavaFX Application entry point
├── controller/
│   └── ControlRoomController  → Builds UI, wires services
├── model/
│   ├── MonitorEntry           → URL health check record
│   ├── ScriptEntry            → Script button config record
│   └── TabConfig              → Tab grouping of scripts
└── service/
    ├── ConfigService          → Parses .env configuration
    ├── StatusChecker          → Periodic HTTP health checks
    └── ScriptRunner           → Async script execution + output streaming
```


# IA
Progetto creato con i prompt:
> ciao, i wahha you create a "97-AlNaoShControlRoom" using java 21 and JavaFX. This new application is a "control room" of servers environment (dev for now, feature in production). This application hava a header bar with "red/green" indicators, every indicator call a url (with curl) to understand if a specific url:port is acrive , url is a parametric list into env file , example [localhost:8042 for backend, localhost:5172 for admin, localhost:5174 for website, api.test.games.paths for cloud backend, ... ]. After i wanna tabs, from tab #1 to tab #X . Every  tab is with a list of buttons, every button run a sh script and show the message into textbox (or similar), a button to end/stop the sh if there is a sh already running; list of sh is a list of sh files [ /mnt/.../a.sh, /mnt/dir2/.../b.sh], note: every tabs have a list of scripts/buttons but only one textbox, will be not possibile run multiple script into same tab but will be possibiler un multie scripts in different tabs, every tabs with textarea-result.   

> on toolbar, for every url add two icons-button: "stop" to kill process  using that port (disponibile only if url contains localhost) and "play" to open browser into that url  

> on tollbar add a warning "something is running" on yellow after "control room" title  

> i wanna this change: if url is not avaible don't use red but without background (trasparent to see natural background). i wanna tab title area with same thinks: is there is a sh running background title on green


## Creazione icona per desktop Linux
```
cd ~/.local/share/applications/
nano PathsGamesControlRoom.desktop 
    [Desktop Entry]
    Encoding=UTF-8
    Name=Paths Games Control Room
    Exec=/mnt/Dati4/Workspace/JavaExamples/JavaFX/97-AlNaoShControlRoom/run.sh
    Icon=/usr/share/icons/hicolor/32x32/apps/kiriki.png
    Terminal=false
    Type=Application
    Categories=Science;
```


# AlNao.it
Tutti i codici sorgente e le informazioni presenti in questo repository sono frutto di un attento e paziente lavoro di sviluppo da parte di Alberto Nao, che si è impegnato a verificarne la correttezza nella misura massima possibile. Qualora parte del codice o dei contenuti sia stato tratto da fonti esterne, la relativa provenienza viene sempre citata, nel rispetto della trasparenza e della proprietà intellettuale. 


Alcuni contenuti e porzioni di codice presenti in questo repository sono stati realizzati anche grazie al supporto di strumenti di intelligenza artificiale, il cui contributo ha permesso di arricchire e velocizzare la produzione del materiale. Ogni informazione e frammento di codice è stato comunque attentamente verificato e validato, con l’obiettivo di garantire la massima qualità e affidabilità dei contenuti offerti. 


Per ulteriori dettagli, approfondimenti o richieste di chiarimento, si invita a consultare il sito [alnao.it](https://www.alnao.it/).


## License
Public projects 
<a href="https://it.wikipedia.org/wiki/GNU_General_Public_License"  valign="middle"><img src="https://img.shields.io/badge/License-GNU-blue" style="height:22px;"  valign="middle"></a> 
*Free Software!*

E' garantito il permesso di copiare, distribuire e/o modificare questo documento in base ai termini della GNU Free Documentation License, Versione 1.2 o ogni versione successiva pubblicata dalla Free Software Foundation. Permission is granted to copy, distribute and/or modify this document under the terms of the GNU Free Documentation License, Version 1.2 or any later version published by the Free Software Foundation.
