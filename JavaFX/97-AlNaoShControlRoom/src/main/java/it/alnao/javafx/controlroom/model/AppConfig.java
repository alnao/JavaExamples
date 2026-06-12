package it.alnao.javafx.controlroom.model;

import java.util.ArrayList;
import java.util.List;

public class AppConfig {
    private int refreshSeconds = 30;
    private List<MonitorEntry> monitors = new ArrayList<>();
    private List<TabConfig> tabs = new ArrayList<>();

    public AppConfig() {}

    public int getRefreshSeconds() { return refreshSeconds; }
    public void setRefreshSeconds(int refreshSeconds) { this.refreshSeconds = refreshSeconds; }

    public List<MonitorEntry> getMonitors() { return monitors; }
    public void setMonitors(List<MonitorEntry> monitors) { this.monitors = monitors; }

    public List<TabConfig> getTabs() { return tabs; }
    public void setTabs(List<TabConfig> tabs) { this.tabs = tabs; }
}
