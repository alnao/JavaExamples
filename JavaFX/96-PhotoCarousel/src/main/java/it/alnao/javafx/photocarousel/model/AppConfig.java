package it.alnao.javafx.photocarousel.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Data model for storing Photo Carousel application configuration in JSON.
 */
public class AppConfig {

    private List<String> folders = new ArrayList<>();
    private int intervalSeconds = 3;
    private boolean overlap10 = false;
    private boolean centerLarge = true;
    private boolean allowOverflow = false;
    private boolean includeSubfolders = true;

    public AppConfig() {
    }

    public List<String> getFolders() {
        if (folders != null) {
            folders.sort(String.CASE_INSENSITIVE_ORDER);
        }
        return folders;
    }

    public void setFolders(List<String> folders) {
        if (folders != null) {
            this.folders = new ArrayList<>(folders);
            this.folders.sort(String.CASE_INSENSITIVE_ORDER);
        } else {
            this.folders = new ArrayList<>();
        }
    }

    public int getIntervalSeconds() {
        return intervalSeconds;
    }

    public void setIntervalSeconds(int intervalSeconds) {
        this.intervalSeconds = Math.max(1, intervalSeconds);
    }

    public boolean isOverlap10() {
        return overlap10;
    }

    public void setOverlap10(boolean overlap10) {
        this.overlap10 = overlap10;
    }

    public boolean isCenterLarge() {
        return centerLarge;
    }

    public void setCenterLarge(boolean centerLarge) {
        this.centerLarge = centerLarge;
    }

    public boolean isAllowOverflow() {
        return allowOverflow;
    }

    public void setAllowOverflow(boolean allowOverflow) {
        this.allowOverflow = allowOverflow;
    }

    public boolean isIncludeSubfolders() {
        return includeSubfolders;
    }

    public void setIncludeSubfolders(boolean includeSubfolders) {
        this.includeSubfolders = includeSubfolders;
    }
}
