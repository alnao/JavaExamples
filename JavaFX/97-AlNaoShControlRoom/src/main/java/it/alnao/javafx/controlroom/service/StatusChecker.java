package it.alnao.javafx.controlroom.service;

import it.alnao.javafx.controlroom.model.MonitorEntry;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Periodically checks the status of monitored URLs by performing
 * HTTP HEAD/GET requests. Results are published as a map of
 * MonitorEntry -> Boolean (true = reachable, false = unreachable).
 */
public class StatusChecker {

    /**
     * Callback interface for delivering status results back to the UI thread.
     */
    @FunctionalInterface
    public interface StatusCallback {
        void onStatusUpdate(Map<MonitorEntry, Boolean> results);
    }

    private final List<MonitorEntry> monitors;
    private final int refreshSeconds;
    private final StatusCallback callback;
    private final ExecutorService executor;
    private ScheduledExecutorService scheduler;

    public StatusChecker(List<MonitorEntry> monitors, int refreshSeconds, StatusCallback callback) {
        this.monitors = monitors;
        this.refreshSeconds = refreshSeconds;
        this.callback = callback;
        this.executor = Executors.newFixedThreadPool(
                Math.max(1, Math.min(monitors.size(), 8)),
                r -> {
                    Thread t = new Thread(r, "status-checker");
                    t.setDaemon(true);
                    return t;
                }
        );
    }

    /**
     * Starts the periodic status checking.
     */
    public void start() {
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "status-scheduler");
            t.setDaemon(true);
            return t;
        });
        // Run immediately, then at fixed intervals
        scheduler.scheduleAtFixedRate(this::checkAll, 0, refreshSeconds, TimeUnit.SECONDS);
    }

    /**
     * Stops the periodic status checking.
     */
    public void stop() {
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
        executor.shutdownNow();
    }

    /**
     * Performs a one-shot check of all monitors and publishes results.
     */
    public void checkAll() {
        Map<MonitorEntry, Future<Boolean>> futures = new LinkedHashMap<>();
        for (MonitorEntry entry : monitors) {
            futures.put(entry, executor.submit(() -> isReachable(entry.url())));
        }

        Map<MonitorEntry, Boolean> results = new LinkedHashMap<>();
        for (var e : futures.entrySet()) {
            try {
                results.put(e.getKey(), e.getValue().get(5, TimeUnit.SECONDS));
            } catch (Exception ex) {
                results.put(e.getKey(), false);
            }
        }

        callback.onStatusUpdate(results);
    }

    /**
     * Checks if a URL is reachable by making an HTTP connection.
     * Tries HEAD first, falls back to GET on method-not-allowed.
     */
    private boolean isReachable(String urlStr) {
        if (tryRequest(urlStr, "HEAD")) {
            return true;
        }
        if (tryRequest(urlStr, "GET")) {
            return true;
        }
        
        // Fallback for localhost: try explicit IPv4 and IPv6
        // This is needed because Vite often binds to ::1 but Java might try 127.0.0.1 first and fail
        if (urlStr.contains("localhost")) {
            String ipv4 = urlStr.replace("localhost", "127.0.0.1");
            if (tryRequest(ipv4, "HEAD") || tryRequest(ipv4, "GET")) return true;
            
            String ipv6 = urlStr.replace("localhost", "[::1]");
            if (tryRequest(ipv6, "HEAD") || tryRequest(ipv6, "GET")) return true;
        }
        
        return false;
    }

    private boolean tryRequest(String urlStr, String method) {
        try {
            // Ensure URL has a scheme
            if (!urlStr.startsWith("http://") && !urlStr.startsWith("https://")) {
                urlStr = "http://" + urlStr;
            }
            URL url = URI.create(urlStr).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            conn.setRequestMethod(method);
            conn.setInstanceFollowRedirects(true);
            // Some servers require a User-Agent
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (AlNaoControlRoom)");
            
            int code = conn.getResponseCode();
            //System.out.println("urlStr " + urlStr + " code " + code);   
            conn.disconnect();
            return code >= 200 && code < 500;
        } catch (Exception e) {
            //System.out.println("urlStr " + urlStr + " code " + e.getMessage());
            return false;   
        }
    }
}
