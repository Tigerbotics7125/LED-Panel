import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author stovri
 * This class will fetch the integer RGBA values of an animated GIF, and write these values to
 * an Arduino friendly file for easy copy paste. Designed for use with a neopixel array.
 */
public class Gif2Array {
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		final File folder = new File("src/images/");
		convertFilesForFolder(folder);
	}
	
	/**
	 * 
	 * @param folder
	 * @throws IOException
	 */
	public static void convertFilesForFolder(final File folder)  throws IOException {
		//Step through each file in the images folder
		for (final File fileEntry : folder.listFiles()) {
			convertFiles(fileEntry);
		}
	}

	/**
	 * 
	 * @param initialFile
	 * @throws IOException
	 */
	private static void convertFiles(File initialFile)  throws IOException {
		InputStream targetStream = new FileInputStream(initialFile);

		//Capture all animation frames as their own image in an array.
		ImageFrame[] theFrames=readGIF(targetStream);

		// Strip the non-alpha characters from the file name
		String codeName=getCodeName(initialFile.getName());

		//Open the RGBA files for writing.
		FileWriter fWReds=new FileWriter("src/arrays/"+codeName+"_reds_array.txt");
		FileWriter fWGreens=new FileWriter("src/arrays/"+codeName+"_greens_array.txt");
		FileWriter fWBlues=new FileWriter("src/arrays/"+codeName+"_blues_array.txt");
		FileWriter fWAlphas=new FileWriter("src/arrays/"+codeName+"_alphas_array.txt");

		//Write the data type and array size int the RGBA files
		writeHeader(codeName+"_reds",fWReds,theFrames.length,theFrames[0].getImage().getHeight(),theFrames[0].getImage().getWidth());
		writeHeader(codeName+"_greens",fWGreens,theFrames.length,theFrames[0].getImage().getHeight(),theFrames[0].getImage().getWidth());
		writeHeader(codeName+"_blues",fWBlues,theFrames.length,theFrames[0].getImage().getHeight(),theFrames[0].getImage().getWidth());
		writeHeader(codeName+"_alphas",fWAlphas,theFrames.length,theFrames[0].getImage().getHeight(),theFrames[0].getImage().getWidth());

		//Step through each frame
		for(int k=0;k<theFrames.length;k++){

			//get the pixels of each frame as an array[x][y][RGBA]
			int[][][] colors=getPixels(theFrames[k].getImage());

			//Open a new set of curly brackets for the initialization of the Arduino array
			writeRowOpen(fWReds);
			writeRowOpen(fWGreens);
			writeRowOpen(fWBlues);
			writeRowOpen(fWAlphas);

			//Step through each row
			for(int i=0;i<colors.length;i++){

				//Open a new set of curly brackets for the initialization of the Arduino array
				writeRowOpen(fWReds);
				writeRowOpen(fWGreens);
				writeRowOpen(fWBlues);
				writeRowOpen(fWAlphas);

				//Step through each column
				for(int j=0;j<colors[i].length;j++){
					//write the color values of each cell to the correct file
					writeCell(fWReds,colors[i][j][0],j,colors[i].length);
					writeCell(fWGreens,colors[i][j][1],j,colors[i].length);
					writeCell(fWBlues,colors[i][j][2],j,colors[i].length);
					writeCell(fWAlphas,colors[i][j][3],j,colors[i].length);
				}

				//Close a set of curly brackets for the initialization of the Arduino array
				writeRowClose(fWReds,i,colors.length);
				writeRowClose(fWGreens,i,colors.length);
				writeRowClose(fWBlues,i,colors.length);
				writeRowClose(fWAlphas,i,colors.length);
			}

			//Close a set of curly brackets for the initialization of the Arduino array
			writeRowClose(fWReds,k,theFrames.length);
			writeRowClose(fWGreens,k,colors.length);
			writeRowClose(fWBlues,k,colors.length);
			writeRowClose(fWAlphas,k,colors.length);
		}

		//Close the files
		fWReds.close();
		fWGreens.close();
		fWBlues.close();
		fWAlphas.close();

	}


	/**
	 * @param name
	 * @return
	 */
	private static String getCodeName(String name) {
		// TODO Auto-generated method stub
		String retVal="";
		name=name.toLowerCase();
		for(int i=0;i<name.length();i++){
			String tmp=name.substring(i,i+1);
			if(tmp.compareTo("a")>=0&&tmp.compareTo("z")<=0)
				retVal+=tmp;
		}
		return retVal;
	}


	/**
	 * @param fW
	 * @param i
	 * @param length
	 * @throws IOException
	 */
	private static void writeRowClose(FileWriter fW, int i, int length) throws IOException {
		// TODO Auto-generated method stub
		fW.write("}");
		if(i<length-1)
			fW.write(", ");
		fW.write("\n");
	}

	/**
	 * @param fW
	 * @param color
	 * @param j
	 * @param length
	 * @throws IOException
	 */
	private static void writeCell(FileWriter fW, int color, int j, int length) throws IOException {
		// TODO Auto-generated method stub
		Integer val=new Integer(color);
		fW.write(val.toString());
		if(j<length-1)
			fW.write(", ");
	}

	/**
	 * @param fW
	 * @throws IOException
	 */
	private static void writeRowOpen(FileWriter fW) throws IOException {
		// TODO Auto-generated method stub
		fW.write("  {");
	}

	/**
	 * @param color
	 * @param fW
	 * @param numFrames
	 * @throws IOException
	 */
	private static void writeHeader(String color, FileWriter fW, int numFrames,int h, int w) throws IOException {
		// TODO Auto-generated method stub
		fW.write("byte "+color+"["+numFrames+"]["+h+"]["+w+"]=\n");
	}

	//from here: https://stackoverflow.com/questions/8933893/convert-each-animated-gif-frame-to-a-separate-bufferedimage
	/**
	 * @param targetStream
	 * @return
	 * @throws IOException
	 */
	private static ImageFrame[] readGIF(InputStream targetStream) throws IOException {
		ArrayList<ImageFrame> frames = new ArrayList<ImageFrame>(2);
		ImageReader reader = (ImageReader) ImageIO.getImageReadersByFormatName("gif").next();
		reader.setInput(ImageIO.createImageInputStream(targetStream));

		int width = -1;
		int height = -1;

		IIOMetadata metadata = reader.getStreamMetadata();
		if (metadata != null) {
			IIOMetadataNode globalRoot = (IIOMetadataNode) metadata.getAsTree(metadata.getNativeMetadataFormatName());

			NodeList globalScreenDescriptor = globalRoot.getElementsByTagName("LogicalScreenDescriptor");

			if (globalScreenDescriptor != null && globalScreenDescriptor.getLength() > 0) {
				IIOMetadataNode screenDescriptor = (IIOMetadataNode) globalScreenDescriptor.item(0);

				if (screenDescriptor != null) {
					width = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenWidth"));
					height = Integer.parseInt(screenDescriptor.getAttribute("logicalScreenHeight"));
				}
			}
		}

		BufferedImage master = null;
		Graphics2D masterGraphics = null;

		for (int frameIndex = 0;; frameIndex++) {
			BufferedImage image;
			try {
				image = reader.read(frameIndex);
			} catch (IndexOutOfBoundsException io) {
				break;
			}

			if (width == -1 || height == -1) {
				width = image.getWidth();
				height = image.getHeight();
			}

			IIOMetadataNode root = (IIOMetadataNode) reader.getImageMetadata(frameIndex).getAsTree("javax_imageio_gif_image_1.0");
			IIOMetadataNode gce = (IIOMetadataNode) root.getElementsByTagName("GraphicControlExtension").item(0);
			int delay = Integer.valueOf(gce.getAttribute("delayTime"));
			String disposal = gce.getAttribute("disposalMethod");

			int x = 0;
			int y = 0;

			if (master == null) {
				master = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				masterGraphics = master.createGraphics();
				masterGraphics.setBackground(new Color(0, 0, 0, 0));
			} else {
				NodeList children = root.getChildNodes();
				for (int nodeIndex = 0; nodeIndex < children.getLength(); nodeIndex++) {
					Node nodeItem = children.item(nodeIndex);
					if (nodeItem.getNodeName().equals("ImageDescriptor")) {
						NamedNodeMap map = nodeItem.getAttributes();
						x = Integer.valueOf(map.getNamedItem("imageLeftPosition").getNodeValue());
						y = Integer.valueOf(map.getNamedItem("imageTopPosition").getNodeValue());
					}
				}
			}
			masterGraphics.drawImage(image, x, y, null);

			BufferedImage copy = new BufferedImage(master.getColorModel(), master.copyData(null), master.isAlphaPremultiplied(), null);
			frames.add(new ImageFrame(copy, delay, disposal));

			if (disposal.equals("restoreToPrevious")) {
				BufferedImage from = null;
				for (int i = frameIndex - 1; i >= 0; i--) {
					if (!frames.get(i).getDisposal().equals("restoreToPrevious") || frameIndex == 0) {
						from = frames.get(i).getImage();
						break;
					}
				}

				master = new BufferedImage(from.getColorModel(), from.copyData(null), from.isAlphaPremultiplied(), null);
				masterGraphics = master.createGraphics();
				masterGraphics.setBackground(new Color(0, 0, 0, 0));
			} else if (disposal.equals("restoreToBackgroundColor")) {
				masterGraphics.clearRect(x, y, image.getWidth(), image.getHeight());
			}
		}
		reader.dispose();

		return frames.toArray(new ImageFrame[frames.size()]);
	}
	/**
	 * @param image
	 * @return
	 */
	private static int[][][] getPixels(BufferedImage image) {
		int[][][] result = new int[image.getHeight()][image.getWidth()][4];
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				Color c = new Color(image.getRGB(x, y), true);
				result[y][x][0] = c.getRed();
				result[y][x][1] = c.getGreen();
				result[y][x][2] = c.getBlue();
				result[y][x][3] = c.getAlpha();
			}
		}
		return result;
	}
}
