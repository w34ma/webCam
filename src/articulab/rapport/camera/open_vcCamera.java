package articulab.rapport.camera;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.googlecode.javacv.FFmpegFrameGrabber;
import com.googlecode.javacv.FrameRecorder;
import com.googlecode.javacv.OpenCVFrameRecorder;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import articulab.rapport.Config;
import edu.usc.ict.vhmsg.MessageEvent;
import edu.usc.ict.vhmsg.MessageListener;
import edu.usc.ict.vhmsg.VHMsg;

public class open_vcCamera implements MessageListener{
	private volatile boolean flag = true;
	private FrameRecorder recorder1;
	private VHMsg vhmsgSubscriber;
	public static String path_to_ffmpeg = "/usr/local/bin/ffmpeg";


	public open_vcCamera() throws Exception{
		System.setProperty("VHMSG_SERVER", Config.VHMSG_SERVER_URL);
		System.out.println("VHMSG_SERVER: "
				+ System.getProperty("VHMSG_SERVER"));
		System.out.println("VHMSG_SCOPE: " + System.getProperty("VHMSG_SCOPE"));
		vhmsgSubscriber = new VHMsg();
		vhmsgSubscriber.openConnection();
		vhmsgSubscriber.enableImmediateMethod();
		vhmsgSubscriber.addMessageListener(this);
		vhmsgSubscriber.subscribeMessage("vrCamera");
		System.out.println("VHMsg started");
		
		System.out.print("open camera");
		CvCapture capture1 = cvCreateCameraCapture(0); //CV_CAP_ANY = -1 USBCam = 0
		cvSetCaptureProperty(capture1,CV_CAP_PROP_FRAME_WIDTH,640);
		cvSetCaptureProperty(capture1,CV_CAP_PROP_FRAME_HEIGHT,480);

		cvNamedWindow("LiveVid",CV_WINDOW_AUTOSIZE);
		recorder1 = new OpenCVFrameRecorder("RecordVid.avi",640,480);
		recorder1.setVideoCodec(CV_FOURCC('M','J','P','G'));
		recorder1.setFrameRate(30);
		recorder1.setPixelFormat(1);
	    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
	    System.out.println("vedio start: "+strDate);
		recorder1.start();
		
		IplImage img1;
		
		while (flag){
			img1 = cvQueryFrame(capture1);
			if(img1 == null) break;
			cvShowImage("LiveVid",img1);
			recorder1.record(img1);
//			char c = (char) cvWaitKey(15);
//			if(c == 'q') break;
		}
		
		recorder1.stop();
	    sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
	     now = new Date();
	     strDate = sdfDate.format(now);
	    System.out.println("vedio end: "+strDate);
		cvDestroyWindow("LiveVid");
		cvReleaseCapture(capture1);
		Runtime rt = Runtime.getRuntime();
		System.out.println("Reproducing mp4....");
		Process pr1 = rt.exec("cd /Users/Vivi/Documents/workspace/InMind_vcCamera");
		Process pr2 = rt.exec(path_to_ffmpeg + " -i RecordVid.avi -c:v libx264 -preset slow -crf 19 -c:a libvo_aacenc -b:a 128k new_output.mp4");
		System.out.println("end vedio");
	}

	@Override
	public void messageAction(MessageEvent e) {
		// TODO Auto-generated method stub
		System.out.println(e);
		String[] tokens = e.toString().split(" ");
		 if (tokens[0].equals("vrCamera") && tokens[1].equals("end")) {
			flag = false;
		}
	}
}
