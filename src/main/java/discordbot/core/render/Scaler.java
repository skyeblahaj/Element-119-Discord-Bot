package discordbot.core.render;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class Scaler {

	private BufferedImage render;
	private Image img;
	
	public Scaler(BufferedImage img, BufferedImage ratio, float xMult, float yMult) {
		float x = ratio.getWidth() * xMult;
		float y = ratio.getHeight() * yMult;
		this.img = img.getScaledInstance((int) x, (int) y, Image.SCALE_SMOOTH);
		this.render = new BufferedImage((int) x, (int) y, BufferedImage.TYPE_INT_ARGB);
	}
	
	public BufferedImage scale() {
		Graphics2D renderer = this.render.createGraphics();
		renderer.drawImage(this.img, 0, 0, null);
		renderer.dispose();
		return this.render;
	}
}