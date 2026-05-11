package it.alnao.javafx.controlroom.model;

/**
 * Represents a script button configuration within a tab.
 */
public record ScriptEntry(int tabIndex, int scriptIndex, String label, String scriptPath, String params) {
}
