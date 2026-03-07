#!/bin/bash
# Script di lancio per AlNao Photo Dispatcher
# Gestisce automaticamente i parametri JavaFX necessari

cd "$(dirname "$0")"

if [ ! -f "target/photo-dispatcher-1.0-SNAPSHOT.jar" ]; then
    echo "JAR non trovato. Eseguo mvn package..."
    mvn -q clean package -DskipTests
fi

java --module-path target/lib \
     --add-modules javafx.controls,javafx.fxml \
     -jar target/photo-dispatcher-1.0-SNAPSHOT.jar "$@"
