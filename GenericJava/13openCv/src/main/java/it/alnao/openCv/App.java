package it.alnao.openCv;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
/*
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;
*/
/*
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
*/
public class App /*extends Application*/{
    public static void main( String[] args )    {
    	App.mateye();
    	
    	App.faseDetector();//launch(args);
    	/*
        WritableRaster cr = img.getRaster();
        WritableRaster wr = img.copyData(null);
    	BufferedImage img2= new BufferedImage(94, 94, BufferedImage.TYPE_INT_RGB);
        img2.setData(wr);
        JFrame frame = new JFrame("uiuxcu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new JLabel(new ImageIcon(img2)));
        frame.pack();
        frame.setSize(new Dimension(500,500));
        frame.setVisible(true);
        */
    }

    
    public static void mateye(  )    {
    	//see https://opencv-java-tutorials.readthedocs.io/en/latest/02-first-java-application-with-opencv.html
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat mat = Mat.eye(3, 3, CvType.CV_8UC1);
        System.out.println("mat = " + mat.dump());
    }
    public static void faseDetector(  )    {    
        FaceDetector.start("in.jpg"
        		, "out.jpg"
        		//#https://github.com/kipr/opencv/blob/master/data/haarcascades/haarcascade_frontalface_default.xml
        		, "haarcascade_frontalface_alt.xml");
    }

    
    /* //see https://opencv-java-tutorials.readthedocs.io/en/latest/02-first-java-application-with-opencv.html
	@Override
	public void start(Stage primaryStage) throws Exception {
    	
		// load the FXML resource
		FXMLLoader loader = new FXMLLoader(getClass().getResource("FaceDetection.fxml"));
		BorderPane root = (BorderPane) loader.load();
		// set a whitesmoke background
		root.setStyle("-fx-background-color: whitesmoke;");
		// create and style a scene
		Scene scene = new Scene(root, 800, 600);
		//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		// create the stage with the given title and the previously created
		// scene
		primaryStage.setTitle("Face Detection and Tracking");
		primaryStage.setScene(scene);
		// show the GUI
		primaryStage.show();
		
		// init the controller
		App.faseDetector(); 
		
	}*/
}
