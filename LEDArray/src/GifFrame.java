import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class GifFrame {
	int[][] red;
	int[][] blue;
	int[][] green;
	Color[][] color;
	Color background;

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
	public void loadAll(String reds, String greens, String blues, String alphas) {
		String reg="[^0-9 ]";
		reds=reds.replaceAll(reg,"");
		//System.out.println(blues.replaceAll(reg,""));
		Scanner redIn=new Scanner(reds.replaceAll(reg,""));
		Scanner greenIn=new Scanner(greens.replaceAll(reg,""));
		Scanner blueIn=new Scanner(blues.replaceAll(reg,""));
		Scanner alphaIn=new Scanner(alphas.replaceAll(reg,""));
		for(int i=0;i<red.length;i++) {
			for(int j=0;j<red[i].length;j++) {
				
				int a=alphaIn.nextInt()/255;
				if(a==1)
					a=0;
				else
					a=1;
				int r=redIn.nextInt()+color[i][j].getRed()*a;
				int b=blueIn.nextInt()+color[i][j].getBlue()*a;
				int g=greenIn.nextInt()+color[i][j].getGreen()*a;
				//corrected to 1 or 0
				//System.out.print(r+",");
				color[i][j]=new Color((int)r,(int)g,(int)b);
			}
			///System.out.println();
		}
		redIn.close();
		blueIn.close();
		greenIn.close();
		alphaIn.close();
	}
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		for(int i=0;i<color.length;i++) {
			for(int j=0;j<color[i].length;j++) {
				g.setColor(color[i][j]);
				//System.out.print(color[i][j].getRed()+",");
				g.fillRect(j*15, i*15, 15,15);
			}
			//System.out.println();
		}
		//System.out.println();
	}
	public int getRows() {
		// TODO Auto-generated method stub
		return red.length;
	}
}
