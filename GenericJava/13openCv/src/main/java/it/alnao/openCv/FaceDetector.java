package it.alnao.openCv;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;
import javafx.fxml.FXML;
//see https://opencv-java-tutorials.readthedocs.io/en/latest/06-face-detection-and-tracking.html
//see https://github.com/opencv-java/face-detection/blob/master/src/it/polito/teaching/cv/FaceDetectionController.java
public class FaceDetector {
	//https://stackoverflow.com/questions/28231066/how-to-crop-the-detected-face-image-in-opencv-java 
		public static void start(String inFile,String outFile,String classifierPath) {
	    int x,y,height,width;
	
	    //CascadeClassifier faceDetector = new CascadeClassifier(FaceDetector.class.getResource("haarcascade_frontalface_alt.xml").getPath());
	    CascadeClassifier faceDetector=new CascadeClassifier();
	    faceDetector.load(classifierPath);
	    //System.out.println(""+faceDetector);
	   // Mat image = Highgui .imread(FaceDetector.class.getResource("D:/shekar.jpg").getPath());
	    Mat image = Imgcodecs.imread(inFile);
	    MatOfRect faceDetections = new MatOfRect();
	    faceDetector.detectMultiScale(image, faceDetections);
	
	    System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));
	
	    for (Rect rect : faceDetections.toArray()) {
	    	Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
	                new Scalar(0, 255, 0));
	    }

	    System.out.println(String.format("Writing %s", outFile));
	    Imgcodecs.imwrite(outFile, image);
	    HighGui.imshow(outFile, image);
	    HighGui.waitKey(0); //https://stackoverflow.com/questions/54103815/opencv-4-java-highgui-imshow
	}
	/*
	 * NOO https://stackoverflow.com/questions/20440573/opencv-mapping-images
	public static void start(String filePath,String classifierPath) {
	    BufferedImage img = getImage(filePath);
	    CascadeClassifier faceDetector = new CascadeClassifier(classifierPath);
	    Mat image = HighGui.toBufferedImage(img);

	    MatOfRect faceDetections = new MatOfRect();
	    faceDetector.detectMultiScale(image, faceDetections);

	    if( faceDetections.toArray().length == 0){
	        //  load("C:\\Users\\Yousra\\Desktop\\download.jpg") ){
	      System.out.println("not found");

	    }

	    System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));


	    for (Rect rect : faceDetections.toArray()) {
	        Core.rectangle(img, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
	                new Scalar(0, 255, 0));


	    }
	    WritableRaster cr = img.getRaster();
	    WritableRaster wr = img.copyData(null);

	    for(int b=0; b<94; b++){
	          for(int a=0; a<94; a++){
	              for(int h = faceDetections.toArray()[0].y; h< 60+ 94; h++){
	                  for(int w = faceDetections.toArray()[0].x; w< 50+ 94; w++){

	                      wr.setSample(b, a, 0, cr.getSample(w, h, 0));


	                  }

	              }
	          }

	    }

	    BufferedImage img2= new BufferedImage(94, 94, BufferedImage.TYPE_INT_RGB);
	    img2.setData(wr);

	    JFrame frame = new JFrame("uiuxcu");
	    frame.getContentPane().add(new JLabel(new ImageIcon(img2)));
	    frame.pack();
	    frame.setVisible(true);
	    String filename = "ouput.png";
	    System.out.println(String.format("Writing %s", filename));
	    HighGui.imshow(filename, img);
	}
	public static BufferedImage getImage(String imageName) {
	    try {
	        File input = new File(imageName);
	        BufferedImage image = ImageIO.read(input);
	        return image;
	    } catch (IOException ie) {
	        System.out.println("Error:" + ie.getMessage());
	    }
	    return null;
	}}
	*/
	/*
	// a timer for acquiring the video stream
	private ScheduledExecutorService timer;
	// the OpenCV object that performs the video capture
	private VideoCapture capture;
	// a flag to change the button behavior
	private boolean cameraActive;
	@FXML
	private ImageView originalFrame;
	// face cascade classifier
	private CascadeClassifier faceCascade;
	private int absoluteFaceSize;

	public BufferedImage init(String fileName,String... classifierPath){
		this.capture = new VideoCapture();
		this.faceCascade = new CascadeClassifier();
		this.absoluteFaceSize = 0;
		this.checkboxSelection(classifierPath);
		return this.startCamera(fileName);
	}
	private void updateImageView( Image image){
		Utils.onFXThread(originalFrame.imageProperty(), image);
	}
	private BufferedImage startCamera(String fileName){	
		if (!this.cameraActive){
			this.capture.open(fileName);
			if (this.capture.isOpened()){
				this.cameraActive = true;
				// grab a frame every 33 ms (30 frames/sec)
				Runnable frameGrabber = new Runnable() {
					@Override
					public void run(){
						// effectively grab and process a single frame
						Mat frame = grabFrame();
						// convert and show the frame
						Image imageToShow = Utils.mat2Image(frame);
						updateImageView(imageToShow);
					}
				};
				
				this.timer = Executors.newSingleThreadScheduledExecutor();
				this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
			}else{
				// log the error
				System.err.println("Failed to open the camera connection...");
			}
		}
		
	}
	private Mat grabFrame()	{
		Mat frame = new Mat();
		// check if the capture is open
		if (this.capture.isOpened()){
			try{
				this.capture.read(frame);
				// if the frame is not empty, process it
				if (!frame.empty()){
					// face detection
					this.detectAndDisplay(frame);
				}
			}catch (Exception e){
				// log the (full) error
				System.err.println("Exception during the image elaboration: " + e);
			}
		}
		return frame;
	}
	
	private void detectAndDisplay(Mat frame)	{
		MatOfRect faces = new MatOfRect();
		Mat grayFrame = new Mat();
		
		// convert the frame in gray scale
		Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
		// equalize the frame histogram to improve the result
		Imgproc.equalizeHist(grayFrame, grayFrame);
		
		// compute minimum face size (20% of the frame height, in our case)
		if (this.absoluteFaceSize == 0){
			int height = grayFrame.rows();
			if (Math.round(height * 0.2f) > 0){
				this.absoluteFaceSize = Math.round(height * 0.2f);
			}
		}
		
		// detect faces
		this.faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
				new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());
				
		// each rectangle in faces is a face: draw them!
		Rect[] facesArray = faces.toArray();
		for (int i = 0; i < facesArray.length; i++)
			Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0), 3);
			
	}
	
	
	
	private void checkboxSelection(String... classifierPath){
	        // load the classifier(s)
	        for (String xmlClassifier : classifierPath)
	        {
	                this.faceCascade.load(xmlClassifier);
	        }
	}
	*/
}
