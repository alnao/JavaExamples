package it.alnao.aws.s3console.view;

// MenuManager.java
import javax.swing.*;

import it.alnao.aws.s3console.controller.UIController;
import it.alnao.aws.s3console.services.S3Service;
import it.alnao.aws.s3console.utils.S3Event;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

public class MenuManager {
	private final JFrame frame;
    private final JMenuBar menuBar;
    private JMenu bucketsMenu;
    private final UIController controller;
    private final S3Service s3Service;
    

    public MenuManager(JFrame frame, UIController controller, S3Service s3Service) {
    	this.frame=frame;
        this.controller = controller;
        this.s3Service = s3Service;
        this.bucketsMenu=null;
        this.menuBar = createMenuBar();
        
        setupHelpMenu(frame);
        
        s3Service.addListener(this::handleS3Event);
    }



	public JMenuBar getMenuBar() {
        return menuBar;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Menu File
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

/*TODO up        
        addMenuItem(fileMenu, "Upload File", KeyEvent.VK_U, 
            KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.CTRL_DOWN_MASK),
            e -> s3Service.navigateUp());
         
*/
/* TODO REfresh
        JMenu navigationMenu=new JMenu("Navigation");
        addMenuItem(navigationMenu, "Refresh", KeyEvent.VK_F,
            KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK),
            e -> s3Service.refreshFiles());
*/
        // Menu AWS Profiles
        JMenu profileMenu = new JMenu("AWS Profiles");
        profileMenu.setMnemonic(KeyEvent.VK_P);
        updateProfileMenu(profileMenu);

        // Menu Buckets
        this.bucketsMenu = new JMenu("Buckets");
        this.bucketsMenu.setMnemonic(KeyEvent.VK_B);
        
        menuBar.add(fileMenu);
//        menuBar.add(navigationMenu);
        menuBar.add(profileMenu);
        menuBar.add(this.bucketsMenu);

       
        return menuBar;
    }

    private void updateProfileMenu(JMenu menu) {
        menu.removeAll();
        ButtonGroup group = new ButtonGroup();

        for (String profile : s3Service.getAvailableProfiles()) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(profile);
            item.addActionListener(e -> controller.handleProfileChange(profile));
            group.add(item);
            menu.add(item);
        }
    }
    
    private void handleS3Event(S3Event event) {
    	System.out.println("Evento ricevuto: " + event.getType() + ", dati: " + event.getData());

        switch (event.getType()) {
            case BUCKETS_LISTED -> {
                @SuppressWarnings("unchecked")
                List<String> buckets = (List<String>) event.getData();
System.out.println("handleS3Event " + event.toString());
				//showAboutDialog(this.frame,"handleS3Event " + event.toString());
                updateBucketsMenu(buckets);
            }
            case PROFILE_CHANGED -> {
                System.out.println("MenuManager - Profilo cambiato");
                // Pulisci il menu dei bucket quando cambia il profilo
                SwingUtilities.invokeLater(() -> {
                    bucketsMenu.removeAll();
                    bucketsMenu.revalidate();
                    bucketsMenu.repaint();
                });
            }
            case BUCKET_CHANGED -> {
                System.out.println("MenuManager - Bucket cambiato a: " + s3Service.getCurrentBucket());
                // Aggiorna la selezione nel menu
                updateBucketSelection(s3Service.getCurrentBucket());
            }
            case FILES_REFRESHED -> {
                System.out.println("MenuManager - Files refreshed nel bucket: " + s3Service.getCurrentBucket());
                // Aggiorna la selezione del bucket corrente nel menu
                updateBucketSelection(s3Service.getCurrentBucket());
            }
            default -> System.out.println("MenuManager - Evento non gestito: " + event.getType());
        }
    }

    private void updateBucketSelection(String currentBucket) {
        for (int i = 0; i < bucketsMenu.getItemCount(); i++) {
            JMenuItem item = bucketsMenu.getItem(i);
            if (item instanceof JRadioButtonMenuItem) {
                JRadioButtonMenuItem radioItem = (JRadioButtonMenuItem) item;
                if (radioItem.getText().equals(currentBucket)) {
                    radioItem.setSelected(true);
                    break;
                }
            }
        }
    }
    
    private void updateBucketsMenu(List<String> buckets) {
System.out.println("updateBucketsMenu " + buckets.toString());
		//showAboutDialog(this.frame,"updateBucketsMenu");
        SwingUtilities.invokeLater(() -> {
            bucketsMenu.removeAll();
            ButtonGroup group = new ButtonGroup();

            for (String bucket : buckets) {
                JRadioButtonMenuItem item = new JRadioButtonMenuItem(bucket);
                item.addActionListener(e -> s3Service.changeBucket(bucket));
                
                item.addActionListener(e -> controller.handleBucketChange(bucket));
                
                // Se è il bucket corrente, selezionalo
                if (bucket.equals(s3Service.getCurrentBucket())) {
                    item.setSelected(true);
                }
                
                group.add(item);
                bucketsMenu.add(item);
            }
            
            // Se non ci sono bucket, mostra un elemento disabilitato
            if (buckets.isEmpty()) {
                JMenuItem noBuckets = new JMenuItem("No buckets available");
                noBuckets.setEnabled(false);
                bucketsMenu.add(noBuckets);
            }
        });
    }

    private void addMenuItem(JMenu menu, String text, int mnemonic, KeyStroke accelerator,ActionListener listener) {
        JMenuItem menuItem = new JMenuItem(text, mnemonic);
        menuItem.setAccelerator(accelerator);
        menuItem.addActionListener(listener);
        menu.add(menuItem);
    }
    
    
    private void setupHelpMenu(JFrame parentFrame) {
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        
        JMenuItem aboutItem = new JMenuItem("About S3 Commander");
        aboutItem.setMnemonic(KeyEvent.VK_A);
        aboutItem.addActionListener(e -> showAboutDialog(parentFrame,""));
        
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);
    }
    private void showAboutDialog(JFrame parentFrame, String message) {
        // Creiamo un pannello personalizzato per il messaggio
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Aggiungiamo un'icona (puoi personalizzarla)
        JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.informationIcon"));
        panel.add(iconLabel, BorderLayout.WEST);

        // Pannello per il testo
        JPanel textPanel = new JPanel(new BorderLayout(5, 10));
        
        // Titolo in grassetto
        JLabel titleLabel = new JLabel("S3 Commander");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16));
        textPanel.add(titleLabel, BorderLayout.NORTH);

        // Messaggio multilinea
        JLabel messageLabel = new JLabel("<html>" + message +
            "<br>Version 1.0.0<br><br>" +
            "A powerful AWS S3 file manager<br>" +
            "© 2024 AlNao. All rights reserved.<br><br>" +
            "Built with AWS SDK v2<br>" +
            "Region: EU-West-1" +
            "</html>");
        textPanel.add(messageLabel, BorderLayout.CENTER);

        panel.add(textPanel, BorderLayout.CENTER);

        // Mostra il dialog
        JOptionPane.showMessageDialog(
            parentFrame,
            panel,
            "About S3 Commander",
            JOptionPane.PLAIN_MESSAGE
        );
    }
}