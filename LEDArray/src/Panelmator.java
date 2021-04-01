import java.awt.Color;
import java.io.File;
import java.io.IOException;

public class Panelmator {
	private static long transmitTime;
	private static final long FRAME_DELAY=100;
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		transmitTime=0;
		try {
			final File folder = new File("../src/arrays/");
			Gif[] theGifs=new Gif[folder.listFiles().length];
			for (int i=0;i<folder.listFiles().length;i++) {
				final File fileEntry=folder.listFiles()[i];
				String fileName=fileEntry.getName();
				fileName=fileName.substring(0,fileName.indexOf("_"));
				theGifs[i]=new Gif(fileName,Color.blue);
			}
			while(true) {
				/*
				 * CODE THAT READS THE TEAM COLOR FROM THE ROBORIO 
				 * GOES HERE
				 */
				/*
				 * CODE THAT READS THE CURRENT IMAGE FROM THE ROBORIO
				 * GOES HERE
				 */
				String theImage="bb8popupgif";
				String curImage="";
				int frame=0;
				for(Gif g:theGifs) {
					if(theImage==g.getName()&&curImage!=theImage) {
						curImage=theImage;
						frame=0;
					}
					if(transmit(g,frame))
						frame++;
					if(frame==g.getFrames()) {
						frame=0;
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static boolean transmit(Gif g, int frame) throws Exception {
		// TODO Auto-generated method stub
		if(!SerialCommunication.isOpen()) {
			SerialCommunication.begin(0);
			transmitTime=System.currentTimeMillis();
		}
		if(System.currentTimeMillis()<transmitTime+FRAME_DELAY) {
			return false;
		}
		SerialCommunication.sendData(g.getData(frame));
		return true;
	}

}
