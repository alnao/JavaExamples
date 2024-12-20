package it.alnao.aws.s3console.view;

// NavigationPanel.java
import javax.swing.*;

import it.alnao.aws.s3console.services.S3Service;
import it.alnao.aws.s3console.utils.S3Event;
import it.alnao.aws.s3console.utils.S3EventType;

import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class NavigationPanel extends JPanel implements Observer {
    private final JTextField pathField;
    private final JTextField filterField;

    public NavigationPanel() {
        setLayout(new BorderLayout());
        
        // Panel per il path
        JPanel pathPanel = new JPanel(new BorderLayout());
        pathPanel.add(new JLabel("Path: "), BorderLayout.WEST);
        pathField = new JTextField();
        pathField.setEditable(false);
        pathPanel.add(pathField, BorderLayout.CENTER);

        // Panel per il filtro
        JPanel filterPanel = new JPanel(new BorderLayout());
        filterPanel.add(new JLabel("Filter: "), BorderLayout.WEST);
        filterField = new JTextField(30);
        filterField.addActionListener(e -> filterFiles());
        filterPanel.add(filterField, BorderLayout.CENTER);

        // Layout complessivo
        setLayout(new BorderLayout(5, 0));
        add(pathPanel, BorderLayout.CENTER);
        add(filterPanel, BorderLayout.EAST);
    }

    private void filterFiles() {
        String filterText = filterField.getText().toLowerCase();
        // Implementazione del filtro
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof S3Event) {
            S3Event event = (S3Event) arg;
            if (event.getType() == S3EventType.FILES_REFRESHED) {
//TODO                pathField.setText(((S3Service) o).getCurrentPath());
                pathField.setText("ERROR");
            }
        }
    }
}