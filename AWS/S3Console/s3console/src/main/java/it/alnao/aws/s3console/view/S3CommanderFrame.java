package it.alnao.aws.s3console.view;

// S3CommanderFrame.java
import javax.swing.*;

import it.alnao.aws.s3console.controller.KeyboardShortcutManager;
import it.alnao.aws.s3console.controller.UIController;
import it.alnao.aws.s3console.services.S3Service;

import java.awt.*;

public class S3CommanderFrame extends JFrame {
    private final S3Service s3Service;
    private final UIController uiController;
    private final MenuManager menuManager;
    private final FileListPanel fileListPanel;
    private final NavigationPanel navigationPanel;

    public S3CommanderFrame() {
        setTitle("S3 Commander");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 600);
        setLocationRelativeTo(null);

        // Inizializzazione dei componenti principali
        s3Service = new S3Service();
        fileListPanel = new FileListPanel(s3Service);
        navigationPanel = new NavigationPanel();
        uiController = new UIController(s3Service, fileListPanel, navigationPanel);
        //menuManager = new MenuManager(this, uiController); /
        menuManager = new MenuManager(this, uiController,s3Service); //MenuManager(JFrame frame, UIController controller, S3Service s3Service) {

        // Setup del layout
        setLayout(new BorderLayout());
        setJMenuBar(menuManager.getMenuBar());
        add(navigationPanel, BorderLayout.NORTH);
        add(fileListPanel, BorderLayout.CENTER);

        // Registrazione degli observer
//TODO observer
//        s3Service.addObserver(fileListPanel);
//        s3Service.addObserver(navigationPanel);
        
        // Setup delle scorciatoie da tastiera
        setupKeyboardShortcuts();
    }

    private void setupKeyboardShortcuts() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
            .addKeyEventDispatcher(new KeyboardShortcutManager(uiController));
    }
}
