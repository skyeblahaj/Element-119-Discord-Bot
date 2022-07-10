package discordbot.core.command;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.io.FileUtils;
import org.apache.hc.core5.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicHeader;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;

import discordbot.Element119;
import discordbot.core.audio.GuildMusicManager;
import discordbot.core.audio.PlayerManager;
import discordbot.core.audio.spotify.LinkConverter;
import discordbot.core.network.GetRequester;
import discordbot.core.network.PostRequester;
import discordbot.core.render.Converter;
import discordbot.core.render.IllegalMediaFormatException;
import discordbot.core.render.ImageLayerer;
import discordbot.core.render.Scaler;
import discordbot.inter_face.custom.CustomGuildFeatures;
import discordbot.inter_face.custom.GuildController;
import discordbot.inter_face.debug.ManualControl;
import discordbot.utils.BusPassenger;
import discordbot.utils.Functions;
import discordbot.utils.Info;
import discordbot.utils.RegistryBus;
import discordbot.utils.function.SameThreadRunnable;
import discordbot.utils.media.AudioTypes;
import discordbot.utils.media.ImageTypes;
import discordbot.utils.media.MediaType;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.managers.AudioManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

@BusPassenger
public class CommandRegistry {
	
	private CommandRegistry() {}
	
	public static final List<Command> COMMANDS = new ArrayList<>();
	public static final List<OwnerCommand> OWNER_COMMANDS = new ArrayList<>();
	public static final List<PermissionCommand> PERMISSION_COMMANDS = new ArrayList<>();
	
	public static final List<List<? extends Command>> ALL_COMMANDS = List.of(COMMANDS, OWNER_COMMANDS, PERMISSION_COMMANDS);
	
	private static Command register(String name, CommandAction action, String help) {
		Command cmd = new Command(name, action, help);
		COMMANDS.add(cmd);
		return cmd;
	}
	
	private static OwnerCommand registerOwner(String name, CommandAction action, String help) {
		OwnerCommand cmd = new OwnerCommand(name, action, help);
		OWNER_COMMANDS.add(cmd);
		return cmd;
	}
	
	private static PermissionCommand registerPermission(String name, CommandAction action, String help, Permission... perms) {
		PermissionCommand cmd = new PermissionCommand(name, action, help, perms);
		PERMISSION_COMMANDS.add(cmd);
		return cmd;
	}
	
	private static Command register(String name, CommandAction action) {
		return register(name, action, null);
	}
	
	private static OwnerCommand registerOwner(String name, CommandAction action) {
		return registerOwner(name, action, null);
	}
	
	private static PermissionCommand registerPermission(String name, CommandAction action, Permission... perms) {
		return registerPermission(name, action, null, perms);
	}
	
//////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static final Command CMD_HELP = register("cmdhelp", event -> {
		String[] args = Functions.Messages.multiArgs(event.getMessage());
		if (args.length < 2) {
			Functions.Messages.sendEmbeded(event.getChannel(), 
					Functions.Messages.errorEmbed(event.getMessage(), "Include the string of another command."));
		} else {
			Command cmd = null;
			for (List<? extends Command> l : ALL_COMMANDS) {
				for (Command c : l) {
					if (args[1].equalsIgnoreCase(c.getName())) {
						cmd = c;
					}
				}
			}
			if (cmd instanceof OwnerCommand) {
				Functions.Messages.sendEmbeded(event.getChannel(), 
						Functions.Messages.buildEmbed("Command Help", new Color(0x00ff00),
								new Field("Notice:", "Developer Only", false),
								new Field((String) Functions.Utils.capitalize(cmd.getName()), cmd.getHelp() == null ? "No help message is provided." : cmd.getHelp(), false)));
			} else if (cmd instanceof PermissionCommand) {
				String perms = "";
				for (Permission p : ((PermissionCommand) cmd).getPerms()) {
					perms += p.getName() + "\n";
				}
				Functions.Messages.sendEmbeded(event.getChannel(), 
						Functions.Messages.buildEmbed("Command Help", new Color(0x00ff00),
								new Field("Notice:", "Needs:\n" + perms, false),
								new Field((String) Functions.Utils.capitalize(cmd.getName()), cmd.getHelp() == null ? "No help message is provided." : cmd.getHelp(), false)));
			} else if (cmd == null) {
				Functions.Messages.sendEmbeded(event.getChannel(), 
						Functions.Messages.errorEmbed(event.getMessage(), "Cannot find requested command."));
			} else {
				Functions.Messages.sendEmbeded(event.getChannel(), 
						Functions.Messages.buildEmbed("Command Help", new Color(0x00ff00),
								new Field((String) Functions.Utils.capitalize(cmd.getName()), cmd.getHelp() == null ? "No help message is provided." : cmd.getHelp(), false)));
			}
		}
	}, "You're already on the help page. Stupid.");
	
	public static final Command INFORMATION = register("info", event -> {
		Functions.Messages.sendEmbeded(event.getChannel(),
				Functions.Messages.buildEmbed("Bot Information",
						new Color(0x42f598),
						new Field("Owner:", Info.OWNER_TAG, false),
						new Field("Language:", "Java", false),
						new Field("GitHub:", "https://github.com/skyeblahaj/Element-119-Discord-Bot", false)).setThumbnail(Element119.mainJDA.getSelfUser().getAvatarUrl()));
	}, "Returns generic information about the bot.");
	
	public static final Command HELP = register("help", event -> {
		MessageChannel c = event.getChannel();
		switch (Functions.RANDOM.nextInt(4)) {
		case 0 : Functions.Messages.sendMessage(c, "the walls are filled with j"); break;
		case 1 : Functions.Messages.sendMessage(c, "hello " + event.getAuthor().getAsTag()); break;
		case 2 : Functions.Messages.sendMessage(c, "مرحبا غروه الكرة لوغان"); break;
		case 3 : Functions.Messages.sendMessage(c, "burger credit card"); break;
		}
	}, "Not what you'd expect from a help command. Why don't you try it out?");
	
	public static final Command COMMAND_LIST = register("commands", event -> {
		String list = "";
		for (List<? extends Command> l : ALL_COMMANDS) {
			for (Command c : l) {
				if (c instanceof OwnerCommand) {
					list += "\n(Owner Only) " + c.getName();
				} else if (c instanceof PermissionCommand) {
					String permsNeeded = "";
					for (Permission p : ((PermissionCommand) c).getPerms()) {
						permsNeeded += " (" + p.getName() + ") ";
					}
					list += "\nRequires:" + permsNeeded + c.getName();
				} else {
					list += "\n" + c.getName();
				}
			}
		}
		Functions.Messages.sendMessage(event.getChannel(), "```--Command List--" + list + "```");
	}, "Returns all registered commands. Note that this command is automated as development progress continues so some commands may not function correctly.");
	
	public static final Command AVATAR = register("avatar", event -> {
		String[] args = Functions.Messages.multiArgs(event.getMessage());
		if (args.length < 2) {
			Functions.Messages.sendEmbeded(event.getChannel(),
					Functions.Messages.buildEmbed(event.getMember().getNickname() + "'s Avatar",
					new Color(0xffffff),
					event.getMember().getEffectiveAvatarUrl()));
		} else if (Functions.Messages.isPinging(args[1])) {
			Member pingedMember = event.getMessage().getMentionedMembers().get(0);
			Functions.Messages.sendEmbeded(event.getChannel(),
					Functions.Messages.buildEmbed(pingedMember.getEffectiveName() + "'s Avatar",
					new Color(0xffffff),
					pingedMember.getEffectiveAvatarUrl()));
		} else if (args[1].length() > 0) {
			String search = "";
			for (int i = 1; i < args.length; i++) {
				search += args[i] + " ";
			}
			search = search.trim();
			try {
				Member pingedMember = event.getGuild().getMembersByEffectiveName(search, true).get(0);
				Functions.Messages.sendEmbeded(event.getChannel(),
						Functions.Messages.buildEmbed(pingedMember.getEffectiveName() + "'s Avatar",
						new Color(0xffffff),
						pingedMember.getEffectiveAvatarUrl()));
			} catch (IndexOutOfBoundsException e) {Functions.Messages.sendEmbeded(event.getChannel(), Functions.Messages.errorEmbed(event.getMessage(), "Could not grab avatar."));}
			
		} else Functions.Messages.sendEmbeded(event.getChannel(), Functions.Messages.errorEmbed(event.getMessage(), "Could not grab avatar."));
	}, "Grabs the avatar of the user either searched my nickname, pinged, or if nothing specified, the user theirself.");
	
	public static final Command TTS = register("tts", event -> {
		String[] args = Functions.Messages.multiArgs(event.getMessage());
		new Thread(() -> {
			try {
				
				if (args.length < 3) Functions.Messages.sendEmbeded(event.getChannel(),
						Functions.Messages.errorEmbed(event.getMessage(), "Not enough parameters."));
				else {
					PostRequester httpTool = new PostRequester("http://api.15.ai/app/getAudioFile5");
					
					//exceptions
					args[1] = (String) Functions.Utils.capitalize(args[1]);
					if (args[1].equalsIgnoreCase("spongebob")) args[1] = "SpongeBob SquarePants";
					//
					
					String input = "";
					for (int i = 2; i < args.length; i++) {
						input += args[i];
					}
					
					CloseableHttpResponse resp = httpTool.post(String.format("{\"character\":\"%s\",\"emotion\":\"Contextual\",\"text\":\"%s\"}", args[1], input), 
							new BasicHeader("accept", "application/json"),
							new BasicHeader("content-type", "application/json"));
					JsonObject obj = Functions.Network.getJSON(resp);
					httpTool.close();
					
				    File rawFile = new File("src/main/resources/cache/ttsRaw.wav");
				    File bit16 = new File("src/main/resources/cache/tts16Bit.wav");
				    String wavFile = "https://cdn.15.ai/audio/" + obj.getJsonArray("wavNames").getString(0);
				    FileUtils.copyURLToFile(new URL(wavFile), rawFile);
				    
				    AudioFormat format = new AudioFormat(44100, 16, 1, true, true);
				    Converter convertTool = new Converter(rawFile);
				    
				    convertTool.convert(AudioTypes.WAV, format, bit16);
					
					Functions.Messages.sendFileReply(event.getMessage(), bit16);
					
					// play in vc
					
					if (event.getMember().getVoiceState().inAudioChannel()) {
						AudioManager manager = event.getGuild().getAudioManager();
						if (!manager.isConnected()) manager.openAudioConnection(event.getMember().getVoiceState().getChannel());
						PlayerManager.getInstance().load_play(event, bit16.getPath());
					}
				}
			} catch (Exception e) {}
		}).start();
	}, "Accesses an external AI generation website and returns the text entered by the user spoken by the character specified.");
	
	public static final Command QUOTE = register("quote", event -> {
		try {
			Attachment attachment = event.getMessage().getAttachments().get(0);
			File attachmentCached = new File("src/main/resources/cache/quoteIn." + attachment.getFileExtension());
			FileUtils.copyURLToFile(new URL(attachment.getProxyUrl()), attachmentCached);
			
			BufferedImage biAttachment = ImageIO.read(attachmentCached);
			BufferedImage bubble = ImageIO.read(new File("src/main/resources/bubble.png"));
			
			Scaler scalerTool = new Scaler(bubble, biAttachment, 1f, 0.33f);
			ImageLayerer renderTool = new ImageLayerer(biAttachment, scalerTool.scale());
			
			renderTool.render(0, 0, 0);
			renderTool.render(1, 0, 0);
			String attachmentOutCached = "src/main/resources/cache/quoteOut.png";
			renderTool.complete(attachmentOutCached);
			
			Functions.Messages.sendFile(event.getChannel(), new File(attachmentOutCached));
			
		} catch (IOException e) {e.printStackTrace();}
	}, "Renders a speech bubble the exact hex color of Discord's dark mode on top of the image provided by the user.");
	
	public static final Command PLAY = register("play", event -> {
		String[] args = Functions.Messages.multiArgs(event.getMessage());
		AudioManager manager = event.getGuild().getAudioManager();
		if (args.length < 2) Functions.Messages.sendEmbeded(event.getChannel(),
				Functions.Messages.errorEmbed(event.getMessage(), "Not enough arguments."));
		else {
			if (!manager.isConnected()) {
				manager.openAudioConnection(event.getMember().getVoiceState().getChannel()); 
			}
			try {
				
				if (args[1].contains("youtu.be") || args[1].contains("youtube.com")) {

						PlayerManager.getInstance().load_play(event, args[1].replace("shorts/", "watch?v="));
						
				} else if (args[1].contains("spotify.com")) {
					List<String> songs = null;
					try {
						songs = new LinkConverter().convert(args[1]);
					} catch (ParseException | SpotifyWebApiException | IOException e) {
							e.printStackTrace();
					}
					YouTube yt = Functions.OAuth.buildYoutube();
					for (String song : songs) {
						List<SearchResult> results = yt.search().list("id,snippet").setQ(song).setMaxResults(4L)
								.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)").execute().getItems();
						if (!results.isEmpty()) {
							String vidID = results.get(0).getId().getVideoId();
							PlayerManager.getInstance().load_play(event, "https://www.youtube.com/watch?v=" + vidID);
							Functions.Messages.sendEmbeded(event.getChannel(),
									Functions.Messages.buildEmbed("Audio Player", new Color(0xf0f0f0),
											new Field("Queued:", PlayerManager.getInstance()
																	 .getMusicManager(event.getGuild())
																	 .getPlayer().getPlayingTrack()
																	 .getInfo().title, false)));
						} else Functions.Messages.sendEmbeded(event.getChannel(),
							Functions.Messages.errorEmbed(event.getMessage(), "Could not retrieve song via the Spotify API."));
					}
				} else {
					Functions.Messages.sendEmbeded(event.getChannel(),
							Functions.Messages.errorEmbed(event.getMessage(), "URL or file cannot be retrieved."));
					}
				} catch (IllegalArgumentException e) {
					Functions.Messages.sendEmbeded(event.getChannel(),
						Functions.Messages.errorEmbed(event.getMessage(), "User is not connected to a voice channel."));
			} catch (IOException e) {}
		}
	}, "Plays audio from different sources. Must be in a voice channel to use.");
	
	public static final Command TRACK = register("track", event -> {
		if (event.getGuild().getAudioManager().isConnected()) {
			final GuildMusicManager manager = PlayerManager.getInstance().getMusicManager(event.getGuild());
			final AudioPlayer player = manager.getPlayer();
			AudioTrackInfo info = null;
			try {
				info = player.getPlayingTrack().getInfo();
				
				Functions.Messages.sendEmbeded(event.getChannel(),
						Functions.Messages.buildEmbed("Track Info", Color.CYAN,
								new Field("Title:", info.title, false),
								new Field("Author:", info.author, false),
								new Field("Source:", info.uri, false),
								new Field("Time:", manager.getScheduler().getSecondsIn() + " / " + (info.length / 1000) + 's', false)));
			} catch (NullPointerException e) {
				Functions.Messages.sendEmbeded(event.getChannel(),
						Functions.Messages.errorEmbed(event.getMessage(), "No track is playing."));
			}		
		} else Functions.Messages.sendEmbeded(event.getChannel(),
					Functions.Messages.errorEmbed(event.getMessage(), "Bot is not connected to an audio channel."));
	}, "Returns the current playing song's information. This includes the title, author, source, and the elapsed time since it started.");
	
	public static final Command NEXT = register("next", event -> {
		if (event.getGuild().getAudioManager().isConnected() && event.getMember().getVoiceState().inAudioChannel()) {
			final GuildMusicManager manager = PlayerManager.getInstance().getMusicManager(event.getGuild());
			if (manager.getScheduler().getQueue() == null) {
				Functions.Messages.sendEmbeded(event.getChannel(),
						Functions.Messages.errorEmbed(event.getMessage(), "Queue is empty."));
			} else manager.getScheduler().nextTrack();
		} else Functions.Messages.sendEmbeded(event.getChannel(), 
					Functions.Messages.errorEmbed(event.getMessage(), "Not in voice channel."));
	}, "Skips to the next song within the queue.");
	
	public static final Command VC = register("vc", event -> {
		if (event.getMember().getVoiceState().inAudioChannel()) {
			AudioManager manager = event.getGuild().getAudioManager();
			if (manager.isConnected()) manager.closeAudioConnection();
			else manager.openAudioConnection(event.getMember().getVoiceState().getChannel());
		} else Functions.Messages.sendEmbeded(event.getChannel(),
					Functions.Messages.errorEmbed(event.getMessage(), "User is not in a voice channel."));
	}, "Toggles connection to the voice channel the user is connected to.");
	
	public static final Command STOP = register("stop", event -> {
		if (event.getMember().getVoiceState().inAudioChannel()) {
			final GuildMusicManager manager = PlayerManager.getInstance().getMusicManager(event.getGuild());
			AudioManager audio = event.getGuild().getAudioManager();
			if (audio.isConnected()) {
				manager.getScheduler().getPlayer().stopTrack();
				manager.getScheduler().getQueue().clear();
			} else {
				Functions.Messages.sendEmbeded(event.getChannel(),
						Functions.Messages.errorEmbed(event.getMessage(), "Bot is not connected to an audio channel."));
			}
		} else Functions.Messages.sendEmbeded(event.getChannel(),
				Functions.Messages.errorEmbed(event.getMessage(), "User is not in a voice channel."));
	}, "Stops the current track and clears the queue.");
	
	public static final Command CAPTION = register("caption", event -> {
		String[] args = Functions.Messages.multiArgs(event.getMessage());
		if (event.getMessage().getAttachments().size() <= 0) {
			Functions.Messages.sendEmbeded(event.getChannel(), 
					Functions.Messages.errorEmbed(event.getMessage(), "Must include an attachment."));
		} else {
			Attachment toCaption = event.getMessage().getAttachments().get(0);
			File cache = new File("src/main/resources/cache/caption_cache." + toCaption.getFileExtension()),
				 output = new File("src/main/resources/cache/caption." + toCaption.getFileExtension());
			try {
				FileUtils.copyURLToFile(new URL(toCaption.getProxyUrl()), cache);
				BufferedImage origin = ImageIO.read(cache);
				int x = origin.getWidth();
				int y = origin.getHeight();
				BufferedImage resized = Functions.Rendering.resizeCanvas(origin, x, y + y / 5, false);
				Graphics2D renderCaption = resized.createGraphics();
				renderCaption.fillRect(0, 0, x, y / 5);
				if (args.length > 1) {
					String draw = "";
					for (int i = 1; i < args.length; i++) {
						draw += args[i];
					}
					renderCaption.drawString(draw, x / 2, y / 10); //TODO get impact font displaying centered on caption
				}
				renderCaption.dispose();
				ImageIO.write(resized, toCaption.getFileExtension(), output);
			} catch (IOException e) {e.printStackTrace();}
			
			Functions.Messages.sendFile(event.getChannel(), output);
		}
	});
	
	public static final Command MC_SERVER = register("mc", event -> {
		String[] args = Functions.Messages.multiArgs(event.getMessage());
		try {
			GetRequester httpTool = new GetRequester("https://api.mcsrvstat.us/2/mc.omobasil.xyz");
			JsonObject obj = Functions.Network.getJSON(httpTool.get());
			
			httpTool.close();
			
			if (obj.getJsonObject("debug").getBoolean("ping")) {
				int playerCount = obj.getJsonObject("players").getInt("online");
				String players = "";
				if (playerCount > 0) {
					JsonArray list = obj.getJsonObject("players").getJsonArray("list");
					for (int i = 0; i < list.size(); i++) {
						players += list.getString(i) + "\n";
					}
				}
				Functions.Messages.sendEmbeded(event.getChannel(),
						Functions.Messages.buildEmbed("Minecraft Server", new Color(0x99652e),
								new Field("Host:", "mc.omobasil.xyz", true),
								new Field("Port:", obj.getInt("port") + "", true),
								new Field("Player Count:", playerCount + " / " +
														   obj.getJsonObject("players").getInt("max"), false),
								(playerCount > 0) ? new Field("Players:", players, false) : null, 
								new Field("How to Join:", "https://www.omobasil.xyz/servers/modmc/", false))
						.setThumbnail("https://cdn.discordapp.com/attachments/592579994408976384/991204974170218537/server-icon.png"));
				File motd = new File("src/main/resources/reusable/motd.html");
				
				if (args.length > 1) if (args[1].equalsIgnoreCase("motd")) {
					Functions.Utils.writeToFile(motd, "<!DOCTYPE html>\n<html>\n" + 
							obj.getJsonObject("motd")							 							   
							   .getJsonArray("html")
							   .getString(0) + "\n</html>");
					Functions.Messages.sendFile(event.getChannel(), motd);
				}
			} else {
				Functions.Messages.sendEmbeded(event.getChannel(), 
						Functions.Messages.buildEmbed("Minecraft Server", new Color(0xff0000),
								new Field("Error:", "Server Offline", false)));
			}
		} catch (Exception e) {e.printStackTrace();}
	}, "Returns the current information of the Minecraft server hosted by the bot owner. Write 'motd' as a parameter to access the MOTD message.");
	
	public static final Command CONVERT = register("convert", event -> {
		String[] args = Functions.Messages.multiArgs(event.getMessage());
		
		if (args.length < 2)
			Functions.Messages.sendEmbeded(event.getChannel(),
					Functions.Messages.errorEmbed(event.getMessage(), "Must include a file format."));
		else {
			new Thread(() -> {
				List<Attachment> attachments = event.getMessage().getAttachments();
				if (attachments.size() <= 0)
					Functions.Messages.sendEmbeded(event.getChannel(),
							Functions.Messages.errorEmbed(event.getMessage(), "Must include an attachment."));
				else {
					Attachment toConvert = attachments.get(0);
					if (toConvert.getSize() < 10485760) {
						File inventory = new File("src/main/resources/cache/convert_input." + toConvert.getFileExtension());
						toConvert.downloadToFile(inventory);

						try {
							Converter convertTool = new Converter(inventory);
							convertTool.convert(MediaType.findFormat(args[1]), null, null);
							Functions.Messages.sendFileReply(event.getMessage(), convertTool.getOutputFile());
						} catch (IllegalMediaFormatException e) {
							Functions.Messages.sendEmbeded(event.getChannel(), Functions.Messages.errorEmbed(
									event.getMessage(), "Format provided does not support the media type provided."));
						} catch (UnsupportedAudioFileException e) {
							Functions.Messages.sendEmbeded(event.getChannel(), Functions.Messages.errorEmbed(
									event.getMessage(), "Format provided is not supported."));
						} catch (IOException e) {}
					} else {
						Functions.Messages.sendEmbeded(event.getChannel(), 
								Functions.Messages.errorEmbed(event.getMessage(), "Please keep inventory files below 10 MB."));
					}
				}
			}).start();
		}
	}, "Converts video, audio, or images to different formats.");
	
	public static final Command SHOW_PERMISSIONS = register("showperms", event -> {
		String[] args = Functions.Messages.multiArgs(event.getMessage());
		String perms = "";
		if (args.length < 2) {
			for (Permission p : event.getMember().getPermissions()) {
				perms += "\n" + p.getName();
			}
			Functions.Messages.sendEmbeded(event.getChannel(),
					Functions.Messages.buildEmbed(event.getMember().getNickname() + "'s Permissions",
					new Color(0xffffff),
					new Field("List of Perms:", perms, false)));
		} else if (Functions.Messages.isPinging(args[1])) {
			Member pingedMember = event.getMessage().getMentionedMembers().get(0);
			for (Permission p : pingedMember.getPermissions()) {
				perms += "\n" + p.getName();
			}
			Functions.Messages.sendEmbeded(event.getChannel(),
					Functions.Messages.buildEmbed(pingedMember.getEffectiveName() + "'s Permissions",
					new Color(0xffffff),
					new Field("List of Perms:", perms, false)));
		} else if (args[1].length() > 0) {
			String search = "";
			for (int i = 1; i < args.length; i++) {
				search += args[i] + " ";
			}
			search = search.trim();
			try {
				Member pingedMember = event.getGuild().getMembersByEffectiveName(search, true).get(0);
				for (Permission p : pingedMember.getPermissions()) {
					perms += "\n" + p.getName();
				}
				Functions.Messages.sendEmbeded(event.getChannel(),
						Functions.Messages.buildEmbed(pingedMember.getEffectiveName() + "'s Permissions",
						new Color(0xffffff),
						new Field("List of Perms:", perms, false)));
			} catch (IndexOutOfBoundsException e) {Functions.Messages.sendEmbeded(event.getChannel(), Functions.Messages.errorEmbed(event.getMessage(), "Could not grab permissions."));}
			
		} else Functions.Messages.sendEmbeded(event.getChannel(), Functions.Messages.errorEmbed(event.getMessage(), "Could not grab permissions."));
	}, "Returns all permissions the user has, or user specified as a parameter.");
	
	public static final Command RANDOM = register("random", event -> {
		String output = "";
		int charAmount = Functions.RANDOM.nextInt(150) + 1;
		char[] data = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
		for (int i = 0; i < charAmount; i++) {
			output += (Functions.RANDOM.nextBoolean()) ? data[Functions.RANDOM.nextInt(data.length)] : Character.toUpperCase(data[Functions.RANDOM.nextInt(data.length)]);
		}
		Functions.Messages.sendMessage(event.getChannel(), output);
	}, "Generates a random alpha-numeric character sequence from a length of 1-150.");
	
	public static final Command CLICKBAIT = register("clickbait", event -> {
		String[] args = Functions.Messages.multiArgs(event.getMessage());
		
		int index = 0;
		boolean random = true;
		
		if (args.length > 1) {
			switch (Integer.parseInt(args[1])) {
				case 0 -> {
					random = false;
					break;
				}
				case 1 -> {
					index = 1;
					random = false;
					break;
				}
				case 2 -> {
					index = 2;
					random = false;
				}
				case 3 -> {
					index = 3;
					random = false;
				}
			}
		}
		List<Attachment> imageInput = event.getMessage().getAttachments();
		String output = "src/main/resources/cache/clickbait_output.png";
		
		final BufferedImage inImage;
		BufferedImage inTemp = null;
		final BufferedImage clickbait0;
		BufferedImage cl0Temp = null;
		final BufferedImage clickbait1;
		BufferedImage cl1Temp = null;
		final BufferedImage clickbait2;
		BufferedImage cl2Temp = null;
		final BufferedImage clickbait3;
		BufferedImage cl3Temp = null;
		
		if (imageInput.size() <= 0) {
			Functions.Messages.sendEmbeded(event.getChannel(), 
					Functions.Messages.errorEmbed(event.getMessage(), "Must include an image attachment."));
		} else if (!(MediaType.findFormat(imageInput.get(0).getFileExtension()) instanceof ImageTypes)) {
			Functions.Messages.sendEmbeded(event.getChannel(), 
					Functions.Messages.errorEmbed(event.getMessage(), "Attachment must be an image."));
		} else {
			
			File input = new File("src/main/resources/cache/clickbaitIn." + imageInput.get(0).getFileExtension());
			
			try {
				FileUtils.copyURLToFile(new URL(imageInput.get(0).getProxyUrl()), input);
			} catch (IOException e) {e.printStackTrace();}
			
			if (random) index = Functions.RANDOM.nextInt(4);
			
			try {
				inTemp = ImageIO.read(input);
				
				cl0Temp = ImageIO.read(new File("src/main/resources/clickbait.png"));
				cl1Temp = ImageIO.read(new File("src/main/resources/banana.jpg"));
				cl2Temp = ImageIO.read(new File("src/main/resources/scammer.png"));
				cl3Temp = ImageIO.read(new File("src/main/resources/osaka.png"));
				
			} catch (IOException e) {e.printStackTrace();}
			
			inImage = inTemp;
			clickbait0 = cl0Temp;
			clickbait1 = cl1Temp;
			clickbait2 = cl2Temp;
			clickbait3 = cl3Temp;
			
			SameThreadRunnable cr1tikal = () -> {
				Scaler scaleTool = new Scaler(inImage, 300, 150);
				ImageLayerer renderTool = new ImageLayerer(clickbait0, scaleTool.scale());
				renderTool.render(0, 0, 0);
				renderTool.render(1, 142, 242);
				
				renderTool.complete(output);
			};
			
			SameThreadRunnable banana = () -> {
				Scaler scaleTool = new Scaler(inImage, 335, 486);
				ImageLayerer renderTool = new ImageLayerer(clickbait1, scaleTool.scale());
				renderTool.render(0, 0, 0);
				renderTool.render(1, 131, 209);
				
				renderTool.complete(output);
			};
			
			SameThreadRunnable scammer = () -> {
				Scaler scaleTool = new Scaler(inImage, 345, 420);
				ImageLayerer renderTool = new ImageLayerer(clickbait2, scaleTool.scale());
				renderTool.render(0, 0, 0);
				renderTool.render(1, 27, 265);
				
				renderTool.complete(output);
			};
			
			SameThreadRunnable osaka = () -> {
				Scaler scaleTool = new Scaler(inImage, 208, 256);
				ImageLayerer renderTool = new ImageLayerer(clickbait3, scaleTool.scale());
				renderTool.render(0, 0, 0);
				renderTool.render(1, 943, 576);
				
				renderTool.complete(output);
			};
			
			switch (index) {
			case 0 -> {cr1tikal.run(); break;}
			case 1 -> {banana.run(); break;}
			case 2 -> {scammer.run(); break;}
			case 3 -> {osaka.run(); break;}
			}
			
			Functions.Messages.sendFileReply(event.getMessage(), new File(output));
		}
	}, "Renders the image proveded by the user over a predetermined image of an internet clickbait thumbnail. Specify an integer as a parameter to select a specific thumbnail.");
	
//////////////////////////////////////////////////////////////////////////////////////////////////

	public static final OwnerCommand BOT_SHUTDOWN = registerOwner("botshutdown", event -> {
		Functions.Messages.sendMessage(event.getChannel(), "Bot is shutting down...");
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {e.printStackTrace();}
		System.exit(0);
	}, "Shuts down the current instance of the bot.");
	
	public static final OwnerCommand SYSTEM_SHUTDOWN = registerOwner("systemshutdown", event -> {
		Functions.Messages.sendMessage(event.getChannel(), "System is shutting down...");
		try {
			Thread.sleep(1500);
			Runtime.getRuntime().exec("shutdown -s -t 0"); //win10 only
		} catch (IOException | InterruptedException e) {e.printStackTrace();}
	}, "Shuts down the computer the bot is hosted on.");
	
	public static final OwnerCommand MANUAL_CONTROL = registerOwner("takeover", event -> {
		ManualControl.commandToggle = false;
		new ManualControl(true, event);
	}, "Switches to manual mode, providing a control panel.");
	
//////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static final PermissionCommand CLEAR = registerPermission("clear", event -> {
		String[] args = Functions.Messages.multiArgs(event.getMessage());
		if (args.length < 2) Functions.Messages.sendEmbeded(event.getChannel(), 
				Functions.Messages.errorEmbed(event.getMessage(), "Must include an integer."));
		else {
			try {
				event.getChannel().purgeMessages(event.getChannel().getHistory().retrievePast(Integer.parseInt(args[1] + 1)).complete());
				Functions.Messages.sendEmbeded(event.getChannel(),
						Functions.Messages.buildEmbed("Message Purge Success", new Color(0x00ff00),
								new Field("Amount cleared:", args[1], false)));
			} catch (IllegalArgumentException e) {
				Functions.Messages.sendEmbeded(event.getChannel(),
						Functions.Messages.errorEmbed(event.getMessage(), "Can only clear up to 100 messages, or messages are too old for automated deletion. Remember, your inventory is increased by one because of your command request."));
			}
		}
	}, "Purges the amount of previous messages specified as a parameter.", Permission.MESSAGE_MANAGE);
	
	public static final PermissionCommand CUSTOM = registerPermission("custom", event -> {
		List<Attachment> atts = event.getMessage().getAttachments();
		File loc = new File("src/main/resources/custom_interface/" + event.getGuild().getId() + ".json");
		if (loc.exists()) Functions.Messages.sendEmbeded(event.getChannel(), 
				Functions.Messages.errorEmbed(event.getMessage(), "Custom server features already exist."));
		else {
			if (atts.size() <= 0) {
				Functions.Messages.sendEmbeded(event.getChannel(), 
						Functions.Messages.errorEmbed(event.getMessage(), "Must include a .json file."));
			}
			else if (!atts.get(0).getFileExtension().equalsIgnoreCase("json")) {
				Functions.Messages.sendEmbeded(event.getChannel(), 
						Functions.Messages.errorEmbed(event.getMessage(), "File must be a .json file."));
			}
			else {
				try {
					atts.get(0).downloadToFile(loc).get();
					new CustomGuildFeatures(event.getGuild());
					Functions.Messages.sendEmbeded(event.getChannel(), 
							Functions.Messages.buildEmbed("Custom Server Features", new Color(0x00ff00), 
									new Field("Success", "Added custom features.", false)));
				} catch (InterruptedException | ExecutionException | IOException e) {e.printStackTrace();}
			}
		}
	}, "Adds custom server features to this server such as commands and events. Provide a .json file for the bot to read and parse.", Permission.ADMINISTRATOR);
	
	public static final PermissionCommand CUSTOM_REMOVE = registerPermission("rmvcustom", event -> {
		File loc = new File("src/main/resources/custom_interface/" + event.getGuild().getId() + ".json");
		if (loc.exists()) {
			try {
				GuildController.removeCustomServer(event.getGuild());
				FileUtils.forceDelete(loc);
				Functions.Messages.sendEmbeded(event.getChannel(), Functions.Messages.buildEmbed("Custom Server Features", new Color(0x00ff00),
						new Field("Success", "Deleted custom server features.", false)));
			} catch (IOException e) {e.printStackTrace();}
		} else {
			Functions.Messages.sendEmbeded(event.getChannel(), 
					Functions.Messages.errorEmbed(event.getMessage(), "Custom server features do not currently exist."));
		}
	}, "Removes any custom server features.", Permission.ADMINISTRATOR);
	
//////////////////////////////////////////////////////////////////////////////////////////////////
	
	@RegistryBus
	public static void registerAll() {
		for (Command cmd : COMMANDS) {
			cmd.register();
			System.out.println(cmd.getName() + " command is registered.");
		}
		for (OwnerCommand cmd : OWNER_COMMANDS) {
			cmd.register();
			System.out.println(cmd.getName() + " owner command is registered.");
		}
		for (PermissionCommand cmd : PERMISSION_COMMANDS) {
			cmd.register();
			System.out.println(cmd.getName() + " permission command is registered.");
		}
	}
}