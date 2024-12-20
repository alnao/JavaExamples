package it.alnao.aws.s3console.utils;

// ConfigurationManager.java
import java.util.prefs.Preferences;

import javax.swing.JFrame;

import it.alnao.aws.s3console.view.S3CommanderFrame;

public class ConfigurationManager {
    private static final String LAST_PROFILE = "lastProfile";
    private static final String LAST_BUCKET = "lastBucket";
    private static final String WINDOW_WIDTH = "windowWidth";
    private static final String WINDOW_HEIGHT = "windowHeight";
    private static final String WINDOW_X = "windowX";
    private static final String WINDOW_Y = "windowY";
    
    private final Preferences prefs;
    
    public ConfigurationManager() {
        prefs = Preferences.userNodeForPackage(S3CommanderFrame.class);
    }
    
    public void saveLastProfile(String profile) {
        prefs.put(LAST_PROFILE, profile);
    }
    
    public String getLastProfile() {
        return prefs.get(LAST_PROFILE, "");
    }
    
    public void saveLastBucket(String bucket) {
        prefs.put(LAST_BUCKET, bucket);
    }
    
    public String getLastBucket() {
        return prefs.get(LAST_BUCKET, "");
    }
    
    public void saveWindowState(JFrame frame) {
        prefs.putInt(WINDOW_WIDTH, frame.getWidth());
        prefs.putInt(WINDOW_HEIGHT, frame.getHeight());
        prefs.putInt(WINDOW_X, frame.getX());
        prefs.putInt(WINDOW_Y, frame.getY());
    }
    
    public void restoreWindowState(JFrame frame) {
        int width = prefs.getInt(WINDOW_WIDTH, 1200);
        int height = prefs.getInt(WINDOW_HEIGHT, 600);
        int x = prefs.getInt(WINDOW_X, -1);
        int y = prefs.getInt(WINDOW_Y, -1);
        
        frame.setSize(width, height);
        if (x >= 0 && y >= 0) {
            frame.setLocation(x, y);
        } else {
            frame.setLocationRelativeTo(null);
        }
    }
}