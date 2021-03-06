package discordbot.core.command;

import static discordbot.utils.Functions.Messages.buildEmbed;
import static discordbot.utils.Functions.Messages.errorEmbed;
import static discordbot.utils.Functions.Messages.isPinging;
import static discordbot.utils.Functions.Messages.multiArgs;
import static discordbot.utils.Functions.Messages.sendEmbeded;
import static discordbot.utils.Functions.Messages.sendFile;
import static discordbot.utils.Functions.Messages.sendFileReply;
import static discordbot.utils.Functions.Messages.sendMessage;
import static discordbot.utils.Functions.Network.getJSON;
import static discordbot.utils.Functions.OAuth.youtubeInstance;
import static discordbot.utils.Functions.Rendering.findColor;
import static discordbot.utils.Functions.Rendering.findFont;
import static discordbot.utils.Functions.Rendering.resizeCanvas;
import static discordbot.utils.Functions.Utils.capitalize;
import static discordbot.utils.Functions.Utils.writeToFile;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import javax.imageio.ImageIO;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
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
import discordbot.core.download.Downloader;
import discordbot.core.event.actions.MessageReceivedAction;
import discordbot.core.exceptions.IllegalMediaException;
import discordbot.core.exceptions.NotFoundException;
import discordbot.core.math.Operations;
import discordbot.core.math.chem.ChemicalFormula;
import discordbot.core.network.GetRequester;
import discordbot.core.network.PostRequester;
import discordbot.core.render.Converter;
import discordbot.core.render.Cropper;
import discordbot.core.render.ImageLayerer;
import discordbot.core.render.Rotator;
import discordbot.core.render.Scaler;
import discordbot.inter_face.custom.server.CustomGuildFeatures;
import discordbot.inter_face.custom.server.GuildController;
import discordbot.inter_face.custom.user.CustomUserFeatures;
import discordbot.inter_face.custom.user.UserController;
import discordbot.inter_face.debug.ManualControl;
import discordbot.utils.BusPassenger;
import discordbot.utils.Debug;
import discordbot.utils.Functions;
import discordbot.utils.Info;
import discordbot.utils.RegistryBus;
import discordbot.utils.function.SameThreadRunnable;
import discordbot.utils.media.AudioTypes;
import discordbot.utils.media.ImageTypes;
import discordbot.utils.media.MediaType;
import discordbot.utils.media.VideoTypes;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.managers.AudioManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

@BusPassenger
public final class CommandRegistry {
	
	private CommandRegistry() {}
	
	public static final List<Command> COMMANDS = new ArrayList<>();
	public static final List<OwnerCommand> OWNER_COMMANDS = new ArrayList<>();
	public static final List<PermissionCommand> PERMISSION_COMMANDS = new ArrayList<>();
	
	public static final List<List<? extends Command>> ALL_COMMANDS = List.of(COMMANDS, OWNER_COMMANDS, PERMISSION_COMMANDS);
	
	private static Command register(String name, MessageReceivedAction action, String help, boolean debug) {
		Command cmd = new Command(name, action, help, debug);
		COMMANDS.add(cmd);
		return cmd;
	}
	
	private static Command register(String name, MessageReceivedAction action, boolean debug) {
		return register(name, action, null, debug);
	}
	
	private static Command register(String name, MessageReceivedAction action, String help) {
		return register(name, action, help, false);
	}
	
	private static Command register(String name, MessageReceivedAction action) {
		return register(name, action, null);
	}
	
	private static OwnerCommand registerOwner(String name, MessageReceivedAction action, String help) {
		OwnerCommand cmd = new OwnerCommand(name, action, help);
		OWNER_COMMANDS.add(cmd);
		return cmd;
	}
	
	private static OwnerCommand registerOwner(String name, MessageReceivedAction action) {
		return registerOwner(name, action, null);
	}
	
	private static PermissionCommand registerPermission(String name, MessageReceivedAction action, String help, boolean debug, Permission... perms) {
		PermissionCommand cmd = new PermissionCommand(name, action, help, debug, perms);
		PERMISSION_COMMANDS.add(cmd);
		return cmd;
	}
	
	private static PermissionCommand registerPermission(String name, MessageReceivedAction action, String help, Permission... perms) {
		return registerPermission(name, action, help, false, perms);
	}
	
	private static PermissionCommand registerPermission(String name, MessageReceivedAction action, boolean debug, Permission... perms) {
		return registerPermission(name, action, null, debug, perms);
	}
	
	private static PermissionCommand registerPermission(String name, MessageReceivedAction action, Permission... perms) {
		return registerPermission(name, action, false, perms);
	}
	
//////////////////////////////////////////////////////////////////////////////////////////////////
//CASUAL COMMANDS
	public static final Command CMD_HELP = register("cmdhelp", event -> {
		String[] args = multiArgs(event.getMessage());
		if (args.length < 2) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Include the string of another command."));
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
				sendEmbeded(event.getChannel(), 
						buildEmbed("Command Help", new Color(0x00ff00),
								new Field("Notice:", "Developer Only", false),
								new Field((String) capitalize(cmd.getName()), cmd.getHelp() == null ? "No help message is provided." : cmd.getHelp(), false)));
			} else if (cmd instanceof PermissionCommand) {
				String perms = "";
				for (Permission p : ((PermissionCommand) cmd).getPerms()) {
					perms += p.getName() + "\n";
				}
				sendEmbeded(event.getChannel(), 
						buildEmbed("Command Help", new Color(0x00ff00),
								new Field("Permissions Needed:", perms, false),
								new Field((String) capitalize(cmd.getName()), cmd.getHelp() == null ? "No help message is provided." : cmd.getHelp(), false),
								cmd.isDebug() ? new Field("Notice:", "Developer Only --- Currently in development.", false) : null));
			} else if (cmd == null) {
				sendEmbeded(event.getChannel(), 
						errorEmbed(event.getMessage(), "Cannot find requested command."));
			} else {
				sendEmbeded(event.getChannel(), 
						buildEmbed("Command Help", new Color(0x00ff00),
								new Field((String) capitalize(cmd.getName()), cmd.getHelp() == null ? "No help message is provided." : cmd.getHelp(), false),
								cmd.isDebug() ? new Field("Notice:", "Developer Only --- Currently in development.", false) : null));
			}
		}
	}, "You're already on the help page. Stupid.");
	
	public static final Command INFORMATION = register("info", event -> {
		sendEmbeded(event.getChannel(),
				buildEmbed("Bot Information",
						new Color(0x42f598),
						new Field("Owner:", Info.OWNER_TAG, false),
						new Field("Language:", "Java", false),
						new Field("GitHub:", "https://github.com/skyeblahaj/Element-119-Discord-Bot", false)).setThumbnail(Element119.mainJDA.getSelfUser().getAvatarUrl()));
	}, "Returns generic information about the bot.");
	
	public static final Command HELP = register("help", event -> {
		MessageChannel c = event.getChannel();
		switch (Functions.RANDOM.nextInt(4)) {
		case 0 : sendMessage(c, "the walls are filled with j"); break;
		case 1 : sendMessage(c, "hello " + event.getAuthor().getAsTag()); break;
		case 2 : sendMessage(c, "?????????? ???????? ?????????? ??????????"); break;
		case 3 : sendMessage(c, "burger credit card"); break;
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
					list += "\nRequires:" + permsNeeded + c.getName() + (c.isDebug() ? " --- WIP (temporarily dev only)" : "");
				} else {
					list += "\n" + c.getName() + (c.isDebug() ? " --- WIP (temporarily dev only)" : "");
				}
			}
		}
		sendMessage(event.getChannel(), "```--Command List--" + list + "\n--End Of Command List--```");
	}, "Returns all registered commands. Note that this command is automated as development progress continues so some commands may not function correctly.");
	
	public static final Command AVATAR = register("avatar", event -> {
		String[] args = multiArgs(event.getMessage());
		if (args.length < 2) {
			sendEmbeded(event.getChannel(),
					buildEmbed(event.getMember().getEffectiveName() + "'s Avatar",
					new Color(0xffffff),
					event.getMember().getEffectiveAvatarUrl()));
		} else if (isPinging(args[1])) {
			Member pingedMember = event.getMessage().getMentionedMembers().get(0);
			sendEmbeded(event.getChannel(),
					buildEmbed(pingedMember.getEffectiveName() + "'s Avatar",
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
				sendEmbeded(event.getChannel(),
						buildEmbed(pingedMember.getEffectiveName() + "'s Avatar",
						new Color(0xffffff),
						pingedMember.getEffectiveAvatarUrl()));
			} catch (IndexOutOfBoundsException e) {sendEmbeded(event.getChannel(), errorEmbed(event.getMessage(), "Could not grab avatar."));}
			
		} else sendEmbeded(event.getChannel(), errorEmbed(event.getMessage(), "Could not grab avatar."));
	}, "Grabs the avatar of the user either searched my nickname, pinged, or if nothing specified, the user theirself.");
	
	public static final Command TTS = register("tts", event -> {
		String[] args = multiArgs(event.getMessage());
		new Thread(() -> {
			try {
				
				if (args.length < 3) sendEmbeded(event.getChannel(),
						errorEmbed(event.getMessage(), "Not enough parameters."));
				else {
					
					boolean website15AI;
					
					//find a voice//
					switch (args[1].toLowerCase()) {
						case "spongebob" -> {args[1] = "SpongeBob SquarePants"; website15AI = true;}
						case "scout" -> {args[1] = "Scout"; website15AI = true;}
						case "soldier" -> {args[1] = "Soldier"; website15AI = true;}
						case "pyro" -> {args[1] = "Pyro"; website15AI = true;}
						case "demoman" -> {args[1] = "Demoman"; website15AI = true;}
						case "heavy" -> {args[1] = "Heavy"; website15AI = true;}
						case "engineer" -> {args[1] = "Engineer"; website15AI = true;}
						case "medic" -> {args[1] = "Medic"; website15AI = true;}
						case "sniper" -> {args[1] = "Sniper"; website15AI = true;}
						case "spy" -> {args[1] = "Spy"; website15AI = true;}
						default -> {
							website15AI = false;
							sendEmbeded(event.getChannel(),
									errorEmbed(event.getMessage(), "Cannot find voice."));
							return;
						}
					}
					////////////////
					
					String input = "";
					for (int i = 2; i < args.length; i++) {
						input += args[i];
					}
					
					if (website15AI) {
						//15.AI
						PostRequester httpTool = new PostRequester("http://api.15.ai/app/getAudioFile5");
						
						CloseableHttpResponse resp = httpTool.post(String.format("{\"character\":\"%s\",\"emotion\":\"Contextual\",\"text\":\"%s\"}", args[1], input), 
								new BasicHeader("accept", "application/json"),
								new BasicHeader("content-type", "application/json"));
						JsonObject obj = getJSON(resp);
						httpTool.close();
						
					    File rawFile = new File("src/main/resources/cache/ttsRaw.wav");
					    File bit16 = new File("src/main/resources/cache/tts16Bit.wav");
					    String wavFile = "https://cdn.15.ai/audio/" + obj.getJsonArray("wavNames").getString(0);
					    FileUtils.copyURLToFile(new URL(wavFile), rawFile);
					    
					    AudioFormat format = new AudioFormat(44100, 16, 1, true, true);
					    Converter convertTool = new Converter(rawFile);
					    
					    convertTool.convert(AudioTypes.WAV, format, bit16);
						
						sendFileReply(event.getMessage(), bit16);
						
						// play in vc
						
						if (event.getMember().getVoiceState().inAudioChannel()) {
							AudioManager manager = event.getGuild().getAudioManager();
							if (!manager.isConnected()) manager.openAudioConnection(event.getMember().getVoiceState().getChannel());
							PlayerManager.getInstance().load_play(event, bit16.getPath());
						}
					} else {
						//UBERDUCK.AI
						
						
						//PostRequester httpTool = new PostRequester("https://app.uberduck.ai/speak#mode=tts-basic&voice=" + args[1]);
						
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
			
			sendFile(event.getChannel(), new File(attachmentOutCached));
			
		} catch (IOException e) {e.printStackTrace();}
	}, "Renders a speech bubble the exact hex color of Discord's dark mode on top of the image provided by the user.");
	
	public static final Command PLAY = register("play", event -> {
		String[] args = multiArgs(event.getMessage());
		
		AudioManager manager = event.getGuild().getAudioManager();
		
		if (event.getMessage().getAttachments().size() > 0) {
			
			String loc = "src/main/resources/cache/audioplayertemp." + event.getMessage().getAttachments().get(0).getFileExtension();
			File dir = new File(loc);
			try {
				event.getMessage().getAttachments().get(0).downloadToFile(dir).get();
			} catch (InterruptedException | ExecutionException e) {e.printStackTrace();}
			
			if (MediaType.findFormat(FilenameUtils.getExtension(loc)) instanceof VideoTypes) {
				sendEmbeded(event.getChannel(), 
						buildEmbed("Audio Player", new Color(0x00ff00), 
								new Field("Downloading File...", "This may take a few seconds, and longer if the file is in video format.", false),
								new Field("Warning:", "This will overwrite the previous file queued, if any file was queued.", false)));
				try {
					loc = "src/main/resources/cache/audioplayertemp.mp3";
					Converter convertTool = new Converter(dir);
					convertTool.convert(AudioTypes.MP3, null, new File(loc));
				} catch (IOException | IllegalMediaException | UnsupportedAudioFileException e) {e.printStackTrace();}
				
				if (!manager.isConnected()) {
					manager.openAudioConnection(event.getMember().getVoiceState().getChannel()); 
				}
				PlayerManager.getInstance().load_play(event, loc);
			} else {
				sendEmbeded(event.getChannel(),
						errorEmbed(event.getMessage(), "File format is not supported. Try audio or video."));
			}
			
		} else if (args.length < 2) sendEmbeded(event.getChannel(),
				errorEmbed(event.getMessage(), "Not enough arguments."));
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
					YouTube yt = youtubeInstance();
					for (String song : songs) {
						List<SearchResult> results = yt.search().list("id,snippet").setQ(song).setMaxResults(4L)
								.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)").execute().getItems();
						if (!results.isEmpty()) {
							String vidID = results.get(0).getId().getVideoId();
							PlayerManager.getInstance().load_play(event, "https://www.youtube.com/watch?v=" + vidID);
						} else sendEmbeded(event.getChannel(),
							errorEmbed(event.getMessage(), "Could not retrieve song via the Spotify API."));
					}
				} else {
					String input = "";
					for (int i = 1; i < args.length; i++) {
						input += " " + args[i];
					}
					YouTube yt = youtubeInstance();
					List<SearchResult> results = yt.search().list("id, snippet").setQ(input.trim()).setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)").execute().getItems();
					if (results.isEmpty()) {
						sendEmbeded(event.getChannel(),
								errorEmbed(event.getMessage(), "Can not find video or audio."));
						return;
					}
					String vidID = results.get(0).getId().getVideoId();
					PlayerManager.getInstance().load_play(event, "https://www.youtube.com/watch?v=" + vidID);
					
				}
			} catch (IllegalArgumentException e) {
					sendEmbeded(event.getChannel(),
						errorEmbed(event.getMessage(), "User is not connected to a voice channel."));
			} catch (IOException e) {}
		}
	}, "Plays audio from different sources. Youtube links, Spotify links, and files. Must be in a voice channel to use.");
	
	public static final Command TRACK = register("track", event -> {
		if (event.getGuild().getAudioManager().isConnected()) {
			final GuildMusicManager manager = PlayerManager.getInstance().getMusicManager(event.getGuild());
			final AudioPlayer player = manager.getPlayer();
			AudioTrackInfo info = null;
			try {
				info = player.getPlayingTrack().getInfo();
				
				sendEmbeded(event.getChannel(),
						buildEmbed("Track Info", Color.CYAN,
								new Field("Title:", info.title, false),
								new Field("Author:", info.author, false),
								new Field("Source:", info.uri, false),
								new Field("Time:", manager.getScheduler().getSecondsIn() + " / " + (info.length / 1000) + 's', false)));
			} catch (NullPointerException e) {
				sendEmbeded(event.getChannel(),
						errorEmbed(event.getMessage(), "No track is playing."));
			}		
		} else sendEmbeded(event.getChannel(),
					errorEmbed(event.getMessage(), "Bot is not connected to an audio channel."));
	}, "Returns the current playing song's information. This includes the title, author, source, and the elapsed time since it started.");
	
	public static final Command NEXT = register("next", event -> {
		if (event.getGuild().getAudioManager().isConnected() && event.getMember().getVoiceState().inAudioChannel()) {
			final GuildMusicManager manager = PlayerManager.getInstance().getMusicManager(event.getGuild());
			if (manager.getScheduler().getQueue() == null) {
				sendEmbeded(event.getChannel(),
						errorEmbed(event.getMessage(), "Queue is empty."));
			} else manager.getScheduler().nextTrack();
		} else sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Not in voice channel."));
	}, "Skips to the next song within the queue.");
	
	public static final Command VC = register("vc", event -> {
		if (event.getMember().getVoiceState().inAudioChannel()) {
			final GuildMusicManager manager = PlayerManager.getInstance().getMusicManager(event.getGuild());
			AudioManager aManager = event.getGuild().getAudioManager();
			if (aManager.isConnected()) {
				aManager.closeAudioConnection();
				manager.kill();
			}
			else aManager.openAudioConnection(event.getMember().getVoiceState().getChannel());
		} else sendEmbeded(event.getChannel(),
					errorEmbed(event.getMessage(), "User is not in a voice channel."));
	}, "Toggles connection to the voice channel the user is connected to.");
	
	public static final Command STOP = register("stop", event -> {
		if (event.getMember().getVoiceState().inAudioChannel()) {
			final GuildMusicManager manager = PlayerManager.getInstance().getMusicManager(event.getGuild());
			AudioManager audio = event.getGuild().getAudioManager();
			if (audio.isConnected()) {
				manager.kill();
			} else {
				sendEmbeded(event.getChannel(),
						errorEmbed(event.getMessage(), "Bot is not connected to an audio channel."));
			}
		} else sendEmbeded(event.getChannel(),
				errorEmbed(event.getMessage(), "User is not in a voice channel."));
	}, "Stops the current track and clears the queue.");
	
	@Debug
	public static final Command CAPTION = register("caption", event -> {
		String[] args = multiArgs(event.getMessage());
		if (event.getMessage().getAttachments().size() <= 0) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Must include an attachment."));
		} else {
			Attachment toCaption = event.getMessage().getAttachments().get(0);
			File cache = new File("src/main/resources/cache/caption_cache." + toCaption.getFileExtension()),
				 output = new File("src/main/resources/cache/caption." + toCaption.getFileExtension());
			try {
				FileUtils.copyURLToFile(new URL(toCaption.getProxyUrl()), cache);
				BufferedImage origin = ImageIO.read(cache);
				int x = origin.getWidth();
				int y = origin.getHeight();
				BufferedImage resized = resizeCanvas(origin, x, y + y / 5, false);
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
			
			sendFile(event.getChannel(), output);
		}
	}, true);
	
	public static final Command MC_SERVER = register("mc", event -> {
		String[] args = multiArgs(event.getMessage());
		try {
			GetRequester httpTool = new GetRequester("https://api.mcsrvstat.us/2/mc.omobasil.xyz");
			JsonObject obj = getJSON(httpTool.get());
			
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
				sendEmbeded(event.getChannel(),
						buildEmbed("Minecraft Server", new Color(0x99652e),
								new Field("Host:", "mc.omobasil.xyz", true),
								new Field("Port:", obj.getInt("port") + "", true),
								new Field("Player Count:", playerCount + " / " +
														   obj.getJsonObject("players").getInt("max"), false),
								(playerCount > 0) ? new Field("Players:", players, false) : null, 
								new Field("How to Join:", "https://www.omobasil.xyz/servers/modmc/", false))
						.setThumbnail("https://cdn.discordapp.com/attachments/592579994408976384/991204974170218537/server-icon.png"));
				File motd = new File("src/main/resources/reusable/motd.html");
				
				if (args.length > 1) if (args[1].equalsIgnoreCase("motd")) {
					writeToFile(motd, "<!DOCTYPE html>\n<html>\n" + 
							obj.getJsonObject("motd")							 							   
							   .getJsonArray("html")
							   .getString(0) + "\n</html>");
					sendFile(event.getChannel(), motd);
				}
			} else {
				sendEmbeded(event.getChannel(), 
						buildEmbed("Minecraft Server", new Color(0xff0000),
								new Field("Error:", "Server Offline", false)));
			}
		} catch (Exception e) {e.printStackTrace();}
	}, "Returns the current information of the Minecraft server hosted by the bot owner. Write 'motd' as a parameter to access the MOTD message.");
	
	public static final Command CONVERT = register("convert", event -> {
		String[] args = multiArgs(event.getMessage());
		
		if (args.length < 2)
			sendEmbeded(event.getChannel(),
					errorEmbed(event.getMessage(), "Must include a file format."));
		else {
			new Thread(() -> {
				List<Attachment> attachments = event.getMessage().getAttachments();
				if (attachments.size() <= 0)
					sendEmbeded(event.getChannel(),
							errorEmbed(event.getMessage(), "Must include an attachment."));
				else {
					Attachment toConvert = attachments.get(0);
					if (toConvert.getSize() < 10485760) {
						File inventory = new File("src/main/resources/cache/convert_input." + toConvert.getFileExtension());
						try {
							toConvert.downloadToFile(inventory).get();
						} catch (InterruptedException | ExecutionException e) {e.printStackTrace();}

						try {
							Converter convertTool = new Converter(inventory);
							convertTool.convert(MediaType.findFormat(args[1]), null, null);
							sendFileReply(event.getMessage(), convertTool.getOutputFile());
						} catch (IllegalMediaException e) {
							sendEmbeded(event.getChannel(), errorEmbed(
									event.getMessage(), "Format provided does not support the media type provided."));
						} catch (UnsupportedAudioFileException e) {
							sendEmbeded(event.getChannel(), errorEmbed(
									event.getMessage(), "Format provided is not supported."));
						} catch (IOException e) {e.printStackTrace();}
					} else {
						sendEmbeded(event.getChannel(), 
								errorEmbed(event.getMessage(), "Please keep inventory files below 10 MB."));
					}
				}
			}).start();
		}
	}, "Converts video, audio, or images to different formats.");
	
	public static final Command SHOW_PERMISSIONS = register("showperms", event -> {
		String[] args = multiArgs(event.getMessage());
		String perms = "";
		if (args.length < 2) {
			for (Permission p : event.getMember().getPermissions()) {
				perms += "\n" + p.getName();
			}
			sendEmbeded(event.getChannel(),
					buildEmbed(event.getMember().getNickname() + "'s Permissions",
					new Color(0xffffff),
					new Field("List of Perms:", perms, false)));
		} else if (isPinging(args[1])) {
			Member pingedMember = event.getMessage().getMentionedMembers().get(0);
			for (Permission p : pingedMember.getPermissions()) {
				perms += "\n" + p.getName();
			}
			sendEmbeded(event.getChannel(),
					buildEmbed(pingedMember.getEffectiveName() + "'s Permissions",
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
				sendEmbeded(event.getChannel(),
						buildEmbed(pingedMember.getEffectiveName() + "'s Permissions",
						new Color(0xffffff),
						new Field("List of Perms:", perms, false)));
			} catch (IndexOutOfBoundsException e) {sendEmbeded(event.getChannel(), errorEmbed(event.getMessage(), "Could not grab permissions."));}
			
		} else sendEmbeded(event.getChannel(), errorEmbed(event.getMessage(), "Could not grab permissions."));
	}, "Returns all permissions the user has, or user specified as a parameter.");
	
	public static final Command RANDOM = register("random", event -> {
		String output = "";
		int charAmount = Functions.RANDOM.nextInt(150) + 1;
		char[] data = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
		for (int i = 0; i < charAmount; i++) {
			output += (Functions.RANDOM.nextBoolean()) ? data[Functions.RANDOM.nextInt(data.length)] : Character.toUpperCase(data[Functions.RANDOM.nextInt(data.length)]);
		}
		sendMessage(event.getChannel(), output);
	}, "Generates a random alpha-numeric character sequence from a length of 1-150.");
	
	public static final Command CLICKBAIT = register("clickbait", event -> {
		String[] args = multiArgs(event.getMessage());
		
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
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Must include an image attachment."));
		} else if (!(MediaType.findFormat(imageInput.get(0).getFileExtension()) instanceof ImageTypes)) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Attachment must be an image."));
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
			
			sendFileReply(event.getMessage(), new File(output));
		}
	}, "Renders the image proveded by the user over a predetermined image of an internet clickbait thumbnail. Specify an integer as a parameter to select a specific thumbnail.");
	
	public static final Command PRINT = register("print", event -> {
		String[] args = multiArgs(event.getMessage());
		
		if (args.length < 2) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Include a string."));
		} else {
			String print = "";
			for (int i = 1; i < args.length; i++) {
				print += " " + args[i];
			}
			sendMessage(event.getChannel(), print.trim());
		}
	}, "Sends an identical message.");
	
	public static final Command ADVICE = register("advice", event -> {
		GetRequester httpTool = new GetRequester("https://api.adviceslip.com/advice");
		try {
			JsonObject slip = Json.createReader(httpTool.get().getEntity().getContent()).readObject();
			String advice = slip.getJsonObject("slip").getString("advice");
			
			sendEmbeded(event.getChannel(), 
					buildEmbed("adviceslip.com", new Color(0x7acaff), 
							new Field("Advice", advice, false)));
			
			httpTool.close();
		} catch (IOException e) {}
	}, "Returns a helpful message.");
	
	public static final Command RENDER = register("render", event -> {
		String[] args = multiArgs(event.getMessage());
		List<Attachment> attachments = event.getMessage().getAttachments();
		int imageAmount = attachments.size();
		
		if (imageAmount < 2) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Include at least two attachments."));
			return;
		}
		
		if (args.length - 1 < imageAmount * 2) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Include two integers per image."));
			return;
		}
		
		int[] positions = new int[args.length - 1];
		try {
			for (int i = 0; i < args.length - 1; i++) {
				positions[i] = Integer.parseInt(args[i + 1]);
			}
		} catch (NumberFormatException e) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Arguments are not integers."));
			return;
		}
		
		for (int pos : positions) {
			if (pos < 0) {
				sendEmbeded(event.getChannel(), 
						errorEmbed(event.getMessage(), "Integers cannot be negative."));
				return;
			}
		}
		
		try {
			for (int i = 0; i < imageAmount; i++) {
					if (!(MediaType.findFormat(attachments.get(i).getFileExtension()) instanceof ImageTypes)) {
						sendEmbeded(event.getChannel(), 
								errorEmbed(event.getMessage(), "An attachment provided is not an image."));
						return;
					}
					FileUtils.copyURLToFile(new URL(attachments.get(i).getProxyUrl()), new File("src/main/resources/cache/render" + i + "." + attachments.get(i).getFileExtension()));
			}
		} catch (IOException e) {e.printStackTrace();}
		
		BufferedImage[] asArray = new BufferedImage[attachments.size()];
		try {
			for (int i = 0; i < imageAmount; i++) {
					asArray[i] = ImageIO.read(new File("src/main/resources/cache/render" + i + "." + attachments.get(i).getFileExtension()));
			}
		} catch (IOException e) {e.printStackTrace();}
		
		ImageLayerer renderTool = new ImageLayerer(asArray);
		
		int read = 0;
		for (int i = 0; i < imageAmount; i++) {
			renderTool.render(i, positions[read], positions[read + 1]);
			read += 2;
		}
		String loc = "src/main/resources/cache/renderOut.png";
		renderTool.complete(loc);
		
		sendFileReply(event.getMessage(), new File(loc));
	}, "Renders images on one big canvas (its size determined by the largest image attached) at specific X/Y coordinates branching from the upper left corner of the canvas. Images are rendered in order (last attachment will overlap all others).");
	
	public static final Command SCALE = register("scale", event -> {
		String[] args = multiArgs(event.getMessage());
		List<Attachment> attachments = event.getMessage().getAttachments();
		
		if (attachments.size() <= 0) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Include at least one attachment."));
			return;
		}
		if (args.length < 3) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Include two integers. (X and Y positions to scale down/up to.)"));
			return;
		}
		int[] positions = new int[2];
		try {
			positions[0] = Integer.parseInt(args[1]);
			positions[1] = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Arguments are not integers."));
			return;
		}
		if (positions[0] < 1 || positions[1] < 1) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Integers cannot be negative or zero."));
			return;
		}
		if (positions[0] > 5000 || positions[1] > 5000) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Max resolution for both height and width is 5000."));
			return;
		}
		if (!(MediaType.findFormat(attachments.get(0).getFileExtension()) instanceof ImageTypes)) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "An attachment provided is not an image."));
			return;
		}
		File loc = new File("src/main/resources/cache/scaleIn." + attachments.get(0).getFileExtension());
		File outLoc = new File("src/main/resources/cache/scaleOut.png");
		try {
			FileUtils.copyURLToFile(new URL(attachments.get(0).getProxyUrl()), loc);
			
			ImageIO.write(new Scaler(ImageIO.read(loc), positions[0], positions[1]).scale(), "png", outLoc);
			
		} catch (IOException e) {e.printStackTrace();}
		
		sendFileReply(event.getMessage(), outLoc);
	}, "Scales an image to the specified width and height.");
	
	public static final Command MATH = register("math", event -> {
		String[] args = multiArgs(event.getMessage());
		
		if (args.length < 2) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Specify an operation."));
			return;
		}
		
		Operations operation = null;
		switch (args[1]) {
			case "add" -> operation = Operations.ADD;
			case "+" -> operation = Operations.ADD;
			case "plus" -> operation = Operations.ADD; 
			case "addition" -> operation = Operations.ADD; 
			case "sub" -> operation = Operations.SUBTRACT;
			case "-" -> operation = Operations.SUBTRACT; 
			case "subtract" -> operation = Operations.SUBTRACT;
			case "subtraction" -> operation = Operations.SUBTRACT;
			case "mult" -> operation = Operations.MULTIPLY;
			case "*" -> operation = Operations.MULTIPLY;
			case "multiply" -> operation = Operations.MULTIPLY;
			case "multiplication" -> operation = Operations.MULTIPLY;
			case "div" -> operation = Operations.DIVIDE;
			case "/" -> operation = Operations.DIVIDE;
			case "divide" -> operation = Operations.DIVIDE;
			case "division" -> operation = Operations.DIVIDE;
			case "exp" -> operation = Operations.POWER;
			case "^" -> operation = Operations.POWER;
			case "exponential" -> operation = Operations.POWER;
			case "exponent" -> operation = Operations.POWER;
			case "log" -> operation = Operations.LOG;
			case "logarithm" -> operation = Operations.LOG;
			case "logarithmic" -> operation = Operations.LOG;
			case "quad" -> operation = Operations.QUADRATIC;
			case "quadratic" -> operation = Operations.QUADRATIC;
			case "ax^2+bx+c" -> operation = Operations.QUADRATIC;
			case "chem" -> operation = Operations.CHEMISTRY;
			case "chemistry" -> operation = Operations.CHEMISTRY;
			case "hypot" -> operation = Operations.HYPOTENUSE;
			case "hypotenuse" -> operation = Operations.HYPOTENUSE;
			case "pythagorean" -> operation = Operations.HYPOTENUSE;
			case "f-c" -> {operation = Operations.OTHER; Operations.OTHER.setParametersNeeded(1);}
			case "c-f" -> {operation = Operations.OTHER; Operations.OTHER.setParametersNeeded(1);}
			case "c-k" -> {operation = Operations.OTHER; Operations.OTHER.setParametersNeeded(1);}
			case "f-k" -> {operation = Operations.OTHER; Operations.OTHER.setParametersNeeded(1);}
			case "k-c" -> {operation = Operations.OTHER; Operations.OTHER.setParametersNeeded(1);}
			case "k-f" -> {operation = Operations.OTHER; Operations.OTHER.setParametersNeeded(1);}
			default -> {
				sendEmbeded(event.getChannel(), 
						errorEmbed(event.getMessage(), "Math operation not found. Do \"->cmdhelp math\" for all operations."));
				return;
			}
		}
		int offset = operation.paramsNeeded();
		int extOffset = operation.extraParamsNeeded();
		if (args.length < offset + 2) { //add two to bypass command request and operation request
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Specify more parameters."));
			return;
		}
		
		String[] params = new String[args.length - 2 - extOffset];
		for (int i = 0; i < args.length - 2 - extOffset; i++) {
			params[i] = args[i + 2 + extOffset];
		}
		
		double output;
		if (operation == Operations.CHEMISTRY) {
			output = 0;
		} else {
			output = Double.parseDouble(params[0]);
		}
		String answer = null;
		
		Function<Double, String> toAnswer = (val) -> {
			return val + "";
		};
		
		switch (operation) {
		
			case ADD -> {
				for (int i = 1; i < params.length; i++) {
					output += Double.parseDouble(params[i]);
				}
			}
			
			case SUBTRACT -> {
				for (int i = 1; i < params.length; i++) {
					output -= Double.parseDouble(params[i]);
				}
			}
			
			case MULTIPLY -> {
				for (int i = 1; i < params.length; i++) {
					output *= Double.parseDouble(params[i]);
				}
			}
			
			case DIVIDE -> {
				for (int i = 1; i < params.length; i++) {
					output /= Double.parseDouble(params[i]);
				}
			}
			
			case POWER -> {
				for (int i = 1; i < params.length; i++) {
					output = Operations.EXPONENTIAL.carry(output, Double.parseDouble(params[i]));
				}
			}
			
			case LOG -> {
				for (int i = 1; i < params.length; i++) {
					output = Operations.BASE_LOG.carry(Double.parseDouble(params[i]), output);
				}
			}
			
			case QUADRATIC -> {
				double[] xVals = Operations.QUADRATICS.apply(Double.parseDouble(params[0]), Double.parseDouble(params[1]), Double.parseDouble(params[2]));
				toAnswer = (val) -> {
					return xVals[0] + ", " + xVals[1];
				};
			}
			
			case CHEMISTRY -> {
				switch (args[2].toLowerCase()) {
					case "molarmass" -> {
						output = new ChemicalFormula(args[3]).molarMass(); //wip
					}
				}
			}
			
			case HYPOTENUSE -> {
				output = Operations.HYPOT.carry(Double.parseDouble(params[0]), Double.parseDouble(params[1]));
			}
			
			case OTHER -> {
				switch (args[1].toLowerCase()) {
					case "f-c" -> output = Operations.FAHRENHEIT_TO_CELSIUS.carry(Double.parseDouble(params[0]));
					case "c-f" -> output = Operations.CELSIUS_TO_FAHRENHEIT.carry(Double.parseDouble(params[0]));
					case "c-k" -> output = Operations.CELSIUS_TO_KELVIN.carry(Double.parseDouble(params[0]));
					case "f-k" -> output = Operations.FAHRENHEIT_TO_KELVIN.carry(Double.parseDouble(params[0]));
					case "k-c" -> output = Operations.KELVIN_TO_CELSIUS.carry(Double.parseDouble(params[0]));
					case "k-f" -> output = Operations.KELVIN_TO_FAHRENHEIT.carry(Double.parseDouble(params[0]));
					default -> {
						sendEmbeded(event.getChannel(), 
								errorEmbed(event.getMessage(), "Internal Server Error. Please report this issue."));
						throw new NotFoundException("operation not found and not caught by filter");
					}
				}
			}
		}
		
		answer = toAnswer.apply(output);
		
		sendEmbeded(event.getChannel(), 
				buildEmbed("Math", new Color(0x0000ff),
						new Field("Output:", answer, false)));
		
	}, "Basic bitwise math. Specify the operation before the numbers. To specify an operation, use its name, symbol, or abbreviation.\nExample: \"->math log 10 100\" returns 2.");
	
	public static final Command CROP = register("crop", event -> {
		String[] args = multiArgs(event.getMessage());
		if (event.getMessage().getAttachments().size() <= 0) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Include an attachment."));
			return;
		}
		if (args.length < 2) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Include at least one integer."));
			return;
		}
		Attachment toCrop = event.getMessage().getAttachments().get(0);
		if (!(MediaType.findFormat(toCrop.getFileExtension()) instanceof ImageTypes)) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Attachment must be an image."));
			return;
		}
		File input = new File("src/main/resources/cache/cropIn." + toCrop.getFileExtension());
		try {
			FileUtils.copyURLToFile(new URL(toCrop.getProxyUrl()), input);
			BufferedImage in = ImageIO.read(input);
			
			int top = 1, right = 1, bottom = 1, left = 1;
			try {
				if (args.length >= 2) top = Integer.parseInt(args[1]);
				if (args.length >= 3) right = Integer.parseInt(args[2]);
				if (args.length >= 4) bottom = Integer.parseInt(args[3]);
				if (args.length > 4) left = Integer.parseInt(args[4]);
			} catch (NumberFormatException e) {
				sendEmbeded(event.getChannel(), 
						errorEmbed(event.getMessage(), "Parameters are not integers."));
				return;
			}
			
			File output = new File("src/main/resources/cache/cropOutput.png");
			try {
				ImageIO.write(new Cropper(in).crop(top, right, bottom, left), "png", output);
			} catch (RasterFormatException e) {
				sendEmbeded(event.getChannel(), 
						errorEmbed(event.getMessage(), "Invalid parameters relating to the image. Perhaps the integers you input are bigger than the image's dimensions.\nUse \"->mediainfo\" to get the dimensions of an attachment.\nUse \"->cmdhelp crop\" for instructions on the cropping procedure."));
				return;
			}
			sendFileReply(event.getMessage(), output);
			
		} catch (IOException e) {}
	}, "Crops an image from all sides. Input integers representing pixels to cut into the side. The rotation for parameters is clockwise staring north.\nExample: \"->crop 10 20 30 40\" will cut into the top 10 pixels, the right 20 pixels, the bottom 30 pixels, and the left 40 pixels.");
	
	public static final Command MEDIA_INFO = register("mediainfo", event -> {
		if (event.getMessage().getAttachments().size() <= 0) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Include an attachment."));
			return;
		}
		Attachment infoImg = event.getMessage().getAttachments().get(0);
		int x = infoImg.getWidth(), y = infoImg.getHeight();
		if (x == -1 || y == -1) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Attachment must be a video or image."));
			return;
		}
		
		sendEmbeded(event.getChannel(), 
				buildEmbed("Media Info", new Color(0x0000ff), 
						new Field("File name:", infoImg.getFileName(), false),
						new Field("Width:", x + "px", true),
						new Field("Height:", y + "px", true)).setThumbnail(infoImg.getProxyUrl()));
		
	}, "Retrieves information of the image provided.");
	
	public static final Command DRAW_TEXT = register("drawtext", event -> {
		String[] args = multiArgs(event.getMessage());
		
		if (event.getMessage().getAttachments().size() <= 0) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Include an attachment."));
			return;
		}
		Attachment toDraw = event.getMessage().getAttachments().get(0);
		if (!(MediaType.findFormat(toDraw.getFileExtension()) instanceof ImageTypes)) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Attachment must be an image."));
			return;
		}
		if (args.length < 7) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Include two integers (x and y), a color (string or hexadecimal), a font, the font size, and a string."));
			return;
		}
		
		int[] coords = new int[2];
		try {
			coords[0] = Integer.parseInt(args[1]);
			coords[1] = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Parameters are not integers."));
			return;
		}
		Color textColor = findColor(args[3]).get();
		if (textColor == null) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Color not found. If you think this is a problem and you entered a valid color, please report this."));
			return;
		}
		int fontSize = 0;
		try {
			fontSize = Integer.parseInt(args[5]);
		} catch (NumberFormatException e) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Font size is not an integer."));
			return;
		}
		Font font = findFont(args[4] + "-" + fontSize);
		if (font == null) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Font not found. If you think this is a problem and you entered a valid font, please report this."));
			return;
		}
		
		String inputText = "";
		for (int i = 6; i < args.length; i++) {
			inputText += " " + args[i];
		}
		
		try {
			File loc = new File("src/main/resources/cache/drawInput." + toDraw.getFileExtension());
			File out = new File("src/main/resources/cache/drawOutput.png");
			FileUtils.copyURLToFile(new URL(toDraw.getProxyUrl()), loc);
			
			BufferedImage img = ImageIO.read(loc);
			Graphics2D g = img.createGraphics();
			g.setFont(font);
			g.setColor(textColor);
			g.drawString(inputText.trim(), coords[0], coords[1]);
			g.dispose();
			
			ImageIO.write(img, "png", out);
			sendFileReply(event.getMessage(), out);
			
		} catch (IOException e) {}
		
	}, "Draws text over an image. There are many parameters needed. These are:\nAn attachment, X coordinate, Y coordinate, Text color, Text font, Font size, and the rest being whatever text you want to be drawn.");
	
	public static final Command API = register("api", event -> {
		sendEmbeded(event.getChannel(), 
				buildEmbed("Element-119 API", new Color(0x34eba1), 
						new Field("How to set up:", "This API is for implementing your own custom features into your server. To add these, server members with **Administrator** access can "
								+ "use the \"->custom\" command, attachming a **.json** file containing the features. To build the file correctly, check out the documentation over on the GitHub page.", false),
						new Field("Link:", "https://github.com/skyeblahaj/Element-119-Discord-Bot", false)
						).setThumbnail(event.getJDA().getSelfUser().getEffectiveAvatarUrl()));
	}, "API Instructions.");
	
	@Debug
	public static final Command ROTATE = register("rotate", event -> {
		String[] args = multiArgs(event.getMessage());
		if (args.length < 2) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Enter an integer value (as degrees)."));
			return;
		}
		if (event.getMessage().getAttachments().size() <= 0) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Include an attachment."));
			return;
		}
		Attachment inputFile = event.getMessage().getAttachments().get(0);
		if (!(MediaType.findFormat(inputFile.getFileExtension()) instanceof ImageTypes)) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Attachment must be an image."));
			return;
		}
		File inputLoc = new File("src/main/resources/cache/rotateIn." + inputFile.getFileExtension());
		File outputLoc = new File("src/main/resources/cache/rotateOut.png");
		try {
			FileUtils.copyURLToFile(new URL(inputFile.getProxyUrl()), inputLoc);
			Rotator renderTool = new Rotator(ImageIO.read(inputLoc));
			ImageIO.write(renderTool.rotate(Integer.parseInt(args[1])), "png", outputLoc);
		} catch (IOException e) {}
		
		sendFileReply(event.getMessage(), outputLoc);
	}, true);
	
	@Debug
	public static final Command DOWNLOAD = register("download", event -> {
		String[] args = multiArgs(event.getMessage());
		if (args.length < 2) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Include a youtube URL."));
			return;
		}
		try {
			Downloader.getInstance().download(args[1], "src/main/resources/cache/ytdownload");
		} catch (IOException e) {e.printStackTrace();}
	}, true);
	
	@Debug
	public static final Command USER_FEATURES = register("userfeature", event -> {
		
	}, true);
	
	@Debug
	public static final Command CROSSOVER = register("crossover", event -> {
	
		File data = UserController.USER_DATA.get(event.getMember().getUser());
		
		if (!data.exists()) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "You have not set a custom crossover feature."));
			return;
		}
		
		CustomUserFeatures features = UserController.FEATURES.get(event.getMember().getUser());
		String crossoverData = ((JsonString) features.getData("crossover")).getString();
		
		sendMessage(event.getChannel(), crossoverData);
		
	}, true);
	
	@Debug
	public static final Command REMOVE_CROSSOVER = register("rmvcrossover", event -> {
		
	}, true);
	
//////////////////////////////////////////////////////////////////////////////////////////////////
//OWNER COMMANDS

	public static final OwnerCommand BOT_SHUTDOWN = registerOwner("botshutdown", event -> {
		sendMessage(event.getChannel(), "Bot is shutting down...");
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {e.printStackTrace();}
		System.exit(0);
	}, "Shuts down the current instance of the bot.");
	
	public static final OwnerCommand SYSTEM_SHUTDOWN = registerOwner("systemshutdown", event -> {
		sendMessage(event.getChannel(), "System is shutting down...");
		try {
			Thread.sleep(1500);
			Runtime.getRuntime().exec("shutdown -s -t 0"); //win10 only
		} catch (IOException | InterruptedException e) {e.printStackTrace();}
	}, "Shuts down the computer the bot is hosted on.");
	
	public static final OwnerCommand MANUAL_CONTROL = registerOwner("takeover", event -> {
		ManualControl.commandToggle = false;
		new ManualControl(true, event);
	}, "Switches to manual mode, providing a control panel.");
	
	public static final OwnerCommand RUNTIME = registerOwner("runtime", event -> {
		String[] args = multiArgs(event.getMessage());
		try {
			String[] input = new String[args.length - 1];
			for (int i = 0; i < args.length - 1; i++) {
				input[i] = args[i + 1];
			}
			Runtime.getRuntime().exec(input);
		} catch (IOException e) {e.printStackTrace();}
	}, "Direct system runtime access.");
	
	public static final OwnerCommand TOGGLE_OVERRIDE = registerOwner("override", event -> {
		if (ManualControl.overrideToggle) ManualControl.overrideToggle = false;
		else ManualControl.overrideToggle = true;
	});
	
//////////////////////////////////////////////////////////////////////////////////////////////////
//PERMISSION COMMANDS	
	public static final PermissionCommand CLEAR = registerPermission("clear", event -> {
		String[] args = multiArgs(event.getMessage());
		if (args.length < 2) sendEmbeded(event.getChannel(), 
				errorEmbed(event.getMessage(), "Must include an integer."));
		else {
			try {
				List<Message> toPurge = event.getChannel().getHistory().retrievePast(Integer.parseInt(args[1]) + 1).complete();
				event.getChannel().purgeMessages(toPurge);
				sendEmbeded(event.getChannel(),
						buildEmbed("Message Purge Success", new Color(0x00ff00),
								new Field("Amount cleared:", args[1], false)));
			} catch (IllegalArgumentException e) {
				sendEmbeded(event.getChannel(),
						errorEmbed(event.getMessage(), "Can only clear up to 100 messages, or messages are too old for automated deletion. Remember, your input is increased by one because of your command request."));
			}
		}
	}, "Purges the amount of previous messages specified as a parameter.", Permission.MESSAGE_MANAGE);
	
	public static final PermissionCommand CUSTOM = registerPermission("custom", event -> {
		String[] args = multiArgs(event.getMessage());
		List<Attachment> atts = event.getMessage().getAttachments();
		File loc = new File("src/main/resources/custom_interface/" + event.getGuild().getId() + ".json");
		
		if (loc.exists()) {
			if (args.length > 1) {
				if (args[1].equalsIgnoreCase("get")) {
					sendFile(event.getChannel(), loc);
					return;
				}
			}
			sendEmbeded(event.getChannel(), 
				errorEmbed(event.getMessage(), "Custom server features already exist."));
		}
		else {
			if (atts.size() <= 0) {
				sendEmbeded(event.getChannel(), 
						errorEmbed(event.getMessage(), "Must include a .json file."));
			}
			else if (!atts.get(0).getFileExtension().equalsIgnoreCase("json")) {
				sendEmbeded(event.getChannel(), 
						errorEmbed(event.getMessage(), "File must be a .json file."));
			}
			else {
				try {
					atts.get(0).downloadToFile(loc).get();
					CustomGuildFeatures cgf = new CustomGuildFeatures(event.getGuild());
					
					cgf.register();
					sendEmbeded(event.getChannel(), 
							buildEmbed("Custom Server Features", new Color(0x00ff00), 
									new Field("Success", "Added custom features.", false)));
				} catch (InterruptedException | ExecutionException | IOException e) {e.printStackTrace();}
			}
		}
	}, "Adds custom server features to this server such as commands and events. Provide a .json file for the bot to read and parse.", Permission.ADMINISTRATOR);
	
	public static final PermissionCommand CUSTOM_REMOVE = registerPermission("rmvcustom", event -> {
		File loc = new File("src/main/resources/custom_interface/" + event.getGuild().getId() + ".json");
		if (loc.exists()) {
			try {
				GuildController.FEATURES.get(event.getGuild()).deregister();
				FileUtils.forceDelete(loc);
				sendEmbeded(event.getChannel(), buildEmbed("Custom Server Features", new Color(0x00ff00),
						new Field("Success", "Deleted custom server features.", false)));
			} catch (IOException e) {e.printStackTrace();}
		} else {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Custom server features do not currently exist."));
		}
	}, "Removes any custom server features.", Permission.ADMINISTRATOR);
	
	public static final PermissionCommand CHANGE_NICKNAME = registerPermission("nickname", event -> {
		String[] args = multiArgs(event.getMessage());
		String input = "";
		if (args.length < 2) {
			event.getGuild().getSelfMember().modifyNickname("").complete();
		} else {
			for (int i = 1; i < args.length; i++) {
				input += " " + args[i];
			}
			event.getGuild().getSelfMember().modifyNickname(input.trim()).complete();
		}
		
		sendEmbeded(event.getChannel(), 
				buildEmbed("Change Nickname", new Color(0x00ff00), 
						new Field("Success", "Nickname changed to " + (args.length < 2 ? event.getJDA().getSelfUser().getName() : input.trim()), false)));
		
	}, "Changes the bot's nickname. If no nickname is specified, it will change to the bot's username.", Permission.NICKNAME_MANAGE);
	
	public static final PermissionCommand KICK_MEMBER = registerPermission("kick", event -> {
		String[] args = multiArgs(event.getMessage());
		if (args.length < 2) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Mention a member of this server."));
			return;
		}
		if (!isPinging(args[1])) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Member must be mentioned."));
			return;
		}
		String reason = "";
		for (int i = 2; i < args.length; i++) {
			reason += " " + args[i];
		}
		List<Member> mentioned = event.getMessage().getMentionedMembers();
		if (mentioned.size() <= 0) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Mention does not include a member."));
			return;
		}
		Member memberToKick = mentioned.get(0);
		try {
			if (reason.equals("")) {
				memberToKick.kick().complete();
			} else {
				memberToKick.kick(reason.trim()).complete();
			}	
		} catch (HierarchyException e) {
			sendEmbeded(event.getChannel(), 
					errorEmbed(event.getMessage(), "Cannot kick member with equal or higher role hierarchy.", e));
			return;
		}
		sendEmbeded(event.getChannel(), 
				buildEmbed("Member Kick", new Color(0x00ff00), 
						new Field("Success", "Kicked member: **" + memberToKick.getEffectiveName() + "**", false),
						!reason.equals("") ? new Field("With Reason:", reason, false) : null));
		
	}, "Kicks a member from the server.", Permission.KICK_MEMBERS);
	
//////////////////////////////////////////////////////////////////////////////////////////////////
	
	@RegistryBus
	public static void registerAll() {
		for (List<? extends Command> cmds : ALL_COMMANDS) {
			for (Command cmd : cmds) {
				cmd.register();
				if (cmd instanceof OwnerCommand) {
					System.out.println(cmd.getName() + " command is registered.");
				} else if (cmd instanceof PermissionCommand) {
					System.out.println(cmd.getName() + " permission command is registered.");
				} else {
					System.out.println(cmd.getName() + " command is registered.");
				}
			}
		}
	}
}