package it.alnao.aws.s3console.controller;

// BackgroundTask.java 
import javax.swing.*;

public abstract class BackgroundTask<T> {
    private final UIController uiController;
    
    protected BackgroundTask(UIController uiController) {
        this.uiController = uiController;
    }
    
    public void execute() {
        SwingWorker<T, Void> worker = new SwingWorker<>() {
            @Override
            protected T doInBackground() throws Exception {
                return performTask();
            }
            
            @Override
            protected void done() {
                try {
                    T result = get();
                    onSuccess(result);
                } catch (Exception e) {
                    onError(e);
                }
            }
        };
        worker.execute();
    }
    
    protected abstract T performTask() throws Exception;
    
    protected abstract void onSuccess(T result);
    
    protected void onError(Exception e) {
        uiController.showError("Operation failed", e);
    }
}