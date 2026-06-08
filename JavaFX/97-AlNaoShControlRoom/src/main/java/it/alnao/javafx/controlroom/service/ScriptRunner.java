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

                java.util.List<String> command = new java.util.ArrayList<>();
                command.add("bash");
                command.add(scriptPath);
                if (!scriptParams.isEmpty()) {
                    command.addAll(parseParams(scriptParams));
                }

                ProcessBuilder pb = new ProcessBuilder(command);
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
     * Sends input to the running process stdin.
     */
    public synchronized void sendInput(String input) {
        if (running && currentProcess != null && currentProcess.isAlive()) {
            try {
                java.io.OutputStream os = currentProcess.getOutputStream();
                os.write((input + "\n").getBytes());
                os.flush();
            } catch (Exception e) {
                System.err.println("[ScriptRunner] Failed to send input: " + e.getMessage());
            }
        }
    }

    /**
     * Parses the parameter string into a list of individual arguments,
     * respecting single and double quotes.
     */
    private java.util.List<String> parseParams(String params) {
        java.util.List<String> list = new java.util.ArrayList<>();
        if (params == null || params.isBlank()) {
            return list;
        }
        StringBuilder current = new StringBuilder();
        boolean inDoubleQuotes = false;
        boolean inSingleQuotes = false;
        for (int i = 0; i < params.length(); i++) {
            char c = params.charAt(i);
            if (c == '\"' && !inSingleQuotes) {
                inDoubleQuotes = !inDoubleQuotes;
            } else if (c == '\'' && !inDoubleQuotes) {
                inSingleQuotes = !inSingleQuotes;
            } else if (Character.isWhitespace(c) && !inDoubleQuotes && !inSingleQuotes) {
                if (current.length() > 0) {
                    list.add(current.toString());
                    current.setLength(0);
                }
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) {
            list.add(current.toString());
        }
        return list;
    }

    /**
     * Returns whether a script is currently running.
     */
    public boolean isRunning() {
        return running;
    }
}
