package it.alnao.javafx.controlroom.model;

import java.util.ArrayList;
import java.util.List;

public class AppConfig {
    private int refreshSeconds = 30;
    private List<MonitorEntry> monitors = new ArrayList<>();
    private List<TabConfig> tabs = new ArrayList<>();
    private WindowConfig window = new WindowConfig();

    public AppConfig() {}

    public int getRefreshSeconds() { return refreshSeconds; }
    public void setRefreshSeconds(int refreshSeconds) { this.refreshSeconds = refreshSeconds; }

    public List<MonitorEntry> getMonitors() { return monitors; }
    public void setMonitors(List<MonitorEntry> monitors) { this.monitors = monitors; }

    public List<TabConfig> getTabs() { return tabs; }
    public void setTabs(List<TabConfig> tabs) { this.tabs = tabs; }

    public WindowConfig getWindow() { return window; }
    public void setWindow(WindowConfig window) { this.window = window; }

    public static class WindowConfig {
        private Double x;
        private Double y;
        private Double width;
        private Double height;
        private boolean maximized;

        public WindowConfig() {}

        public Double getX() { return x; }
        public void setX(Double x) { this.x = x; }

        public Double getY() { return y; }
        public void setY(Double y) { this.y = y; }

        public Double getWidth() { return width; }
        public void setWidth(Double width) { this.width = width; }

        public Double getHeight() { return height; }
        public void setHeight(Double height) { this.height = height; }

        public boolean isMaximized() { return maximized; }
        public void setMaximized(boolean maximized) { this.maximized = maximized; }
    }
}
