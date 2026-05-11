package it.alnao.javafx.controlroom.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a tab configuration: a tab label and its list of scripts.
 */
public class TabConfig {
    private final int index;
    private final String label;
    private final List<ScriptEntry> scripts = new ArrayList<>();

    public TabConfig(int index, String label) {
        this.index = index;
        this.label = label;
    }

    public int getIndex() {
        return index;
    }

    public String getLabel() {
        return label;
    }

    public List<ScriptEntry> getScripts() {
        return scripts;
    }

    public void addScript(ScriptEntry script) {
        scripts.add(script);
    }
}
