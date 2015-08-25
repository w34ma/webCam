package articulab.rapport.camera;

import java.text.SimpleDateFormat;
import java.util.Date;

import articulab.rapport.Config;
import edu.usc.ict.vhmsg.MessageEvent;
import edu.usc.ict.vhmsg.MessageListener;
import edu.usc.ict.vhmsg.VHMsg;

public class vcCameraMain implements MessageListener {

	// subscriber
	private VHMsg vhmsgSubscriber;
	static volatile open_vcCamera cur_camera;
	static volatile MyRecord mr;
	private Thread t1;
	private Thread t2;
	public static String path_to_ffmpeg = "/usr/local/bin/ffmpeg";

	public vcCameraMain() {
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
	}

	@Override
	public void messageAction(MessageEvent e) {
		// TODO Auto-generated method stub
		System.out.println(e);
		String[] tokens = e.toString().split(" ");
		if (tokens[0].equals("vrCamera") && tokens[1].equals("start")) {
			System.out.println("start camera1 & 2");
			t1 = new Thread() {
				public void run() {
					System.out.println("new camera thread");
					try {
						cur_camera = new open_vcCamera();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			t2 = new Thread() {
				public void run() {
					System.out.println("new audio thread");
					try {
						mr = new MyRecord();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
		    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
		    Date now = new Date();
		    String strDate = sdfDate.format(now);
		    System.out.println("start: "+strDate);
			t1.start();
			t2.start();
		} 
//		else if (tokens[0].equals("vcCamera") && tokens[1].equals("end")) {
//			System.out.println("end camera1 & 2");
//			// cur_camera.setflag(false);
//			System.out.println("oh no");
//			// t.stop();
//		}
	}

	public static void main(String args[]) {
		new vcCameraMain();
	}
}
