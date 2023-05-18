package it.alnao.awssdkexamples;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.Dimension;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;

/** see https://stackoverflow.com/questions/10093558/hand-coded-gui-java
 * 
 * card https://docs.oracle.com/javase/tutorial/uiswing/layout/card.html
 * */
public class BucketsFrame  {
    
    private static final Logger logger = LoggerFactory.getLogger(BucketsFrame.class);
    public static void main(String[] args) {
    	logger.info("Application 02_BucketManager start");
    	final String profile="default";
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	Regions region = Regions.EU_WEST_1;
                final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                		//.withCredentials(null)
                		.withRegion(region)
                		.withCredentials( new ProfileCredentialsProvider (profile) )
                		.build();
                new BucketsFrame(s3,profile,true);
            }
        });
    }   
    
    private static final int GAP = 5;
    private AmazonS3 s3 = null;
    private JFrame frame ;
    private JPanel centerPanel;
    private JPanel jbuckets;
    private JTable jbucketstable;
    private JTable jfilestable1;
    private JTable jfilestable2;
    private JTable jfilestable3;
    private JPanel jfiles1;
    private JPanel jfiles2;
    private JPanel jfiles3;
    private JPanel contentPane;
    private DefaultTableModel jbucketstableModel;
    private JLabel statusLabel;
    private JPanel statusPanel;

    public BucketsFrame() {}
    
    public BucketsFrame(AmazonS3 s3, String profile , boolean createFrame ) {
    	if (createFrame)
    		createBucketPanel(s3,profile,createFrame);
    }
    
    public JPanel createBucketPanel(AmazonS3 s3, String profile, boolean createFrame ) {
    	//TODO gestione multiprofilo
    	this.s3=s3;
    	List<Bucket> buckets=loadBucketList(s3);
        
        centerPanel = new JPanel();
        //centerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        centerPanel.setLayout(new GridLayout(1, 0, 3, 3)); /* aumentare 2 se aggiunti sotto-pannelli */
        
        this.jbuckets = createPanelBucket(buckets);
        jbuckets.setPreferredSize(new Dimension(300,600));
        centerPanel.add( this.jbuckets);
        this.jfiles1 = createJpanel();
        jfiles1.setPreferredSize(new Dimension(310,600));
        centerPanel.add( this.jfiles1);
        this.jfiles2 = createJpanel();
        jfiles2.setPreferredSize(new Dimension(310,600));
        centerPanel.add( this.jfiles2);
        this.jfiles3 = createJpanel();
        jfiles3.setPreferredSize(new Dimension(310,600));
        centerPanel.add( this.jfiles3);
        
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(centerPanel, BorderLayout.CENTER);
        //contentPane.add(bottomPanel, BorderLayout.PAGE_END);
        
        if (createFrame) {
	        frame = new JFrame("Buckets Frame");
	        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	        frame.setLayout(new BorderLayout());
	        frame.setLocationByPlatform(true);
	        frame.setSize(700,800);
	        frame.setContentPane(contentPane);        
	        frame.pack();
	        frame.setVisible(true);
	        
	        statusPanel = new JPanel();
	        //statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
	        statusPanel.setPreferredSize(new Dimension(frame.getWidth(), 16));
	        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
	        statusLabel = new JLabel("Buckets Frame with profile: " +profile);
	        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
	        statusPanel.add(statusLabel);
	        frame.add(statusPanel, BorderLayout.SOUTH);
        }
	    return contentPane;
    }
    private JPanel createJpanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setOpaque(true);
        panel.setAlignmentX(Component.TOP_ALIGNMENT );
        //panel.setBackground(Color.DARK_GRAY);
        panel.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
        panel.setPreferredSize(new Dimension(270,600));
        //jfiles3.setPreferredSize(new Dimension(400,600));
        return panel;
    }
    
    private List<Bucket> loadBucketList(AmazonS3 s3) {
    	//setStatusLabel("Carico dei bucket");
    	return BucketUtils.getBucketList(s3);
    }
    private JPanel createPanelBucket(List<Bucket> buckets){
    	JPanel panel=createJpanel();
        GridBagConstraints gbc = new GridBagConstraints();
        String[] columnNames = {"Bucket"};
        Object[][] data= new Object[buckets.size()][1];
        for (int i=0;i<buckets.size();i++) { Bucket b=buckets.get(i);
        	 data[i][0]= b.getName() ;
        }
        
        jbucketstableModel = new DefaultTableModel(data, columnNames);
        this.jbucketstable = new JTable( jbucketstableModel ){
            public Class getColumnClass(int column){
                return getValueAt(0, column).getClass();
            }
            @Override
            public boolean isCellEditable(int row, int column) {
               return false;
            }
        };           
        //model.setFillsViewportHeight(true);
        this.jbucketstable.setPreferredScrollableViewportSize(new Dimension(250, 500));
        this.jbucketstable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(this.jbucketstable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.BLACK, 1)
                        , "Lista dei bucket"
                        , TitledBorder.CENTER
                        , TitledBorder.DEFAULT_POSITION));      
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(scrollPane, gbc);     

        JButton jbuttonloadFiles= new JButton("Carica files del bucket >");
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        jbuttonloadFiles.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            	loadFilesL1();
            }
        });
        panel.add(jbuttonloadFiles, gbc);
        return panel;
    }
    private void loadFilesL1() {
		centerPanel.remove(jfiles1);
		centerPanel.remove(jfiles2);
		centerPanel.remove(jfiles3);
		int indexSelected=jbucketstable.getSelectedRow();//getSelectionModel().getLeadSelectionIndex();
		//indexSelected = jbucketstable.convertRowIndexToModel( jbucketstable.getEditingRow() );
    	if (indexSelected >= 0) {
    		String bucketSel = (String) jbucketstable.getValueAt( indexSelected , 0);
			//logger.info(bucketSel);
			List<S3ObjectSummary> listFileL1=BucketUtils.getObjectList(s3, bucketSel, null);
			jfiles1 = createPanelFilesL1(listFileL1,bucketSel);
			logger.info("listFileL1 size=" + listFileL1.size() );
			jbucketstable.validate();
			jbucketstable.repaint();
			
			jfilestable1.validate();
			jfilestable1.repaint();
    	}else { // if (indexSelected<0) {
    		logger.info("jbucketstable index vuoto = -1 ");
    		jfiles1=new JPanel();
    		JTextField textField=new JTextField("Selezionare un bucket ");
    		jfiles1.add (textField);
    	}
		jfiles2=createJpanel();
		jfiles3=createJpanel();
		centerPanel.add( jfiles1);
		centerPanel.add(jfiles2);
		centerPanel.add(jfiles3);
    	jfiles1.revalidate();
    	jfiles1.repaint();
    	centerPanel.validate();
    	centerPanel.repaint(); //https://stackoverflow.com/questions/29612481/how-to-update-refresh-jpanel-on-jbutton-click
    }
    
    
    private JPanel createPanelFilesL1(List<S3ObjectSummary> list, String bucketSel){
        JPanel panel = createJpanel();
        //panel.setLayout(new GridLayout(2, 1, 3, 3));
        //panel.setLayout(new CardLayout());
        panel.setLayout(new BorderLayout());
        
        String[] columnNames = {"Object","Date"};//,"Size"
        Object[][] data= new Object[list.size()][2];
        for (int i=0;i<list.size();i++) { S3ObjectSummary o=list.get(i);
        	 data[i][0]= o.getKey();
        	 data[i][1]= o.getLastModified();
        	//data[i][2]= o.getSize();
        }
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        this.jfilestable1 = new JTable( model ){
            public Class getColumnClass(int column){
                return String.class;//return getValueAt(0, column).getClass();
            }
        };                      
        this.jfilestable1.setPreferredScrollableViewportSize(new Dimension(250, 300));
        this.jfilestable1.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(this.jfilestable1);
        scrollPane.setPreferredSize( new Dimension(250, 300) );
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.BLACK, 1)
                        , /*-"Files in" + */bucketSel
                        , TitledBorder.CENTER
                        , TitledBorder.DEFAULT_POSITION));   
        
        panel.add(scrollPane,BorderLayout.CENTER);     
        /*
        jbucketstable.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e) {
        		int row = m_table.getSelectedRow();
        		int col = m_table.getSelectedColumn();
        	}
        });*/
        
        JPanel fileButtonPanel = new JPanel();
        fileButtonPanel.setPreferredSize(new Dimension(270,30));
        fileButtonPanel.setLayout(new GridLayout(1, 2, 3, 3));
        //bottone scarica da bucket
        JButton loadFiles= new JButton("Scarica file");
        loadFiles.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
        		int indexSelected=jfilestable1.getSelectedRow();
        		if (indexSelected >= 0) {
            		String fileSel = (String) jfilestable1.getValueAt( indexSelected , 0);
        			S3ObjectSummary oggetto=list.get(indexSelected);
        			boolean s=downloadFiles(loadFiles,s3,oggetto);
        			logger.info(s ? "OK" : "KO");
        		}else {
        			logger.info("download index -1");
        		}
            }});	
        fileButtonPanel.add(loadFiles);
        //bottone upload nel bucket
        JButton uploadFiles= new JButton("Carica file");
        uploadFiles.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
				try {
					boolean s = uploadFiles(uploadFiles, s3, bucketSel, "");
					logger.info(s ? "OK" : "KO");
					if (s) {//ricarico
						loadFilesL1();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
            }});	
        fileButtonPanel.add(uploadFiles);
        //bottone apri da bucket a L1
        JButton loadFiles2= new JButton("Apri >");
        loadFiles2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            	loadFilesL2();
            }
        });
        fileButtonPanel.add(loadFiles2);
        panel.add(fileButtonPanel,BorderLayout.PAGE_END);
        return panel;
    }
    private void loadFilesL2() {

		centerPanel.remove(jfiles2);
		centerPanel.remove(jfiles3);
		int indexSelected=jbucketstable.getSelectedRow();//getSelectionModel().getLeadSelectionIndex();
		int indexSelectedFile=jfilestable1.getSelectedRow();//getSelectionModel().getLeadSelectionIndex();
		//indexSelected = jbucketstable.convertRowIndexToModel( jbucketstable.getEditingRow() );
    	if (indexSelected >= 0 && indexSelectedFile>=0) {
    		String bucketSel = (String) jbucketstable.getValueAt( indexSelected , 0);
    		String path= (String) jfilestable1.getValueAt(indexSelectedFile, 0);
    		if (!path.endsWith("/")) {
        		jfiles2=new JPanel();
        		JTextField textField=new JTextField("Selezionare una cartella");
        		jfiles2.add (textField);
    		}else {
        		List<S3ObjectSummary> listFileL2=BucketUtils.getObjectList(s3, bucketSel, path);
    			//logger.info("listFileL2 size=" + listFileL2.size() );
    			jfiles2 = createPanelFilesL2(listFileL2,bucketSel, path);
    			jbucketstable.validate();
    			jbucketstable.repaint();
    			jfilestable1.validate();
    			jfilestable1.repaint();
    			jfilestable2.validate();
    			jfilestable2.repaint();
    		}

    	}else { // if (indexSelected<0) {
    		logger.info("index vuoto = -1 " + indexSelected + indexSelectedFile);
    		jfiles2=new JPanel();
    		JTextField textField=new JTextField("Selezionare un bucket e una cartella");
    		jfiles2.add (textField);
    	}
		jfiles3=createJpanel();
		centerPanel.add(jfiles2);
		centerPanel.add(jfiles3);
    	jfiles2.revalidate();
    	jfiles2.repaint();
    	centerPanel.validate();
    	centerPanel.repaint(); //https://stackoverflow.com/questions/29612481/how-to-update-refresh-jpanel-on-jbutton-click
    
    }
    
    private JPanel createPanelFilesL2(List<S3ObjectSummary> list, String bucketSel, String path){
    	logger.info("createFilesPanelL2 " + bucketSel + "\\" + path );
        JPanel panel = createJpanel();
        panel.setLayout(new BorderLayout());
        
        String[] columnNames = {"Object","Date"};//,"Size"
        Object[][] data= new Object[list.size()][2];
        for (int i=0;i<list.size();i++) { S3ObjectSummary o=list.get(i);
        	 data[i][0]= o.getKey().replace(path,"");
        	 data[i][1]= o.getLastModified();//data[i][2]= o.getSize();
        }
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        this.jfilestable2 = new JTable( model ){
            public Class getColumnClass(int column){
                return String.class;//return getValueAt(0, column).getClass();
            }
        };                      
        this.jfilestable2.setPreferredScrollableViewportSize(new Dimension(250, 300));
        this.jfilestable2.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(this.jfilestable2);
        scrollPane.setPreferredSize( new Dimension(250, 300) );
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.BLACK, 1)
                        , /*-"Files in" + */path
                        , TitledBorder.CENTER
                        , TitledBorder.DEFAULT_POSITION));   
        
        panel.add(scrollPane,BorderLayout.CENTER);     
        JPanel fileButtonPanel = new JPanel();
        fileButtonPanel.setPreferredSize(new Dimension(270,30));
        fileButtonPanel.setLayout(new GridLayout(1, 2, 3, 3));
        
        JButton loadFiles= new JButton("Scarica file");
        loadFiles.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
        		int indexSelected=jfilestable2.getSelectedRow();
        		if (indexSelected >= 0) {
            		String fileSel = (String) jfilestable2.getValueAt( indexSelected , 0);
        			S3ObjectSummary oggetto=list.get(indexSelected);
        			boolean  s=downloadFiles(loadFiles,s3,oggetto); 
        			logger.info(s ? "OK" : "KO");
        		}else {
        			logger.info("download index -1");
        		}
            }});	
        fileButtonPanel.add(loadFiles);
        //bottone upload nel bucket
        JButton uploadFiles= new JButton("Carica file");
        uploadFiles.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
				try {
					boolean s = uploadFiles(uploadFiles, s3, bucketSel, path);
					logger.info(s ? "OK" : "KO");
					if (s) {//ricarico
						loadFilesL2();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
            }});	
        fileButtonPanel.add(uploadFiles);
        //bottone apri da L1 a L2
        JButton loadFiles2= new JButton("Apri >");
        loadFiles2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
            	loadFilesL3(path);
            }
        });
        fileButtonPanel.add(loadFiles2);
        panel.add(fileButtonPanel,BorderLayout.PAGE_END);
        return panel;
    }
    private void loadFilesL3(String path) {
    	logger.info("loadFilesL3=" + path);
		centerPanel.remove(jfiles3);
		int indexSelected=jbucketstable.getSelectedRow();//getSelectionModel().getLeadSelectionIndex();
		int indexSelectedFile=jfilestable2.getSelectedRow();//getSelectionModel().getLeadSelectionIndex();
		//indexSelected = jbucketstable.convertRowIndexToModel( jbucketstable.getEditingRow() );
    	if (indexSelected >= 0 && indexSelectedFile>=0) {
    		String bucketSel = (String) jbucketstable.getValueAt( indexSelected , 0);
    		String path2= (String) jfilestable2.getValueAt(indexSelectedFile, 0);
    		logger.info("loadFilesL3=" + path2);
    		if ( !path2.endsWith("/") ) {
    			jfiles3=new JPanel();
        		JTextField textField=new JTextField("Selezionare una cartella");
        		jfiles3.add (textField);
    		}else {
	    		List<S3ObjectSummary> listFileL3=BucketUtils.getObjectList(s3, bucketSel, path + "" + path2);
				jfiles3 = createPanelFilesL3(listFileL3,bucketSel, path , path2);
				jbucketstable.validate();
				jbucketstable.repaint();
				jfilestable1.validate();
				jfilestable1.repaint();
				jfilestable2.validate();
				jfilestable2.repaint();
				jfilestable3.validate();
				jfilestable3.repaint();
    		}
    	}else { // if (indexSelected<0) {
    		logger.info("index vuoto = -1 " + indexSelected + indexSelectedFile);
    		jfiles3=new JPanel();
    		JTextField textField=new JTextField("Selezionare un bucket e una cartella");
    		jfiles3.add (textField);
    	}
		centerPanel.add(jfiles3);
    	jfiles3.revalidate();
    	jfiles3.repaint();
    	centerPanel.validate();
    	centerPanel.repaint(); //https://stackoverflow.com/questions/29612481/how-to-update-refresh-jpanel-on-jbutton-click
    	
    }
    private JPanel createPanelFilesL3(List<S3ObjectSummary> list, String bucketSel, String pathL1, String pathL2){
    	logger.info("createFilesPanelL3 " + bucketSel + " -- " + pathL1 + " -- " + pathL2);
        JPanel panel = createJpanel();
        panel.setLayout(new BorderLayout());
        
        String[] columnNames = {"Object","Date"};//,"Size"
        Object[][] data= new Object[list.size()][2];
        for (int i=0;i<list.size();i++) { S3ObjectSummary o=list.get(i);
        	 data[i][0]= o.getKey().replace(pathL1+pathL2,"");
        	 data[i][1]= o.getLastModified();//data[i][2]= o.getSize();
        }
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        this.jfilestable3 = new JTable( model ){
            public Class getColumnClass(int column){
                return String.class;//return getValueAt(0, column).getClass();
            }
        };                      
        this.jfilestable3.setPreferredScrollableViewportSize(new Dimension(250, 300));
        this.jfilestable3.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(this.jfilestable3);
        scrollPane.setPreferredSize( new Dimension(250, 300) );
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.BLACK, 1)
                        , /*-"Files in" + */pathL1+pathL2
                        , TitledBorder.CENTER
                        , TitledBorder.DEFAULT_POSITION));   
        
        panel.add(scrollPane,BorderLayout.CENTER);     
        JPanel fileButtonPanel = new JPanel();
        fileButtonPanel.setPreferredSize(new Dimension(270,30));
        fileButtonPanel.setLayout(new GridLayout(1, 2, 3, 3));
        
        JButton loadFiles= new JButton("Scarica file");
        loadFiles.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
        		int indexSelected=jfilestable3.getSelectedRow();
        		if (indexSelected >= 0) {
            		String fileSel = (String) jfilestable3.getValueAt( indexSelected , 0);
        			S3ObjectSummary oggetto=list.get(indexSelected);
        			boolean s=downloadFiles(loadFiles,s3,oggetto); 
        			logger.info(s ? "OK" : "KO");
        		}else {
        			logger.info("download index -1");
        		}
            }});	
        fileButtonPanel.add(loadFiles);
        //bottone upload nel bucket
        JButton uploadFiles= new JButton("Carica file");
        uploadFiles.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
				try {
					boolean s = uploadFiles(uploadFiles, s3, bucketSel, pathL1 + pathL2);
					logger.info(s ? "OK" : "KO");
					if (s) {//ricarico
						loadFilesL3(pathL1);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
            }});	
        fileButtonPanel.add(uploadFiles);
        panel.add(fileButtonPanel,BorderLayout.PAGE_END);
        return panel;
    }
        
    public boolean downloadFiles(Component parent,AmazonS3 s3client, S3ObjectSummary oggetto) {
    	if (oggetto.getKey().endsWith("/")) {
    		statusLabel = new JLabel("Impossibile caricare il file " + oggetto.getKey());
    		return false;
    	}
    	JFileChooser fc = new JFileChooser();
    	fc.setCurrentDirectory(new java.io.File(".")); // start at application current directory
    	fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    	int returnVal = fc.showSaveDialog(parent);
		logger.info(oggetto.getBucketName() + "-" + oggetto.getKey());
    	if(returnVal == JFileChooser.APPROVE_OPTION) {
    	    File yourFolder = fc.getSelectedFile();
    	    logger.info( yourFolder.getPath() );
    	    BucketUtils.downloadFile(s3client, oggetto,  yourFolder.getPath()+"\\" );
    	    return true;
    	}
    	return false;
    }
    
    public boolean uploadFiles(Component parent,AmazonS3 s3client, String bucket, String path) throws Exception{
    	final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            BucketUtils.uploadFileFromLocalToS3(s3client,bucket,path,file);
            logger.info("Opening: " + file.getName() );
            return true;
        } else {
            logger.info("Open command cancelled by user.");
        }
    	return false;
    }
         
    
    
}
