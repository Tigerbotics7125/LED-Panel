import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;

public class TestGif extends Applet {
	public void paint(Graphics g){
		try {
			final File folder = new File("../src/arrays/");
			for (final File fileEntry : folder.listFiles()) {
				String fileName=fileEntry.getName();
				fileName=fileName.substring(0,fileName.indexOf("_"));
				Gif theGif=new Gif(fileName,Color.blue);
				theGif.draw(g);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
