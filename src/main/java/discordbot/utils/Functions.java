package discordbot.utils;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;

public class Functions {

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
		
		public static void addReaction(Message msg, Emote e) {
			msg.addReaction(e).complete();
		}
		
		public static void sendEmbeded(MessageChannel channel, EmbedBuilder b) {
			channel.sendMessageEmbeds(b.build()).queue();
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
		
	}
}