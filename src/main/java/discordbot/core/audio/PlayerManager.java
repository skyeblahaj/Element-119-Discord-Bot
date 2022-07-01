package discordbot.core.audio;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import discordbot.utils.BusPassenger;
import discordbot.utils.Functions;
import discordbot.utils.RegistryBus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@BusPassenger
public class PlayerManager {

	private static PlayerManager INSTANCE;
	
	private final Map<String, GuildMusicManager> guildManagers;
	private final AudioPlayerManager player;
	
	private PlayerManager() {
		this.guildManagers = new HashMap<>();
		this.player = new DefaultAudioPlayerManager();
		AudioSourceManagers.registerRemoteSources(player);
		AudioSourceManagers.registerLocalSource(player);
	}
	
	public GuildMusicManager getMusicManager(Guild guild) {
		return this.guildManagers.computeIfAbsent(guild.getId(), id -> {
			final GuildMusicManager manager = new GuildMusicManager(this.player);
			guild.getAudioManager().setSendingHandler(manager.getSendHandler());
			return manager;
		});
	}
	
	public void load_play(MessageReceivedEvent event, String trackSelected) {
		final GuildMusicManager serverMainAudioManager = this.getMusicManager(event.getGuild());
		this.player.loadItemOrdered(serverMainAudioManager, trackSelected, new AudioLoadResultHandler() {
			
			@Override
			public void trackLoaded(AudioTrack track) {
				serverMainAudioManager.getScheduler().queue(track);
				if (trackSelected.startsWith("http"))
					Functions.Messages.sendEmbeded(event.getChannel(),
							Functions.Messages.buildEmbed("Audio Player", new Color(0x00ff00),
									new Field("Loading:", track.getInfo().title, false),
									new Field("Author:", track.getInfo().author, false),
									new Field("Length:", Long.toString(track.getInfo().length / 1000) + 's', false)));
			}
			
			@Override
			public void playlistLoaded(AudioPlaylist playlist) {
				serverMainAudioManager.getScheduler().queue(playlist);
			}
			
			@Override
			public void noMatches() {
				Functions.Messages.sendEmbeded(event.getChannel(),
						Functions.Messages.errorEmbed(event.getMessage(), "Cannot retrieve URL or file."));
			}
			
			@Override
			public void loadFailed(FriendlyException exception) {
				Functions.Messages.sendEmbeded(event.getChannel(),
						Functions.Messages.errorEmbed(event.getMessage(), "Cannot retrieve URL or file."));
			}
		});
	}
	
	public static PlayerManager getInstance() {
		return INSTANCE;
	}
	
	@RegistryBus
	public static void registerPlayerInstance() {
		INSTANCE = new PlayerManager();
	}
}