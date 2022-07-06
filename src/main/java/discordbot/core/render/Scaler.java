package discordbot.core.render;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class Scaler {

	private BufferedImage render;
	private Image img;
	
	public Scaler(BufferedImage img, BufferedImage ratio, float xMult, float yMult) {
		this(img, (int) (ratio.getWidth() * xMult), (int) (ratio.getHeight() * yMult));
	}
	
	public Scaler(BufferedImage img, int x, int y) {
		this.img = img.getScaledInstance(x, y, Image.SCALE_SMOOTH);
		this.render = new BufferedImage(x, y, BufferedImage.TYPE_INT_ARGB);
	}
	
	public BufferedImage scale() {
		Graphics2D renderer = this.render.createGraphics();
		renderer.drawImage(this.img, 0, 0, null);
		renderer.dispose();
		return this.render;
	}
}