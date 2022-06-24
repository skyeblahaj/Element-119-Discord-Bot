package discordbot.core.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

public class GuildMusicManager { //encap class

	private final AudioPlayer player;
	private final TrackScheduler scheduler;
	private final AudioHandler sendHandler;
	
	public GuildMusicManager(AudioPlayerManager manager) {
		this.player = manager.createPlayer();
		this.scheduler = new TrackScheduler(this.player);
		this.player.addListener(scheduler);
		this.sendHandler = new AudioHandler(this.player);
	}
	
	public AudioHandler getSendHandler() {
		return this.sendHandler;
	}
	
	public TrackScheduler getScheduler() {
		return this.scheduler;
	}
	
	public AudioPlayer getPlayer() {
		return this.player;
	}
}