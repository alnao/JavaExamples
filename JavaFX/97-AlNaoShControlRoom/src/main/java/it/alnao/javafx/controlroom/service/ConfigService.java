package it.alnao.javafx.controlroom.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.alnao.javafx.controlroom.model.AppConfig;
import it.alnao.javafx.controlroom.model.MonitorEntry;
import it.alnao.javafx.controlroom.model.ScriptEntry;
import it.alnao.javafx.controlroom.model.TabConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Loads configuration from ~/.alnaoShControlRoom/config.json.
 */
public class ConfigService {

    private AppConfig appConfig = new AppConfig();
    private final ObjectMapper mapper = new ObjectMapper();

    public void load() {
        Path configPath = resolveConfigPath();
        if (!Files.exists(configPath)) {
            System.err.println("[ConfigService] config.json not found. Creating default empty config.");
            try {
                Files.createDirectories(configPath.getParent());
                save();
            } catch (IOException e) {
                System.err.println("[ConfigService] Could not create default config: " + e.getMessage());
            }
            return;
        }
        System.out.println("[ConfigService] Loading config from: " + configPath);

        try {
            appConfig = mapper.readValue(configPath.toFile(), AppConfig.class);
        } catch (IOException e) {
            System.err.println("[ConfigService] Error reading config.json: " + e.getMessage());
        }

        System.out.println("[ConfigService] Loaded " + appConfig.getMonitors().size() + " monitors, " + appConfig.getTabs().size() + " tabs.");
    }

    public void save() {
        Path configPath = resolveConfigPath();
        try {
            Files.createDirectories(configPath.getParent());
            mapper.writerWithDefaultPrettyPrinter().writeValue(configPath.toFile(), appConfig);
            System.out.println("[ConfigService] Config saved to " + configPath);
        } catch (IOException e) {
            System.err.println("[ConfigService] Error saving config.json: " + e.getMessage());
        }
    }

    private Path resolveConfigPath() {
        return Paths.get(System.getProperty("user.home"), ".alnaoShControlRoom", "config.json");
    }

    public List<MonitorEntry> getMonitors() {
        return Collections.unmodifiableList(appConfig.getMonitors());
    }

    public void setMonitors(List<MonitorEntry> monitors) {
        appConfig.setMonitors(new ArrayList<>(monitors));
    }

    public List<TabConfig> getTabs() {
        return appConfig.getTabs().stream()
                .sorted(Comparator.comparingInt(TabConfig::getIndex))
                .toList();
    }

    public void setTabs(List<TabConfig> tabs) {
        appConfig.setTabs(new ArrayList<>(tabs));
    }

    public int getRefreshSeconds() {
        return appConfig.getRefreshSeconds();
    }

    public void setRefreshSeconds(int seconds) {
        appConfig.setRefreshSeconds(seconds);
    }
}
