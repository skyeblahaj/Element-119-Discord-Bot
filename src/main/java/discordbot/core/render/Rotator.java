package discordbot.core.render;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Rotator {

	private BufferedImage img;
	
	public Rotator(BufferedImage img) {
		this.img = img;
	}
	
	public BufferedImage rotate(int deg) {
		BufferedImage ret = new BufferedImage(this.img.getWidth(), this.img.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = ret.createGraphics();
		AffineTransform rotatedInstance = AffineTransform.getRotateInstance(Math.toRadians(deg), this.img.getWidth() / 2, this.img.getHeight() / 2);
		g.setTransform(rotatedInstance);
		g.drawImage(this.img, 0, 0, null);
		
		return ret;
	}
	
}