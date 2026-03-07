package it.alnao.javafx.photodispatcher.model;

public class FolderConfig {
    private final String name;
    private final String sourcePath;
    private final String destPath;

    public FolderConfig(String name, String sourcePath, String destPath) {
        this.name = name;
        this.sourcePath = normalizePath(sourcePath);
        this.destPath = normalizePath(destPath);
    }

    public String getName() {
        return name;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public String getDestPath() {
        return destPath;
    }

    @Override
    public String toString() {
        return name;
    }

    private static String normalizePath(String path) {
        if (path == null) return "";
        String trimmed = path.trim();
        if (trimmed.isEmpty()) return "";
        if (!trimmed.endsWith("/")) {
            return trimmed + "/";
        }
        return trimmed;
    }
}