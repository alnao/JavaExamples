#!/bin/bash
# Script di lancio per AlNao Sh Control Room
# Gestisce automaticamente i parametri JavaFX necessari

cd "$(dirname "$0")"

if [ ! -f "target/sh-control-room-1.0-SNAPSHOT.jar" ]; then
    echo "Eseguo mvn clean install..."
    mvn -q clean install package -DskipTests
fi

mvn -q clean install package -DskipTests

java --module-path target/lib \
     --add-modules javafx.controls \
     -jar target/sh-control-room-1.0-SNAPSHOT.jar "$@"
