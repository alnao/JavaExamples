package it.alnao.javafx.controlroom.service;

import it.alnao.javafx.controlroom.model.MonitorEntry;
import it.alnao.javafx.controlroom.model.ScriptEntry;
import it.alnao.javafx.controlroom.model.TabConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Loads configuration from .env file.
 * 
 * Supports:
 *   MONITOR_<N>=<label>|<url>
 *   MONITOR_REFRESH_SECONDS=<int>
 *   TAB_<N>=<tab_label>
 *   TAB_<N>_SCRIPT_<M>=<button_label>|<script_path>
 */
public class ConfigService {

    private final List<MonitorEntry> monitors = new ArrayList<>();
    private final Map<Integer, TabConfig> tabs = new LinkedHashMap<>();
    private int refreshSeconds = 30;

    /**
     * Loads configuration from the .env file located next to the application JAR,
     * or from the project root when running from source.
     */
    public void load() {
        Path envPath = resolveEnvPath();
        if (envPath == null || !Files.exists(envPath)) {
            System.err.println("[ConfigService] .env file not found. Using empty config.");
            return;
        }
        System.out.println("[ConfigService] Loading config from: " + envPath);

        try (BufferedReader reader = Files.newBufferedReader(envPath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                int eqIdx = line.indexOf('=');
                if (eqIdx < 0) continue;

                String key = line.substring(0, eqIdx).trim();
                String value = line.substring(eqIdx + 1).trim();

                parseLine(key, value);
            }
        } catch (IOException e) {
            System.err.println("[ConfigService] Error reading .env: " + e.getMessage());
        }

        System.out.println("[ConfigService] Loaded " + monitors.size() + " monitors, " + tabs.size() + " tabs.");
    }

    private void parseLine(String key, String value) {
        if (key.equals("MONITOR_REFRESH_SECONDS")) {
            try {
                refreshSeconds = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.err.println("[ConfigService] Invalid MONITOR_REFRESH_SECONDS: " + value);
            }
            return;
        }

        // MONITOR_<N>=<label>|<url>
        if (key.startsWith("MONITOR_") && !key.contains("_SCRIPT_")) {
            String numStr = key.substring("MONITOR_".length());
            try {
                int idx = Integer.parseInt(numStr);
                String[] parts = value.split("\\|", 2);
                if (parts.length == 2) {
                    monitors.add(new MonitorEntry(idx, parts[0].trim(), parts[1].trim()));
                }
            } catch (NumberFormatException ignored) {
            }
            return;
        }

        // TAB_<N>_SCRIPT_<M>=<label>|<path>
        if (key.contains("_SCRIPT_")) {
            // e.g. TAB_1_SCRIPT_2
            String afterTab = key.substring("TAB_".length()); // "1_SCRIPT_2"
            String[] parts = afterTab.split("_SCRIPT_", 2);
            if (parts.length == 2) {
                try {
                    int tabIdx = Integer.parseInt(parts[0]);
                    int scriptIdx = Integer.parseInt(parts[1]);
                    String[] valParts = value.split("\\|", 3);
                    if (valParts.length == 2 || valParts.length == 3) {
                        TabConfig tab = tabs.get(tabIdx);
                        if (tab != null) {
                            tab.addScript(new ScriptEntry(tabIdx, scriptIdx, valParts[0].trim(), valParts[1].trim(), 
                                                            valParts.length > 2 ? valParts[2].trim() : ""));
                        }
                    }
                } catch (NumberFormatException ignored) {
                }
            }
            return;
        }

        // TAB_<N>=<label>
        if (key.startsWith("TAB_")) {
            String numStr = key.substring("TAB_".length());
            try {
                int idx = Integer.parseInt(numStr);
                tabs.put(idx, new TabConfig(idx, value));
            } catch (NumberFormatException ignored) {
            }
        }
    }

    /**
     * Try several paths for .env file location.
     */
    private Path resolveEnvPath() {
        // 1. Check user.dir (project root when running from IDE/maven)
        Path candidate = Paths.get(System.getProperty("user.dir"), ".env");
        if (Files.exists(candidate)) return candidate;

        // 2. Check next to JAR
        try {
            Path jarDir = Paths.get(
                ConfigService.class.getProtectionDomain().getCodeSource().getLocation().toURI()
            ).getParent();
            candidate = jarDir.resolve("../.env").normalize();
            if (Files.exists(candidate)) return candidate;
            candidate = jarDir.resolve(".env");
            if (Files.exists(candidate)) return candidate;
        } catch (Exception ignored) {
        }

        return null;
    }

    public List<MonitorEntry> getMonitors() {
        return Collections.unmodifiableList(monitors);
    }

    public List<TabConfig> getTabs() {
        return tabs.values().stream()
                .sorted(Comparator.comparingInt(TabConfig::getIndex))
                .toList();
    }

    public int getRefreshSeconds() {
        return refreshSeconds;
    }
}
