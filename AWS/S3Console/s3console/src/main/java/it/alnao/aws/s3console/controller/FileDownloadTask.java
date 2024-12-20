package it.alnao.aws.s3console.controller;

import java.io.File;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import it.alnao.aws.s3console.services.S3Service;

// FileDownloadTask.java
public class FileDownloadTask extends BackgroundTask<Void> {
    private final S3Service s3Service;
    private final String key;
    private final File destination;
    private final JProgressBar progressBar;
    
    public FileDownloadTask(UIController uiController, S3Service s3Service,
                           String key, File destination, JProgressBar progressBar) {
        super(uiController);
        this.s3Service = s3Service;
        this.key = key;
        this.destination = destination;
        this.progressBar = progressBar;
    }
    
    @Override
    protected Void performTask() throws Exception {
        s3Service.downloadFile(key, destination, progress -> {
            SwingUtilities.invokeLater(() -> {
                progressBar.setValue((int)(progress * 100));
            });
        });
        return null;
    }
    
    @Override
    protected void onSuccess(Void result) {
        progressBar.setValue(100);
    }
}