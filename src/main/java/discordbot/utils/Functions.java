package discordbot.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.Random;

import javax.json.Json;
import javax.json.JsonObject;

import org.apache.http.client.methods.CloseableHttpResponse;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;

import discordbot.Element119;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

@BusPassenger
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
	
	@BusPassenger
	public static class OAuth {
		private OAuth() {}
		
		private static YouTube ytInstance;
		
		@RegistryBus
		public static void buildYoutube() {
			YouTube app = null;
			try {
				app = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(),
											JacksonFactory.getDefaultInstance(),
											(req -> req.setInterceptor(
													inter -> inter.getUrl().set("key",
														Functions.Utils.readToken(new File("src/main/resources/private/google_token.prv"))))))
						.setApplicationName(Element119.class.getSimpleName() + "-Discord-Bot-Google-API")
						.build();
			} catch(Exception e) {e.printStackTrace();}
			ytInstance = app;
		}
		
		public static YouTube youtubeInstance() {
			return ytInstance;
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
		
		public static void sendMessageReply(Message message, String s) {
			message.reply(s).queue();;
		}
		
		public static void sendFile(MessageChannel channel, File f) {
			channel.sendFile(f).queue();
		}
		
		public static void sendFileReply(Message message, File f) {
			MessageAction perform = message.reply(f);
			try {
				perform.queue();
			} catch (Exception e) {
				sendEmbededReply(message,
						errorEmbed(message, "File size is too large."));
			}
		}
		
		public static void editMessage(Message message, String s) {
			message.editMessage(s).queue();
		}
		
		public static void editMessage(Message message, Message newMessage) {
			message.editMessage(newMessage).queue();
		}
		
		public static void editEmbeded(MessageChannel channel, EmbedBuilder e) {
			channel.editMessageEmbedsById(channel.getLatestMessageId(), e.build()).queue();
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
		
		public static void sendEmbededReply(Message message, EmbedBuilder b) {
			message.replyEmbeds(b.build()).queue();
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
		
		public static EmbedBuilder errorEmbed(Message m, String msg, Exception e) {
			return errorEmbed(m, msg).addField("Stacktrace:", e.toString(), false);
		}
		
	}
	
	public static class Debug {
		
		public static <T> void printArray(T[] in) {
			String out = "";
			for (T t : in) {
				out += t.toString() + '\t';
			}
			System.out.println(out);
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
		
		public static Optional<Color> findColor(String search) {
			Color out;
			try {
				out = Color.decode(search);
			} catch (NumberFormatException e) {
				switch (search.toLowerCase()) {
					case "red" : out = Color.RED;
					case "crimson" : out = new Color(0x852424);
					case "scarlet" : out = new Color(0xf04526);
					case "orange" : out = Color.ORANGE;
					case "gold" : out = new Color(0xbfa10a);
					case "yellow" : out = Color.YELLOW;
					case "olive" : out = new Color(0x615c07);
					case "lime" : out = new Color(0x07f517);
					case "green" : out = Color.GREEN;
					case "cyan" : out = Color.CYAN;
					case "aqua" : out = new Color(0x26e6f0);
					case "blue" : out = Color.BLUE;
					case "indigo" : out = new Color(0x5f26f0);
					case "purple" : out = new Color(0x7e26f0);
					case "violet" : out = new Color(0xc126f0);
					case "magenta" : out = Color.MAGENTA;
					case "pink" : out = Color.PINK;
					case "white" : out = Color.WHITE;
					case "black" : out = Color.BLACK;
					case "gray" : out = Color.GRAY;
					case "lgray" : out = Color.LIGHT_GRAY;
					case "dgray" : out = Color.DARK_GRAY;
					case "brown" : out = new Color(0x613407);
					default : out = null;
				}
			}
			return Optional.ofNullable(out);
		}
		
		public static Font findFont(String search) {
			Font f = Font.decode(search);
			if (f.equals(new Font(Font.DIALOG, Font.PLAIN, 12))) {
				return null;
			}
			return f;
		}
	}
	
	public static class Network { //http utils
		
		public static JsonObject getJSON(CloseableHttpResponse resp) throws RuntimeException, IOException {
			return Json.createReader(resp.getEntity().getContent()).readObject();
		}
		
	}
}