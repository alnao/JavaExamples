package it.alnao.aws.s3console.view;

import java.awt.BorderLayout;

import javax.swing.*;

// StatusBar.java
public class StatusBar extends JPanel {
    private final JLabel statusLabel;
    private final JProgressBar progressBar;
    
    public StatusBar() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEtchedBorder());
        
        statusLabel = new JLabel(" ");
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        
        add(statusLabel, BorderLayout.CENTER);
        add(progressBar, BorderLayout.EAST);
    }
    
    public void setStatus(String status) {
        statusLabel.setText(status);
    }
    
    public void showProgress(String operation) {
        progressBar.setValue(0);
        progressBar.setString(operation);
        progressBar.setVisible(true);
    }
    
    public void updateProgress(int value) {
        progressBar.setValue(value);
    }
    
    public void hideProgress() {
        progressBar.setVisible(false);
    }
}