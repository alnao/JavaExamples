package it.alnao.awssdkexamples;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfilesConfigFile;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

/** see https://stackoverflow.com/questions/10093558/hand-coded-gui-java
 * 
 * see https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/auth/profile/ProfilesConfigFile.html
 * see http://www.java2s.com/example/java-api/com/amazonaws/auth/profile/profilesconfigfile/profilesconfigfile-1-0.html 
 * see http://www.java2s.com/example/java-src/pkg/aws/profile-7503f.html
 * */

public class ProfilesMenu {
    private static final Logger logger = LoggerFactory.getLogger(ProfilesMenu.class);	
    public static final String DEFAULT_PROFILE="default";
    public static void main(String[] args) {
    	logger.info("Application 03_ProfilesMenu start");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
            	//Regions region = Regions.EU_WEST_1;
            	ArrayList<String> profilesList = ProfilesUtils.getCustomerList();
                new ProfilesMenu(profilesList);
            }
        });
    }  
    private JFrame frame ;
    private JLabel statusLabel;
    private JPanel statusPanel;
    private JPanel contentPane;
    
    public ProfilesMenu(ArrayList<String> profilesList ) {
    	//see menu https://docs.oracle.com/javase/tutorial/uiswing/components/menu.html
    	JMenuBar menuBar;
    	JMenu menu, submenu;
    	JMenuItem menuItem;
    	JRadioButtonMenuItem rbMenuItem;
    	JCheckBoxMenuItem cbMenuItem;
    	menuBar = new JMenuBar();
    	menu = new JMenu("Profiles");
    	menuBar.add(menu);
    	//menu.addSeparator();
    	ButtonGroup group = new ButtonGroup();
    	for (String prof : profilesList) {
        	rbMenuItem = new JRadioButtonMenuItem(prof);
        	if (DEFAULT_PROFILE.equals(prof))
        		rbMenuItem.setSelected(true);
        	rbMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                	logger.info("load profile " + prof);
                	updateStatusPanel(prof);
              	}
            });
        	group.add(rbMenuItem);
        	menu.add(rbMenuItem);
    	}
    	
    	contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.setPreferredSize(new Dimension(320 * 4,700));
        contentPane.add(new JLabel("Content"), BorderLayout.CENTER);
        
        frame = new JFrame("Profiles Menu");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setLocationByPlatform(true);
        frame.setSize(200,200);
        frame.setContentPane(contentPane);        
        frame.pack();
        frame.setJMenuBar(menuBar);
        frame.setVisible(true);
        
        updateStatusPanel(DEFAULT_PROFILE);
    }
    private void updateStatusPanel(String s){
        if (contentPane!=null)
        	frame.remove(contentPane);
        contentPane=createBucketPanel(s);
        frame.setContentPane(contentPane); 
        
    	if (statusPanel!=null)
    		frame.remove(statusPanel);
        statusPanel = new JPanel();
        statusPanel.setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 0));
        statusPanel.setPreferredSize(new Dimension(frame.getWidth(), 16));
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
        statusLabel = new JLabel("Buckets Frame with profile: " +s);
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusPanel.add(statusLabel);
        frame.add(statusPanel, BorderLayout.NORTH);//SOUTH
        frame.validate();
        frame.repaint();
    }
    
    private JPanel createBucketPanel(String profile) {
    	Regions region = Regions.EU_WEST_1;
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
        		//.withCredentials(null)
        		.withRegion(region)
        		.withCredentials( new ProfileCredentialsProvider (profile) )
        		//.withCredentials( ProfilesUtils.getAwsCredentialProvider("AAAAA","KEY") )
        		.build();
        BucketsFrame frame=new BucketsFrame();
    	JPanel p = frame.createBucketPanel(s3,profile,false);
    	return p;
    }
    
    
    
    
}
