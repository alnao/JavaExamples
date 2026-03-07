package it.alnao.javafx.photodispatcher.service;

import java.io.IOException;
import java.nio.file.*;

/**
 * Gestisce lo spostamento fisico dei file immagine verso le cartelle di destinazione.
 * In caso di conflitto di nome aggiunge un suffisso con timestamp.
 */
public class FileMoveService {

    /**
     * Sposta sourceFile nella cartella targetDir.
     * Se esiste già un file con lo stesso nome, aggiunge un suffisso _<timestamp>.
     *
     * @return il Path del file nella destinazione
     */
    public Path moveTo(Path sourceFile, Path targetDir) throws IOException {
        Files.createDirectories(targetDir);
        Path target = targetDir.resolve(sourceFile.getFileName());

        if (Files.exists(target)) {
            String fileName = sourceFile.getFileName().toString();
            int dot = fileName.lastIndexOf('.');
            String base = dot > 0 ? fileName.substring(0, dot) : fileName;
            String ext  = dot > 0 ? fileName.substring(dot) : "";
            target = targetDir.resolve(base + "_" + System.currentTimeMillis() + ext);
        }

        return Files.move(sourceFile, target);
    }

    /**
     * Sposta sourceFile direttamente nella cartella root di destinazione.
     */
    public Path moveToRoot(Path sourceFile, Path destinationRoot) throws IOException {
        return moveTo(sourceFile, destinationRoot);
    }
}