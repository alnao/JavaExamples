package it.alnao.aws.s3console.view;
import software.amazon.awssdk.services.s3.model.S3Object;
import javax.swing.*;
import javax.swing.table.*;

import it.alnao.aws.s3console.controller.FilterManager;
import it.alnao.aws.s3console.services.S3Service;
import it.alnao.aws.s3console.utils.S3Event;
import it.alnao.aws.s3console.utils.S3EventListener;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.event.ListSelectionListener;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class FileListPanel extends JPanel implements S3EventListener {
    private final JTable fileTable;
    private final DefaultTableModel tableModel;
    private final S3Service s3Service;

    public FileListPanel(S3Service s3Service) {
        this.s3Service = s3Service;
        setLayout(new BorderLayout());

        // Setup della tabella
        String[] columns = {"Name", "Size", "Last Modified"};
        tableModel = new DefaultTableModel(columns, 0);
        fileTable = new JTable(tableModel);
        
        // Configurazione della tabella
        setupTable();
        
        JScrollPane scrollPane = new JScrollPane(fileTable);
        add(scrollPane, BorderLayout.CENTER);

        // Registrazione come listener per gli eventi S3
        s3Service.addListener(this);
    }

    @Override
    public void onS3Event(S3Event event) {
        System.out.println("FileListPanel - Ricevuto evento: " + event.getType());
        switch (event.getType()) {
            case FILES_REFRESHED -> {
                @SuppressWarnings("unchecked")
                List<S3Object> files = (List<S3Object>) event.getData();
                updateFileList(files);
            }
            case BUCKET_CHANGED -> {
                System.out.println("FileListPanel - Bucket cambiato, pulisco la tabella");
                SwingUtilities.invokeLater(() -> {
                    tableModel.setRowCount(0);
                });
            }
            default -> System.out.println("FileListPanel - Evento non gestito: " + event.getType());
        }
    }

    public void updateFileList(List<S3Object> files) {
        System.out.println("FileListPanel - Aggiornamento lista file: " + files.size() + " files");
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            for (S3Object file : files) {
                String name = getDisplayName(file.key());
                String size = formatSize(file.size());
                String lastModified = formatDate(file.lastModified());
                tableModel.addRow(new Object[]{name, size, lastModified});
            }
        });
    }

    private String getDisplayName(String key) {
        String name = key;
        if (name.endsWith("/")) {
            name = name.substring(0, name.length() - 1);
        }
        int lastSlash = name.lastIndexOf('/');
        return lastSlash >= 0 ? name.substring(lastSlash + 1) : name;
    }

    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp-1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    private String formatDate(Instant lastModified) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault())
            .format(lastModified);
    }

    private void setupTable() {
        fileTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fileTable.getColumnModel().getColumn(0).setPreferredWidth(300);
        fileTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        fileTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        
        // Aggiungi doppio click per navigare nelle cartelle
        fileTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = fileTable.getSelectedRow();
                    if (row >= 0) {
                        String fileName = (String) tableModel.getValueAt(row, 0);
                        System.out.println("FileListPanel - Doppio click su: " + fileName);
                        // Implementa la navigazione qui se necessario
                    }
                }
            }
        });
    }

	public S3Object getSelectedFile() {
		// TODO Auto-generated method stub
		return null;
	}
}
/*
public class FileListPanel extends JPanel {
    private final JTable fileTable;
    private final FileTableModel tableModel;
    private final FilterManager filterManager;
    private final S3Service s3Service;
    private final DateTimeFormatter dateFormatter;
    private List<S3Object> currentFiles;
    private String currentPath = "/";
    
    public FileListPanel(S3Service s3Service) {
        this.s3Service = s3Service;
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                            .withZone(java.time.ZoneId.systemDefault());
        
        setLayout(new BorderLayout());
        
        // Inizializzazione del modello della tabella
        tableModel = new FileTableModel();
        fileTable = new JTable(tableModel);
        filterManager = new FilterManager(this);
        
        setupTable();
        setupPopupMenu();
        
        JScrollPane scrollPane = new JScrollPane(fileTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Inizializza la lista dei file
        currentFiles = new ArrayList<>();
    }
    
    private void setupTable() {
        // Configurazione delle colonne
        TableColumnModel columnModel = fileTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(400); // Nome
        columnModel.getColumn(1).setPreferredWidth(100); // Dimensione
        columnModel.getColumn(2).setPreferredWidth(200); // Data modifica
        
        // Renderer personalizzato per le celle
        fileTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (column == 0) {
                    // Gestione icone per file e cartelle
                    S3Object file = currentFiles.get(row);
                    if (file.key().endsWith("/")) {
                        setIcon(UIManager.getIcon("FileView.directoryIcon"));
                    } else {
                        setIcon(UIManager.getIcon("FileView.fileIcon"));
                    }
                } else {
                    setIcon(null);
                }
                
                return c;
            }
        });
        
        // Gestione doppio click
        fileTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    handleDoubleClick();
                }
            }
        });
        
        // Configurazione selezione
        fileTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fileTable.setRowHeight(20);
        fileTable.setShowGrid(false);
        fileTable.setIntercellSpacing(new Dimension(0, 0));
    }
    
    private void setupPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        
        // Menu items
        JMenuItem downloadItem = new JMenuItem("Download");
        downloadItem.addActionListener(e -> downloadSelectedFile());
        
        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(e -> deleteSelectedFile());
  
//TODO Rename
//        JMenuItem renameItem = new JMenuItem("Rename");
//        renameItem.addActionListener(e -> renameSelectedFile());
        
        // Aggiunta items al menu
        popupMenu.add(downloadItem);
        popupMenu.add(deleteItem);
        popupMenu.addSeparator();
//        popupMenu.add(renameItem);
        
        // Listener per mostrare il menu contestuale
        fileTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }
            
            private void showPopup(MouseEvent e) {
                int row = fileTable.rowAtPoint(e.getPoint());
                if (row >= 0 && row < fileTable.getRowCount()) {
                    fileTable.setRowSelectionInterval(row, row);
                    popupMenu.show(fileTable, e.getX(), e.getY());
                }
            }
        });
    }
    
    public void updateFileList(List<S3Object> files) {
        this.currentFiles = new ArrayList<>(files);
        SwingUtilities.invokeLater(() -> {
            tableModel.setFiles(files);
            filterManager.setOriginalFiles(files);
        });
    }
    
    private void handleDoubleClick() {
        int selectedRow = fileTable.getSelectedRow();
        if (selectedRow >= 0) {
            S3Object selectedFile = currentFiles.get(selectedRow);
            if (selectedFile.key().endsWith("/")) {
                // È una directory, naviga
                navigateToDirectory(selectedFile.key());
            } else {
                // È un file, scarica
                downloadSelectedFile();
            }
        }
    }
    
    private void navigateToDirectory(String path) {
        currentPath = path;
        s3Service.listObjects(path);
    }
    
    public void addSelectionListener(ListSelectionListener listener) {
        fileTable.getSelectionModel().addListSelectionListener(listener);
    }
    
    public S3Object getSelectedFile() {
        int selectedRow = fileTable.getSelectedRow();
        return selectedRow >= 0 ? currentFiles.get(selectedRow) : null;
    }
    
    private void downloadSelectedFile() {
        S3Object selected = getSelectedFile();
        if (selected != null && !selected.key().endsWith("/")) {
            s3Service.downloadObject(selected,null);
//TODO download
        }
    }
    
    private void deleteSelectedFile() {
        S3Object selected = getSelectedFile();
        if (selected != null) {
            if (JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete " + selected.key() + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                s3Service.deleteObject(selected);
            }
        }
    }
    
/*TODO
    private void renameSelectedFile() {
        S3Object selected = getSelectedFile();
        if (selected != null) {
            String newName = JOptionPane.showInputDialog(this,
                    "Enter new name:", "Rename",
                    JOptionPane.PLAIN_MESSAGE);
            if (newName != null && !newName.isEmpty()) {
                s3Service.renameObject(selected, newName);
            }
        }
    }

    // Modello della tabella personalizzato
    private class FileTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Name", "Size", "Last Modified"};
        private List<S3Object> files = new ArrayList<>();
        
        public void setFiles(List<S3Object> files) {
            this.files = new ArrayList<>(files);
            fireTableDataChanged();
        }
        
        @Override
        public int getRowCount() {
            return files.size();
        }
        
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
        
        @Override
        public Object getValueAt(int row, int column) {
            S3Object file = files.get(row);
            return switch (column) {
                case 0 -> getDisplayName(file.key());
                case 1 -> formatSize(file.size());
                case 2 -> dateFormatter.format(file.lastModified());
                default -> "";
            };
        }
        
        private String getDisplayName(String key) {
            int lastSlash = key.lastIndexOf('/');
            return lastSlash >= 0 ? key.substring(lastSlash + 1) : key;
        }
        
        private String formatSize(long bytes) {
            if (bytes == 0) return "";
            final String[] units = {"B", "KB", "MB", "GB", "TB"};
            int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
            return String.format("%.1f %s",
                    bytes / Math.pow(1024, digitGroups),
                    units[digitGroups]);
        }
    }
    
    public void setPath(String path) {
        this.currentPath = path;
    }
    
    public String getCurrentPath() {
        return currentPath;
    }
    
    public FilterManager getFilterManager() {
        return filterManager;
    }
}
*/