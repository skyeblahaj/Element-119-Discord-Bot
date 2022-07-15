package discordbot.core.render;

import java.awt.image.BufferedImage;

public class Cropper {

	private BufferedImage img;
	
	public Cropper(BufferedImage img) {
		this.img = img;
	}
	
	public BufferedImage crop(int top, int right, int bottom, int left) {
		return this.img.getSubimage(left, top, this.img.getWidth() - right - left, this.img.getHeight() - bottom - top);
	}
	
}