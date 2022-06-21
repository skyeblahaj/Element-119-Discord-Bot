package discordbot.core.command;

import java.awt.Color;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import discordbot.Element119;
import discordbot.log.Logging;
import discordbot.utils.Functions;
import discordbot.utils.Info;
import discordbot.utils.RegistryBus;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.User;

public class CommandRegistry {
	
	public static final List<Command> COMMANDS = new ArrayList<>();
	
	private static Command register(String name, CommandAction action) {
		Command cmd = new Command(name, action);
		COMMANDS.add(cmd);
		return cmd;
	}
	
//////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static final Command INFORMATION = register("info", event -> {
		Functions.Messages.sendEmbeded(event.getChannel(),
				Functions.Messages.buildEmbed("Bot Information",
						new Color(0x42f598),
						new Field("Owner:", Info.OWNER_TAG, false),
						new Field("Language:", "Java", false),
						new Field("GitHub:", "https://github.com/skyeblahaj/Element-119-Discord-Bot", false)).setThumbnail(Element119.mainJDA.getSelfUser().getAvatarUrl()));
	});
	
	public static final Command HELP = register("help", event -> {
		MessageChannel c = event.getChannel();
		switch (Functions.RANDOM.nextInt(4)) {
		case 0 : Functions.Messages.sendMessage(c, "the walls are filled with j"); break;
		case 1 : Functions.Messages.sendMessage(c, "hello " + event.getAuthor().getAsTag()); break;
		case 2 : Functions.Messages.sendMessage(c, "مرحبا غروه الكرة لوغان"); break;
		case 3 : Functions.Messages.sendMessage(c, "burger credit card"); break;
		}
	});
	
	public static final Command COMMAND_LIST = register("commands", event -> {
		String list = "";
		for (Command c : COMMANDS) {
			list += "\n" + c.getName();
		}
		Functions.Messages.sendMessage(event.getChannel(), "```--Command List--" + list + "```");
	});
	
	public static final Command AVATAR = register("avatar", event -> {
		String[] args = Functions.Messages.multiArgs(event.getMessage());
		if (args.length < 2) {
			Functions.Messages.sendEmbeded(event.getChannel(),
					Functions.Messages.buildEmbed(event.getMember().getNickname() + "'s Avatar",
					new Color(0xffffff),
					event.getMember().getEffectiveAvatarUrl()));
		} else if (Functions.Messages.isPinging(args[1])) {
			User pingedUser = event.getMessage().getMentionedUsers().get(0);
			Functions.Messages.sendEmbeded(event.getChannel(),
					Functions.Messages.buildEmbed(pingedUser.getName() + "'s Avatar",
					new Color(0xffffff),
					pingedUser.getEffectiveAvatarUrl()));
		} else if (args[1].length() > 0) {
			try {
				User pingedUser = event.getJDA().getUsersByName(args[1], true).get(0);
				Functions.Messages.sendEmbeded(event.getChannel(),
						Functions.Messages.buildEmbed(pingedUser.getName() + "'s Avatar",
						new Color(0xffffff),
						pingedUser.getEffectiveAvatarUrl()));
			} catch (IndexOutOfBoundsException e) {Functions.Messages.sendEmbeded(event.getChannel(), Functions.Messages.errorEmbed(event.getMessage(), "Could not grab avatar."));}
			
		} else Functions.Messages.sendEmbeded(event.getChannel(), Functions.Messages.errorEmbed(event.getMessage(), "Could not grab avatar."));
	});
	
	public static final Command TTS = register("tts", event -> {
		String[] args = Functions.Messages.multiArgs(event.getMessage());
		new Thread(() -> {
			try {
				
				if (args.length < 3) Functions.Messages.sendEmbeded(event.getChannel(),
						Functions.Messages.errorEmbed(event.getMessage(), "Not enough parameters."));
				else {
					CloseableHttpClient client = HttpClients.createDefault();
					HttpPost httpPost = new HttpPost("http://api.15.ai/app/getAudioFile5");
					
					//exceptions
					args[1] = (String) Functions.Utils.capitalize(args[1]);
					if (args[1].equalsIgnoreCase("spongebob")) args[1] = "SpongeBob SquarePants";
					//
					String input = "";
					for (int i = 2; i < args.length; i++) {
						input += args[i];
					}
					
					httpPost.setEntity(new StringEntity(String.format("{\"character\":\"%s\",\"emotion\":\"Contextual\",\"text\":\"%s\"}", args[1], input)));
					httpPost.setHeader("Accept", "application/json");
				    httpPost.setHeader("Content-type", "application/json");
				    File rawFile = new File("src/main/resources/cache/ttsRaw.wav");
				    CloseableHttpResponse resp = client.execute(httpPost);
				    JsonObject obj = Json.createReader(resp.getEntity().getContent()).readObject();
				    String wavFile = "https://cdn.15.ai/audio/" + obj.getJsonArray("wavNames").getString(0);
				    FileUtils.copyURLToFile(new URL(wavFile), rawFile);
				    AudioFormat format = new AudioFormat(44100, 16, 1, true, true);
					AudioInputStream in = AudioSystem.getAudioInputStream(rawFile);
					AudioInputStream convert = AudioSystem.getAudioInputStream(format, in);
					AudioSystem.write(convert, AudioFileFormat.Type.WAVE, new File("src/main/resources/cache/tts16Bit.wav"));
					in.close();
					convert.close();
					
					//TODO continue this code when lavaplayer is implemented
					
				}
			} catch (Exception e) {}
		}).start();
		
	});
	
//////////////////////////////////////////////////////////////////////////////////////////////////
	@RegistryBus
	public static void registerAll() {
		Element119.mainJDA.addEventListener(new Logging());
		for (Command cmd : COMMANDS) {
			cmd.register();
			System.out.println(cmd.getName() + " command is registered.");
		}
	}

}
