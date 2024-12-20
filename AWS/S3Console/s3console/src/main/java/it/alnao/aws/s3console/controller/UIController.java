package it.alnao.aws.s3console.controller;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import it.alnao.aws.s3console.services.S3Service;
import it.alnao.aws.s3console.utils.S3CommanderException;
import it.alnao.aws.s3console.view.FileListPanel;
import it.alnao.aws.s3console.view.NavigationPanel;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.S3Object;

// UIController.java
public class UIController {
    private final S3Service s3Service;
    private final FileListPanel fileListPanel;
    private final NavigationPanel navigationPanel;

    public UIController(S3Service s3Service, FileListPanel fileListPanel, NavigationPanel navigationPanel) {
        this.s3Service = s3Service;
        this.fileListPanel = fileListPanel;
        this.navigationPanel = navigationPanel;
    }

    public void handleProfileChange(String profile) {
        try {
            s3Service.changeProfile(profile,Region.EU_WEST_1);
        } catch (S3CommanderException e) {
            showError("Error changing profile", e);
        }
    }

    public void handleUpload() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                s3Service.uploadObject(fileChooser.getSelectedFile());
            } catch (S3CommanderException e) {
                showError("Error uploading file", e);
            }
        }
    }

    public void handleDownload() {
    	S3Object selectedFile = fileListPanel.getSelectedFile();
        if (selectedFile == null) {
            showWarning("Please select a file to download");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(selectedFile.key()));
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                s3Service.downloadObject(selectedFile, fileChooser.getSelectedFile());
            } catch (S3CommanderException e) {
                showError("Error downloading file", e);
            }
        }
    }
    public void handleBucketChange(String bucketName) {
        try {
            System.out.println("Cambio bucket a: " + bucketName);
            s3Service.changeBucket(bucketName);
        } catch (Exception e) {
            showError("Error changing bucket", e);
        }
    }

    public void showError(String message, Exception e) {
        JOptionPane.showMessageDialog(null, message + ": " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(null, message,
                "Warning", JOptionPane.WARNING_MESSAGE);
    }
}