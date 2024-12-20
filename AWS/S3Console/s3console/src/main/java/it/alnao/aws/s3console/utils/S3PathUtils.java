package it.alnao.aws.s3console.utils;

// S3PathUtils.java
public class S3PathUtils {
    public static String normalizePath(String path) {
        if (path == null || path.isEmpty()) return "";
        
        String normalizedPath = path.replace('\\', '/');
        if (!normalizedPath.startsWith("/")) {
            normalizedPath = "/" + normalizedPath;
        }
        if (!normalizedPath.endsWith("/") && !normalizedPath.equals("/")) {
            normalizedPath += "/";
        }
        return normalizedPath;
    }
    
    public static String getParentPath(String path) {
        path = normalizePath(path);
        if (path.equals("/")) return null;
        
        int lastSlash = path.lastIndexOf('/', path.length() - 2);
        if (lastSlash < 0) return "/";
        return path.substring(0, lastSlash + 1);
    }
    
    public static String getFileName(String path) {
        path = normalizePath(path);
        if (path.equals("/")) return "";
        
        int lastSlash = path.lastIndexOf('/', path.length() - 2);
        return path.substring(lastSlash + 1).replace("/", "");
    }
}
