import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
/**
 * This class stores RGB information from a GIF, as well as draws 
 * to an Applet for debugging.
 * @author stovri
 *
 */
public class GifFrame {
	int[][] red;
	int[][] blue;
	int[][] green;
	Color[][] color;
	Color background;

	/**
	 * 
	 * @param i The height of the frame
	 * @param j The width of the frame
	 * @param background The background color of the image
	 */
	public GifFrame(int i, int j, Color background){
		red=new int[i][j];
		green=new int[i][j];
		blue=new int[i][j];
		color=new Color[i][j];

		for(int a=0;a<red.length;a++) {
			for(int b=0;b<red[a].length;b++) {
				red[a][b]=background.getRed();
				green[a][b]=background.getGreen();
				blue[a][b]=background.getBlue();
				color[a][b]=background;
			}
		}
		this.background=background;
	}
	/**
	 * 
	 * @param reds A string of red values between 0 and 255 for the frame
	 * @param greens A string of green values between 0 and 255 for the frame
	 * @param blues A string of blue values between 0 and 255 for the frame
	 * @param alphas A string of alpha values between 0 and 255 for the frame
	 */
	public void loadAll(String reds, String greens, String blues, String alphas) {
		//strip out all non numbers with this regex
		String reg="[^0-9 ]";

		Scanner redIn=new Scanner(reds.replaceAll(reg,""));
		Scanner greenIn=new Scanner(greens.replaceAll(reg,""));
		Scanner blueIn=new Scanner(blues.replaceAll(reg,""));
		Scanner alphaIn=new Scanner(alphas.replaceAll(reg,""));

		//Step through each number in the string and store it into 
		//the array
		for(int i=0;i<red.length;i++) {
			for(int j=0;j<red[i].length;j++) {

				//It is either transparent or not for our display
				int a=alphaIn.nextInt()/255;
				//Set up the multiplier for transparency
				if(a==1)
					a=0;
				else
					a=1;

				//get the red value, plus the red portion of the background color
				//times the alpha
				int r=redIn.nextInt()+color[i][j].getRed()*a;
				//get the blue value, plus the blue portion of the background color
				//times the alpha
				int b=blueIn.nextInt()+color[i][j].getBlue()*a;
				//get the green value, plus the green portion of the background color
				//times the alpha
				int g=greenIn.nextInt()+color[i][j].getGreen()*a;
				//make it into a color for debugging purposes
				color[i][j]=new Color((int)r,(int)g,(int)b);
			}
		}
		//close the streams
		redIn.close();
		blueIn.close();
		greenIn.close();
		alphaIn.close();
	}
	/**
	 * This is a debugging method - check to see if your import worked
	 * @param g
	 */
	public void draw(Graphics g) {
		for(int i=0;i<color.length;i++) {
			for(int j=0;j<color[i].length;j++) {
				g.setColor(color[i][j]);
				g.fillRect(j*15, i*15, 15,15);
			}
		}
	}
	/**
	 * Return the number of rows in the frame
	 * @return
	 */
	public int getRows() {
		// TODO Auto-generated method stub
		return red.length;
	}
	public String getData() {
		// TODO Auto-generated method stub
		int count=0;
		String data="";
		for(int i=0;i<red.length;i++) {
			for(int j=0;j<red[i].length;j++) {
				data+=count+" "+green[i][j]+" "+red[i][j]+" "+blue[i][j]+" ";
				count++;
			}
		}
		return data;
	}
}
