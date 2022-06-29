package discordbot.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

public class Functions{

	//utils for memory management
	public static final Random RANDOM = new Random();
	//
	
	private Functions() {}
	
	////
	
	public static class Utils { //ordinary utilities
		private Utils() {}
		
		public static String readToken(File toRead) {
			String output = null;
			try (BufferedReader br = new BufferedReader(new FileReader(toRead))) {
				while (br.ready()) {
					output = br.readLine();
				}
				br.close();
			} catch (IOException e) {return null;}
			
			return output;
		}
		
		public static void writeToFile(File f, String s) {
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(f));
				bw.write(s);
				bw.close();
			} catch (IOException e) {return;}
		}
		
		public static CharSequence capitalize(String toCap) {
			char $1 = toCap.toUpperCase().charAt(0);
			String rest = toCap.substring(1).toLowerCase();
			return $1 + rest;
		}
		
		public static boolean startsWithIgnoreCase(String org, String key) {
			return org.toLowerCase().startsWith(key.toLowerCase());
		}
	}
	
	public static class OAuth {
		private OAuth() {}
		
		public static YouTube buildYoutube() {
			final YouTube yt;
			YouTube app = null;
			try {
				app = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(),
											JacksonFactory.getDefaultInstance(),
											(req -> req.setInterceptor(
													inter -> inter.getUrl().set("key",
														Functions.Utils.readToken(new File("src/main/resources/private/google_token.prv")))))).build();
			} catch(Exception e) {e.printStackTrace();}
			yt = app;
			return yt;
		}
	}
	
	public static class Messages { //message utilities
		private Messages() {}
		
		public static String[] multiArgs(Message m) {
			return m.getContentRaw().split("\\s+");
		}
		
		public static void sendMessage(MessageChannel channel, String s) {
			channel.sendMessage(s).queue();
		}
		
		public static void sendFile(MessageChannel channel, File f) {
			channel.sendFile(f).queue();
		}
		
		public static void sendFileReply(Message message, File f) {
			message.reply(f).queue();
		}
		
		public static void addReaction(Message msg, Emote e) {
			msg.addReaction(e).complete();
		}
		
		public static void sendEmbeded(MessageChannel channel, EmbedBuilder b) {
			channel.sendMessageEmbeds(b.build()).queue();
		}
		
		public static void sendEmbeded(MessageChannel channel, EmbedBuilder b, File file) {
			channel.sendFile(file).setEmbeds(b.build()).queue();
		}
		
		public static EmbedBuilder buildEmbed(String title, Color color, Field... fields) {
			EmbedBuilder building = new EmbedBuilder();
			building.setTitle(title);
			building.setColor(color);
			for (Field field : fields) {
				building.addField(field);
			}
			return building;
		}
		
		public static EmbedBuilder buildEmbed(String title, Color color, String imgURL) {
			EmbedBuilder building = new EmbedBuilder();
			building.setTitle(title);
			building.setColor(color);
			building.setImage(imgURL);
			return building;
		}
		
		public static boolean isPinging(String msg) {
			return msg.startsWith("<@") && msg.endsWith(">");
		}
		
		public static EmbedBuilder errorEmbed(Message m, String msg) {
			EmbedBuilder building = new EmbedBuilder();
			building.setTitle("Error");
			building.setColor(0xff0000);
			try {
				building.addField(new Field("Error at: \"" + m.getContentRaw().substring(0, m.getContentRaw().indexOf(' ')) + "\"",
			  			msg,
			  			false));
			} catch (StringIndexOutOfBoundsException e) { //if only one parameter is typed
				building.addField(new Field("Error at: \"" + m.getContentRaw() + "\"", msg, false));
			}
			
			return building;
		}
		
	}
	
	public static class Rendering { //media utilities
		private Rendering() {}
		
		public static BufferedImage resizeCanvas(BufferedImage img, int x, int y, boolean scale) {
			BufferedImage out = new BufferedImage(x, y, img.getType());
			Graphics2D renderer = out.createGraphics();
			if (scale) {
				Image scaled = img.getScaledInstance(x, y, Image.SCALE_SMOOTH);
				renderer.drawImage(scaled, 0, 0, null);
			} else {
				renderer.drawImage(img, 0, y - img.getHeight(), null);
			}
			return out;
		}
	}
}