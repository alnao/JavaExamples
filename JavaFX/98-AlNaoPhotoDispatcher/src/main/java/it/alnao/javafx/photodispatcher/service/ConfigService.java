package it.alnao.javafx.photodispatcher.service;

import it.alnao.javafx.photodispatcher.model.FolderConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

public class ConfigService {
    private static final String CONFIG_DIR = System.getProperty("user.home") + "/.alnaoPhotoDispatcher";
    private static final String CONFIG_FILE = CONFIG_DIR + "/config.properties";

    private static final String DEFAULT_SOURCE_PATH = "/home/alnao/images/source/";
    private static final String DEFAULT_DEST_PATH = "/home/alnao/images/destination/";

    private static final String CONFIG_LIST_KEY = "config.list";
    private static final String ACTIVE_CONFIG_KEY = "active.config";

    private final List<FolderConfig> folderConfigs = new ArrayList<>();
    private FolderConfig activeConfig;

    public List<FolderConfig> getFolderConfigs() {
        return folderConfigs;
    }

    public FolderConfig getActiveConfig() {
        return activeConfig;
    }

    public void setActiveConfig(FolderConfig activeConfig) {
        this.activeConfig = activeConfig;
    }

    public void setFolderConfigs(List<FolderConfig> configs) {
        folderConfigs.clear();
        folderConfigs.addAll(configs);
    }

    public Optional<FolderConfig> findByName(String name) {
        return folderConfigs.stream().filter(cfg -> cfg.getName().equals(name)).findFirst();
    }

    public void loadConfig() {
        File configFile = new File(CONFIG_FILE);
        Properties props = new Properties();

        folderConfigs.clear();

        if (configFile.exists()) {
            try (FileInputStream in = new FileInputStream(configFile)) {
                props.load(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String legacySource = props.getProperty("source.path");
        String legacyDest = props.getProperty("dest.path");
        if ((legacySource != null || legacyDest != null) && props.getProperty(CONFIG_LIST_KEY) == null) {
            folderConfigs.add(new FolderConfig("Default",
                    legacySource != null ? legacySource : DEFAULT_SOURCE_PATH,
                    legacyDest != null ? legacyDest : DEFAULT_DEST_PATH));
        }

        String configNames = props.getProperty(CONFIG_LIST_KEY, "").trim();
        if (!configNames.isEmpty()) {
            String[] names = configNames.split(",");
            for (String rawName : names) {
                String name = rawName.trim();
                if (name.isEmpty()) continue;
                String source = props.getProperty(configKey(name, "source"), "");
                String dest = props.getProperty(configKey(name, "dest"), "");
                if (!source.isBlank() && !dest.isBlank()) {
                    folderConfigs.add(new FolderConfig(name, source, dest));
                }
            }
        }

        if (folderConfigs.isEmpty()) {
            folderConfigs.add(new FolderConfig("Default", DEFAULT_SOURCE_PATH, DEFAULT_DEST_PATH));
        }

        String activeName = props.getProperty(ACTIVE_CONFIG_KEY, folderConfigs.get(0).getName());
        activeConfig = folderConfigs.stream()
                .filter(cfg -> cfg.getName().equals(activeName))
                .findFirst()
                .orElse(folderConfigs.get(0));

        saveConfig();
    }

    public void saveConfig() {
        try {
            File configDir = new File(CONFIG_DIR);
            if (!configDir.exists()) configDir.mkdirs();

            Properties props = new Properties();
            String configList = folderConfigs.stream()
                    .map(FolderConfig::getName)
                    .collect(Collectors.joining(","));
            props.setProperty(CONFIG_LIST_KEY, configList);

            if (activeConfig != null) {
                props.setProperty(ACTIVE_CONFIG_KEY, activeConfig.getName());
            }

            for (FolderConfig cfg : folderConfigs) {
                props.setProperty(configKey(cfg.getName(), "source"), cfg.getSourcePath());
                props.setProperty(configKey(cfg.getName(), "dest"), cfg.getDestPath());
            }

            try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
                props.store(out, "AlNao Photo Dispatcher Configuration");
            }
        } catch (IOException e) {
            throw new RuntimeException("Errore salvataggio configurazione: " + e.getMessage(), e);
        }
    }

    private String configKey(String configName, String field) {
        return "config." + sanitizeConfigName(configName) + "." + field;
    }

    private String sanitizeConfigName(String name) {
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}