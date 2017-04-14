package core;

import java.io.ByteArrayInputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;

import com.sun.javafx.util.Utils;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class FXController {
	@FXML
	private Button button;
	@FXML
	private ImageView currentFrame;

	private ScheduledExecutorService timer;
	private VideoCapture capture;
	private boolean cameraActive;
	private CascadeClassifier faceCascade;
	private int absoluteFaceSize;


	public void initialize()
	{
		System.out.println("Init");
		this.capture = new VideoCapture();
		this.cameraActive = false;
		this.absoluteFaceSize = 0;
		this.faceCascade = new CascadeClassifier();
		this.faceCascade.load("resources/haarcascades/haarcascade_frontalface_alt.xml");
	}
	
	@FXML
	protected void startCamera(ActionEvent event) {
		System.out.println("Cam Start");
		// set a fixed width for the frame
		this.currentFrame.setFitWidth(600);
		// preserve image ratio
		this.currentFrame.setPreserveRatio(true);

		if (!this.cameraActive) {
			// start the video capture
			this.capture.open(0);

			// is the video stream available?
			if (this.capture.isOpened()) {
				this.cameraActive = true;

				// grab a frame every 33 ms (30 frames/sec)
				Runnable frameGrabber = new Runnable() {

					@Override
					public void run() {
						// effectively grab and process a single frame
						Mat frame = grabFrame();

						MatOfByte buffer = new MatOfByte();
				        Imgcodecs.imencode(".png", frame, buffer);
						Image imageToShow = new Image(new ByteArrayInputStream(buffer.toArray()));
						Platform.runLater(new Runnable() {
					        @Override public void run() { currentFrame.setImage(imageToShow); }
					});
						//updateImageView(currentFrame, imageToShow);
					}
				};

				this.timer = Executors.newSingleThreadScheduledExecutor();
				this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

				// update the button content
				this.button.setText("Stop Camera");
			} else {
				// log the error
				System.err.println("Impossible to open the camera connection...");
			}
		} else {
			// the camera is not active at this point
			this.cameraActive = false;
			// update again the button content
			this.button.setText("Start Camera");

			// stop the timer
			this.stopAcquisition();
		}

	}
	
	private Mat grabFrame()
	{
		//System.out.println("Frame");
		Mat frame = new Mat();
		
		// check if the capture is open
		if (this.capture.isOpened())
		{
			try
			{
				this.capture.read(frame);
				
				if (!frame.empty())
				{
					this.detectAndDisplay(frame);
				}
				
				
			}
			catch (Exception e)
			{
				// log the error
				System.err.println("Exception during the frame elaboration: " + e);
			}
		}
		
		return frame;
	}
	
	private void detectAndDisplay(Mat frame)
	{
		MatOfRect faces = new MatOfRect();
		Mat grayFrame = new Mat();
		
		// convert the frame in gray scale
		Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
		// equalize the frame histogram to improve the result
		Imgproc.equalizeHist(grayFrame, grayFrame);
		

		// compute minimum face size (20% of the frame height, in our case)
		if (this.absoluteFaceSize == 0)
		{
			int height = grayFrame.rows();
			if (Math.round(height * 0.2f) > 0)
			{
				this.absoluteFaceSize = Math.round(height * 0.2f);
			}
		}
		
		
		// detect faces
		this.faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE, new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());
				
		// each rectangle in faces is a face: draw them!
		Rect[] facesArray = faces.toArray();
		for (int i = 0; i < facesArray.length; i++)
			Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0), 3);

	}
	
	private void stopAcquisition()
	{
		if (this.timer != null && !this.timer.isShutdown())
		{
			try
			{
				// stop the timer
				this.timer.shutdown();
				this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException e)
			{
				// log any exception
				System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
			}
		}
		
		if (this.capture.isOpened())
		{
			// release the camera
			this.capture.release();
		}
	}
	
	protected void setClosed()
	{
		this.stopAcquisition();
	}

}


