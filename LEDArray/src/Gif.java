import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.Timer;

public class Gif {
	private Scanner redIn;
	Scanner greenIn;
	Scanner blueIn;
	Scanner alphaIn;
	GifFrame[] frames;

	public Gif(String fileName, Color background) throws IOException{
		File redFile=new File("../src/arrays/"+fileName+"_reds_array.txt");
		File greenFile=new File("../src/arrays/"+fileName+"_greens_array.txt");
		File blueFile=new File("../src/arrays/"+fileName+"_blues_array.txt");
		File alphaFile=new File("../src/arrays/"+fileName+"_alphas_array.txt");

		redIn=new Scanner(redFile);
		greenIn=new Scanner(greenFile);
		blueIn=new Scanner(blueFile);
		alphaIn=new Scanner(alphaFile);

		String firstRed=redIn.nextLine();
		greenIn.nextLine();
		blueIn.nextLine();
		alphaIn.nextLine();

		String reg="[^0-9 ]";
		firstRed=firstRed.replaceAll(reg," ").trim();
		int numFrames=Integer.parseInt(firstRed.substring(0,firstRed.indexOf(" ")));
		firstRed=firstRed.substring(firstRed.indexOf(" ")).trim();
		int numX=Integer.parseInt(firstRed.substring(0,firstRed.indexOf(" ")));
		firstRed=firstRed.substring(firstRed.indexOf(" ")).trim();
		int numY=Integer.parseInt(firstRed);

		frames=new GifFrame[numFrames];
		for(int i=0;i<numFrames;i++) {
			frames[i]=new GifFrame(numX,numY,background);
		}
		loadframes();
		redIn.close();
		greenIn.close();
		alphaIn.close();
		blueIn.close();
	}
	private void loadframes() {
		// TODO Auto-generated method stub
		String reds="";
		String blues="";
		String greens="";
		String alphas="";
		for(int frameNum=0;frameNum<frames.length;frameNum++) {
			reds="";
			blues="";
			greens="";
			alphas="";
			
			for(int row=0;row<frames[frameNum].getRows();row++) {
				reds+=redIn.nextLine();
				greens+=greenIn.nextLine();
				blues+=blueIn.nextLine();
				alphas+=alphaIn.nextLine();
			}
			reds+=redIn.nextLine();
			greens+=greenIn.nextLine();
			blues+=blueIn.nextLine();
			alphas+=alphaIn.nextLine();
			//System.out.println(reds);
			frames[frameNum].loadAll(reds, greens, blues, alphas);
			//System.out.println(frames[frameNum]);
		}
	}
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		for(GifFrame f:frames) {
			f.draw(g);
			delay(50);
			//System.out.println(f);
		}
	}
	public void delay(int n) {
		long startDelay=System.currentTimeMillis();
		long endDelay=0;
		while(endDelay-startDelay<n) {
			endDelay=System.currentTimeMillis();
		}
	}

}
