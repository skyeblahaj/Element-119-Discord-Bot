package discordbot.core.render;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import discordbot.utils.Functions;

public class TextDrawer {

	private BufferedImage img;
	private Color color;
	private Font font;
	
	public TextDrawer(BufferedImage img, Color color, Font font) throws IllegalMediaException {
		this.img = img;
		if (color == null) {
			throw new IllegalMediaException("Color is null.");
		}
		this.color = color;
		if (font == null) {
			throw new IllegalMediaException("Font is null.");
		}
		this.font = font;
	}
	
	public TextDrawer(BufferedImage img, String color, String font, int fontSize) throws IllegalMediaException {
		this(img, Functions.Rendering.findColor(color), Functions.Rendering.findFont(font + "-" + fontSize));
	}
	
	public BufferedImage draw(String string, int x, int y) {
		Graphics2D g = this.img.createGraphics();
		g.setColor(this.color);
		g.setFont(this.font);
		g.drawString(string, x, y);
		
		g.dispose();
		
		return this.img;
	}
	
}