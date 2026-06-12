package it.alnao.javafx.controlroom.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a tab configuration: a tab label and its list of scripts.
 */
public class TabConfig {
    private int index;
    private String label;
    private List<ScriptEntry> scripts = new ArrayList<>();

    public TabConfig() {
    }

    public TabConfig(int index, String label) {
        this.index = index;
        this.label = label;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<ScriptEntry> getScripts() {
        return scripts;
    }

    public void setScripts(List<ScriptEntry> scripts) {
        this.scripts = scripts;
    }

    public void addScript(ScriptEntry script) {
        scripts.add(script);
    }
}
