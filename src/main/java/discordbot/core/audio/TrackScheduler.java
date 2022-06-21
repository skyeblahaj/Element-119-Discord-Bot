package discordbot.core.audio;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

public class TrackScheduler extends AudioEventAdapter {

	private final AudioPlayer player;
	private final BlockingQueue<AudioTrack> queue;
	private boolean loop;
	
	public TrackScheduler(AudioPlayer player) {
		this.player = player;
		this.queue = new LinkedBlockingQueue<>();
	}
	
	public void queue(AudioTrack track) {
		if (!this.player.startTrack(track, true)) this.queue.offer(track);
	}
	
	public void queue(AudioPlaylist list) {
		List<AudioTrack> trackList = list.getTracks();
		for (AudioTrack track : trackList) {
			queue(track);
		}
	}
	
	public void nextTrack() {
		this.player.startTrack(this.queue.poll(), false);
	}
	
	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		if (endReason.mayStartNext) {
			if (this.loop) {
				AudioTrack clone = track.makeClone();
				this.player.startTrack(clone, true);
			} else nextTrack();
		}
	}
	
}
