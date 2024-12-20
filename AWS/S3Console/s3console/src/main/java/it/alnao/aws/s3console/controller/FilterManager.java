package it.alnao.aws.s3console.controller;
import software.amazon.awssdk.services.s3.model.S3Object;
import javax.swing.SwingUtilities;

import it.alnao.aws.s3console.view.FileListPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class FilterManager {
    private final FileListPanel fileListPanel;
    private List<S3Object> originalFiles;
    private String currentFilter = "";
    private boolean useRegex = false;
    private boolean caseSensitive = false;
    private FilterType filterType = FilterType.CONTAINS;

    public enum FilterType {
        CONTAINS,
        STARTS_WITH,
        ENDS_WITH,
        EXACT_MATCH,
        REGEX
    }

    public FilterManager(FileListPanel fileListPanel) {
        this.fileListPanel = fileListPanel;
        this.originalFiles = new ArrayList<>();
    }

    public void setOriginalFiles(List<S3Object> files) {
        this.originalFiles = new ArrayList<>(files);
        applyFilter();
    }

    public void setFilter(String filter) {
        this.currentFilter = filter;
        applyFilter();
    }

    public void setFilterType(FilterType type) {
        this.filterType = type;
        this.useRegex = type == FilterType.REGEX;
        applyFilter();
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
        applyFilter();
    }

    public void clearFilter() {
        currentFilter = "";
        applyFilter();
    }

    private void applyFilter() {
        if (originalFiles == null) return;

        SwingUtilities.invokeLater(() -> {
            List<S3Object> filteredFiles;
            
            if (currentFilter.isEmpty()) {
                filteredFiles = new ArrayList<>(originalFiles);
            } else {
                Predicate<S3Object> filterPredicate = createFilterPredicate();
                filteredFiles = originalFiles.stream()
                    .filter(filterPredicate)
                    .collect(Collectors.toList());
            }

            fileListPanel.updateFileList(filteredFiles);
        });
    }

    private Predicate<S3Object> createFilterPredicate() {
        if (useRegex) {
            try {
                Pattern pattern = caseSensitive 
                    ? Pattern.compile(currentFilter)
                    : Pattern.compile(currentFilter, Pattern.CASE_INSENSITIVE);
                return file -> pattern.matcher(getFileName(file)).matches();
            } catch (PatternSyntaxException e) {
                // Se l'espressione regolare non è valida, torna al filtro normale
                return createBasicFilterPredicate();
            }
        }
        return createBasicFilterPredicate();
    }

    private Predicate<S3Object> createBasicFilterPredicate() {
        return file -> {
            String fileName = getFileName(file);
            String filter = caseSensitive ? currentFilter : currentFilter.toLowerCase();
            if (!caseSensitive) {
                fileName = fileName.toLowerCase();
            }

            return switch (filterType) {
                case STARTS_WITH -> fileName.startsWith(filter);
                case ENDS_WITH -> fileName.endsWith(filter);
                case EXACT_MATCH -> fileName.equals(filter);
                case CONTAINS -> fileName.contains(filter);
                default -> fileName.contains(filter);
            };
        };
    }

    private String getFileName(S3Object file) {
        String key = file.key();
        int lastSlash = key.lastIndexOf('/');
        return lastSlash >= 0 ? key.substring(lastSlash + 1) : key;
    }

    public FilterType getFilterType() {
        return filterType;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public String getCurrentFilter() {
        return currentFilter;
    }

    public static class FileInfo {
        private final S3Object s3Object;
        private final String displayName;
        private final String size;
        private final String lastModified;

        public FileInfo(S3Object s3Object) {
            this.s3Object = s3Object;
            this.displayName = extractDisplayName(s3Object.key());
            this.size = formatSize(s3Object.size());
            this.lastModified = formatDate(s3Object.lastModified());
        }

        private String extractDisplayName(String key) {
            int lastSlash = key.lastIndexOf('/');
            return lastSlash >= 0 ? key.substring(lastSlash + 1) : key;
        }

        private String formatSize(long bytes) {
            if (bytes < 1024) return bytes + " B";
            int exp = (int) (Math.log(bytes) / Math.log(1024));
            String pre = "KMGTPE".charAt(exp-1) + "";
            return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
        }

        private String formatDate(java.time.Instant instant) {
            return java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(java.time.ZoneId.systemDefault())
                .format(instant);
        }

        public S3Object getS3Object() {
            return s3Object;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getSize() {
            return size;
        }

        public String getLastModified() {
            return lastModified;
        }

        public boolean isDirectory() {
            return s3Object.key().endsWith("/");
        }
    }

    // Metodi di utility per la conversione tra S3Object e FileInfo
    public List<FileInfo> convertToFileInfo(List<S3Object> s3Objects) {
        return s3Objects.stream()
            .map(FileInfo::new)
            .collect(Collectors.toList());
    }

    // Hook per l'aggiornamento dell'interfaccia utente
    @FunctionalInterface
    public interface FilterResultCallback {
        void onFilterComplete(List<FileInfo> filteredFiles);
    }

    private FilterResultCallback filterResultCallback;

    public void setFilterResultCallback(FilterResultCallback callback) {
        this.filterResultCallback = callback;
    }

    protected void notifyFilterComplete(List<S3Object> filteredFiles) {
        if (filterResultCallback != null) {
            List<FileInfo> fileInfos = convertToFileInfo(filteredFiles);
            SwingUtilities.invokeLater(() -> filterResultCallback.onFilterComplete(fileInfos));
        }
    }
}