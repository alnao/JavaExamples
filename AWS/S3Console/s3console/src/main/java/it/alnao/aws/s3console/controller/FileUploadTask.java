package it.alnao.aws.s3console.controller;

import java.io.File;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import it.alnao.aws.s3console.services.S3Service;

// FileUploadTask.java
public class FileUploadTask extends BackgroundTask<Void> {
    private final S3Service s3Service;
    private final File file;
    private final JProgressBar progressBar;
    
    public FileUploadTask(UIController uiController, S3Service s3Service, 
                         File file, JProgressBar progressBar) {
        super(uiController);
        this.s3Service = s3Service;
        this.file = file;
        this.progressBar = progressBar;
    }
    
    @Override
    protected Void performTask() throws Exception {
        s3Service.uploadObject(file, progress -> {
            SwingUtilities.invokeLater(() -> {
                progressBar.setValue((int)(progress * 100));
            });
        });
        return null;
    }
    
    @Override
    protected void onSuccess(Void result) {
        progressBar.setValue(100);
//TODO sistemare
        //s3Service.refreshFiles();
    }
}
