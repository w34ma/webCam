package articulab.rapport.camera;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.sound.sampled.*;

import edu.usc.ict.vhmsg.MessageEvent;
import edu.usc.ict.vhmsg.MessageListener;
import articulab.rapport.Config;
import edu.usc.ict.vhmsg.VHMsg;

public class MyRecord implements MessageListener {

	private VHMsg vhmsgSubscriber;
	AudioFormat af = null;
	TargetDataLine td = null;
	SourceDataLine sd = null;
	ByteArrayInputStream bais = null;
	ByteArrayOutputStream baos = null;
	AudioInputStream ais = null;
	Boolean stopflag = false;
	public static String path_to_ffmpeg = "/usr/local/bin/ffmpeg";
	private String s;

	public MyRecord(String name) throws InterruptedException {
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
		s = name;
		capture();
	}

	public void capture() throws InterruptedException {
		try {
			af = getAudioFormat();
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, af);
			td = (TargetDataLine) (AudioSystem.getLine(info));
			td.open(af);
			td.start();

//			Record record = new Record();
//			Thread t1 = new Thread(record);
		    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
		    Date now = new Date();
		    String strDate = sdfDate.format(now);
		    System.out.println("audio start: "+strDate);
//			t1.start();
		    byte bts[] = new byte[10000];
		    baos = new ByteArrayOutputStream();
			try {
				System.out.println("open audio record");
				stopflag = false;
				while (stopflag != true) {
					int cnt = td.read(bts, 0, bts.length);
					if (cnt > 0) {
						baos.write(bts, 0, cnt);
					}
				}
			     sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
			     now = new Date();
			     strDate = sdfDate.format(now);
			    System.out.println("audio end: "+strDate);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (baos != null) {
						baos.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					td.drain();
					td.close();
				}
			}
			System.out.println("saving audio....");
			save();
			System.out.println("audio saved....");
		} catch (LineUnavailableException ex) {
			ex.printStackTrace();
			return;
		}
	}

	public void save() {
		af = getAudioFormat();

		byte audioData[] = baos.toByteArray();
		bais = new ByteArrayInputStream(audioData);
		ais = new AudioInputStream(bais, af, audioData.length
				/ af.getFrameSize());
		File file = null;
		try {
			File filePath = new File("/Users/Vivi/Documents/workspace/InMind_vcCamera");
			file = new File(filePath.getPath() + "/" + s +".mp3");
			AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {

				if (bais != null) {
					bais.close();
				}
				if (ais != null) {
					ais.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public AudioFormat getAudioFormat() {
      float sampleRate = 16000.0F;  
      int sampleSizeInBits = 16;  
      int channels = 1;  
      boolean signed = true;  
      boolean bigEndian = true;  
      return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed,bigEndian);  
	}

//	class Record implements Runnable {
//		byte bts[] = new byte[10000];
//
//		public void run() {
//			baos = new ByteArrayOutputStream();
//			try {
//				System.out.println("open audio record");
//				stopflag = false;
//				while (stopflag != true) {
//					int cnt = td.read(bts, 0, bts.length);
//					if (cnt > 0) {
//						baos.write(bts, 0, cnt);
//					}
//				}
//			    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
//			    Date now = new Date();
//			    String strDate = sdfDate.format(now);
//			    System.out.println("audio end: "+strDate);
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				try {
//					if (baos != null) {
//						baos.close();
//					}
//				} catch (IOException e) {
//					e.printStackTrace();
//				} finally {
//					td.drain();
//					td.close();
//				}
//			}
//			System.out.println("saving audio....");
//			save();
//			System.out.println("audio saved....");
//			Runtime rt = Runtime.getRuntime();
//			try {
//				System.out.println("Reproducing mp4....");
//				Process pr1 = rt
//						.exec("cd /Users/Vivi/Documents/workspace/InMind_vcCamera");
//				Process pr2 = rt
//						.exec(path_to_ffmpeg
//								+ " -i RecordVid.avi -c:v libx264 -preset slow -crf 19 -c:a libvo_aacenc -b:a 128k new_output.mp4");
//				System.out.println("start merging....");
//				Process pr1 = rt.exec("cd /Users/Vivi/Documents/workspace/InMind_vcCamera");
//				Process pr3 = rt
//						.exec(path_to_ffmpeg
//								+ " -i new_output.mp4 -i audio.mp3 -c:v copy -c:a aac -strict experimental all_output.mp4");
//			} catch (IOException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
//		}
//	}

	@Override
	public void messageAction(MessageEvent e) {
		// TODO Auto-generated method stub
		System.out.println(e);
		String[] tokens = e.toString().split(" ");
		if (tokens[0].equals("vrCamera") && tokens[1].equals("end")) {
			stopflag = true;
		}
	}
}