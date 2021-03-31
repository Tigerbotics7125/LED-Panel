import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * A class to organize and handle the conversion of animated GIF files
 * into an array that can be transmitted to an Arduino
 * @author stovri
 *
 */
public class Gif {
	private Scanner redIn;
	private Scanner greenIn;
	private Scanner blueIn;
	private Scanner alphaIn;
	private GifFrame[] frames;

	/**
	 * This method will load all of the RGB arrays into each GifFrame
	 * @param fileName The sub name of the file
	 * @param background The color of the background of the GIF - used for 
	 * team colors
	 * @throws IOException Catch file problems
	 */
	public Gif(String fileName, Color background) throws IOException{
		//Read the RGBA arrays of the top level GIF
		File redFile=new File("../src/arrays/"+fileName+"_reds_array.txt");
		File greenFile=new File("../src/arrays/"+fileName+"_greens_array.txt");
		File blueFile=new File("../src/arrays/"+fileName+"_blues_array.txt");
		File alphaFile=new File("../src/arrays/"+fileName+"_alphas_array.txt");

		//Set up sanners to parse the strings
		redIn=new Scanner(redFile);
		greenIn=new Scanner(greenFile);
		blueIn=new Scanner(blueFile);
		alphaIn=new Scanner(alphaFile);

		//Grab the first line of the red array to find the size of
		//the GIF frames
		String firstRed=redIn.nextLine();
		//Eat the other first lines
		greenIn.nextLine();
		blueIn.nextLine();
		alphaIn.nextLine();

		//Strip out all non-numbers
		String reg="[^0-9 ]";
		firstRed=firstRed.replaceAll(reg," ").trim();
		//Get the number of frames of animation
		int numFrames=Integer.parseInt(firstRed.substring(0,firstRed.indexOf(" ")));
		firstRed=firstRed.substring(firstRed.indexOf(" ")).trim();
		//Get the number of rows in the animation
		int numX=Integer.parseInt(firstRed.substring(0,firstRed.indexOf(" ")));
		firstRed=firstRed.substring(firstRed.indexOf(" ")).trim();
		//Get the number of columns in the animation
		int numY=Integer.parseInt(firstRed);

		//Initialize the GifFrame array
		frames=new GifFrame[numFrames];
		for(int i=0;i<numFrames;i++) {
			frames[i]=new GifFrame(numX,numY,background);
		}
		//Load all the frames
		loadframes();
		//Close the streams
		redIn.close();
		greenIn.close();
		alphaIn.close();
		blueIn.close();
	}
	private void loadframes() {
		String reds="";
		String blues="";
		String greens="";
		String alphas="";
		//Step through all the frames
		for(int frameNum=0;frameNum<frames.length;frameNum++) {
			reds="";
			blues="";
			greens="";
			alphas="";
			
			//Load the number of rows into each color
			for(int row=0;row<frames[frameNum].getRows();row++) {
				reds+=redIn.nextLine();
				greens+=greenIn.nextLine();
				blues+=blueIn.nextLine();
				alphas+=alphaIn.nextLine();
			}
			//Eat the last line
			reds+=redIn.nextLine();
			greens+=greenIn.nextLine();
			blues+=blueIn.nextLine();
			alphas+=alphaIn.nextLine();
			
			//Pass it to the GifFrame
			frames[frameNum].loadAll(reds, greens, blues, alphas);
		}
	}
	/**
	 * Draw method for debugging GIF input. Do not use if you can avoid it.
	 * @param g Instance of the Graphics class
	 */
	public void draw(Graphics g) {
		//Draw each frame with a 50ms delay
		for(GifFrame f:frames) {
			f.draw(g);
			delay(50);
		}
	}
	
	/**
	 * Ugly, horrible hack to delay the execution of code.
	 * @param n Number of milliseconds to wait
	 */
	public void delay(int n) {
		long startDelay=System.currentTimeMillis();
		long endDelay=0;
		while(endDelay-startDelay<n) {
			endDelay=System.currentTimeMillis();
		}
	}

}
