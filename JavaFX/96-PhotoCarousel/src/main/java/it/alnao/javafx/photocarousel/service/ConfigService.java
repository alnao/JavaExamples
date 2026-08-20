package it.alnao.javafx.photocarousel.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.alnao.javafx.photocarousel.model.AppConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for loading and persisting Photo Carousel configuration to JSON.
 */
public class ConfigService {

    private AppConfig appConfig = new AppConfig();
    private final ObjectMapper mapper = new ObjectMapper();

    public ConfigService() {
        load();
    }

    public void load() {
        Path configPath = resolveConfigPath();
        if (!Files.exists(configPath)) {
            System.out.println("[ConfigService] File non trovato. Creazione configurazione default: " + configPath);
            try {
                Files.createDirectories(configPath.getParent());
                save();
            } catch (IOException e) {
                System.err.println("[ConfigService] Impossibile creare cartella di config: " + e.getMessage());
            }
            return;
        }

        try {
            appConfig = mapper.readValue(configPath.toFile(), AppConfig.class);
            System.out.println("[ConfigService] Configurazione caricata da: " + configPath);
        } catch (IOException e) {
            System.err.println("[ConfigService] Errore lettura json: " + e.getMessage());
        }
    }

    public void save() {
        Path configPath = resolveConfigPath();
        try {
            if (configPath.getParent() != null) {
                Files.createDirectories(configPath.getParent());
            }
            mapper.writerWithDefaultPrettyPrinter().writeValue(configPath.toFile(), appConfig);
            System.out.println("[ConfigService] Configurazione salvata in: " + configPath);
        } catch (IOException e) {
            System.err.println("[ConfigService] Errore salvataggio config: " + e.getMessage());
        }
    }

    private Path resolveConfigPath() {
        Path userHomePath = Paths.get(System.getProperty("user.home"), ".alnaoPhotoCarousel", "config.json");
        return userHomePath;
    }

    public List<String> getFolders() {
        return new ArrayList<>(appConfig.getFolders());
    }

    public void setFolders(List<String> folders) {
        appConfig.setFolders(folders);
        save();
    }

    public void addFolder(String folderPath) {
        if (folderPath != null && !folderPath.trim().isEmpty() && !appConfig.getFolders().contains(folderPath)) {
            appConfig.getFolders().add(folderPath);
            save();
        }
    }

    public void removeFolder(String folderPath) {
        if (appConfig.getFolders().remove(folderPath)) {
            save();
        }
    }

    public int getIntervalSeconds() {
        return appConfig.getIntervalSeconds();
    }

    public void setIntervalSeconds(int seconds) {
        appConfig.setIntervalSeconds(seconds);
        save();
    }

    public boolean isOverlap10() {
        return appConfig.isOverlap10();
    }

    public void setOverlap10(boolean overlap) {
        appConfig.setOverlap10(overlap);
        save();
    }

    public boolean isCenterLarge() {
        return appConfig.isCenterLarge();
    }

    public void setCenterLarge(boolean centerLarge) {
        appConfig.setCenterLarge(centerLarge);
        save();
    }

    public boolean isAllowOverflow() {
        return appConfig.isAllowOverflow();
    }

    public void setAllowOverflow(boolean allowOverflow) {
        appConfig.setAllowOverflow(allowOverflow);
        save();
    }

    public boolean isIncludeSubfolders() {
        return appConfig.isIncludeSubfolders();
    }

    public void setIncludeSubfolders(boolean includeSubfolders) {
        appConfig.setIncludeSubfolders(includeSubfolders);
        save();
    }

    public AppConfig getAppConfig() {
        return appConfig;
    }
}
