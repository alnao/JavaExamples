#!/bin/bash
# Script di lancio per AlNao Photo Carousel
# Gestisce automaticamente i parametri JavaFX e le dipendenze

cd "$(dirname "$0")"

if [ ! -f "target/photo-carousel-1.0-SNAPSHOT.jar" ]; then
    echo "JAR non trovato. Eseguo mvn package..."
    mvn clean package -DskipTests
fi

java --module-path target/lib \
     --add-modules javafx.controls \
     -jar target/photo-carousel-1.0-SNAPSHOT.jar "$@"
