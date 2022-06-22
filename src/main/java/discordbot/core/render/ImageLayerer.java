package discordbot.core.render;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;

import discordbot.core.math.Point;

public class ImageLayerer {

	private RenderedImage canvas;
	private Graphics2D renderer;
	private BufferedImage[] img;
	private Point dims;
	
	public ImageLayerer(BufferedImage... img) {
		if (img.length < 2) throw new IllegalArgumentException("Layering images requires at least two images.");
		
		this.img = img;
		
		int x = 0, y = 0;
		
		for (int i = 1; i < img.length; i++) {
			x = (img[i].getWidth() > img[i - 1].getWidth()) ? img[i].getWidth() : img[i - 1].getWidth();
			y = (img[i].getHeight() > img[i - 1].getHeight()) ? img[i].getHeight() : img[i - 1].getHeight();
		}
		this.dims = new Point(x,y);
		Image canvas = new BufferedImage(x, y, BufferedImage.TYPE_INT_ARGB);
		this.canvas = (RenderedImage) canvas;
		this.renderer = (Graphics2D) canvas.getGraphics();
		
	}
	
	public void render(int index, int x, int y) {
		this.renderer.drawImage(this.img[index], x, y, null);
	}
	
	public void complete(String output) {
		try {
			ImageIO.write(this.canvas, FilenameUtils.getExtension(output), new File(output));
		} catch (IOException e) {
			System.err.println(ImageLayerer.class.getSimpleName() + " threw an IOException.");
		}
		this.renderer.dispose();
	}	
	
	public Point getMaxDimensions() {
		return dims;
	}
}