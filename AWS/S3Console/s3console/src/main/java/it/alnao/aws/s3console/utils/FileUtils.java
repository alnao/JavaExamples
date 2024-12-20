package it.alnao.aws.s3console.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

// FileUtils.java
public class FileUtils {
    public static String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp-1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
    
    public static String formatLastModified(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }
    
    public static boolean isValidFileName(String fileName) {
        return fileName != null && !fileName.isEmpty() && 
               !fileName.contains("/") && !fileName.contains("\\");
    }
}
