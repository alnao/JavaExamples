package it.alnao.javafx.controlroom.service;

import javafx.application.Platform;
import it.alnao.javafx.controlroom.model.ScriptEntry;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.function.Consumer;

/**
 * Runs a shell script in a background thread, streaming output
 * line-by-line to a callback on the JavaFX Application Thread.
 * 
 * Only one script can run per ScriptRunner instance at a time.
 */
public class ScriptRunner {

    private Process currentProcess;
    private Thread runnerThread;
    private volatile boolean running = false;

    /**
     * Runs the given shell script asynchronously.
     *
     * @param scriptPath   absolute path to the .sh file
     * @param outputCallback receives each line of stdout/stderr on the FX thread
     * @param onFinish      called on the FX thread when the process finishes or is killed
     */
    public synchronized void run(ScriptEntry script, Consumer<String> outputCallback, Runnable onFinish) {
        String scriptPath = script.scriptPath();
        String scriptParams = script.params();

        if (running) {
            Platform.runLater(() -> outputCallback.accept("⚠ A script is already running in this tab. Stop it first.\n"));
            return;
        }

        running = true;
        runnerThread = new Thread(() -> {
            try {
                Platform.runLater(() -> outputCallback.accept("▶ Starting: " + scriptPath + "\n"));
                Platform.runLater(() -> outputCallback.accept("▶ Params: " + scriptParams + "\n"));
                Platform.runLater(() -> outputCallback.accept("─".repeat(60) + "\n"));

                ProcessBuilder pb = new ProcessBuilder("bash", scriptPath, scriptParams.isEmpty() ? " " : scriptParams);
                pb.redirectErrorStream(true);
                currentProcess = pb.start();
                
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(currentProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        final String l = line;
                        Platform.runLater(() -> outputCallback.accept(l + "\n"));
                    }
                }

                int exitCode = currentProcess.waitFor();
                Platform.runLater(() -> {
                    outputCallback.accept("\n─".repeat(1) + "─".repeat(59) + "\n");
                    outputCallback.accept("■ Process exited with code: " + exitCode + "\n\n");
                });

            } catch (InterruptedException e) {
                Platform.runLater(() -> outputCallback.accept("\n⛔ Process interrupted.\n\n"));
            } catch (Exception e) {
                Platform.runLater(() -> outputCallback.accept("\n❌ Error: " + e.getMessage() + "\n\n"));
            } finally {
                running = false;
                currentProcess = null;
                Platform.runLater(onFinish);
            }
        }, "script-runner");
        runnerThread.setDaemon(true);
        runnerThread.start();
    }

    /**
     * Stops the currently running process, if any.
     */
    public synchronized void stop() {
        if (currentProcess != null && currentProcess.isAlive()) {
            currentProcess.descendants().forEach(ProcessHandle::destroyForcibly);
            currentProcess.destroyForcibly();
        }
        if (runnerThread != null) {
            runnerThread.interrupt();
        }
        running = false;
    }

    /**
     * Returns whether a script is currently running.
     */
    public boolean isRunning() {
        return running;
    }
}
