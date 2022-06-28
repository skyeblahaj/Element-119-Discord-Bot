package discordbot.core.audio;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

public class TrackScheduler extends AudioEventAdapter {

	private final AudioPlayer player;
	private final BlockingQueue<AudioTrack> queue;
	private boolean loop = false;
	private int secondsIn;
	private ScheduledExecutorService sched;
	
	public TrackScheduler(AudioPlayer player) {
		this.player = player;
		this.queue = new LinkedBlockingQueue<>();
	}
	
	public void queue(AudioTrack track) {
		if (!this.player.startTrack(track, true)) {
			this.queue.offer(track);
		} else {
			this.sched = Executors.newScheduledThreadPool(1);
			this.sched.scheduleAtFixedRate(() -> {
				this.secondsIn++;
			}, 0, 1, SECONDS);
		}
	}
	
	public void queue(AudioPlaylist list) {
		List<AudioTrack> trackList = list.getTracks();
		for (AudioTrack track : trackList) {
			queue(track);
		}
	}
	
	public void nextTrack() {
		if (this.player.startTrack(this.queue.poll(), false)) {
			this.sched = Executors.newScheduledThreadPool(1);
			this.sched.scheduleAtFixedRate(() -> {
				this.secondsIn++;
			}, 0, 1, SECONDS);
		}
	}
	
	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {	
		this.secondsIn = 0;
		this.sched.shutdown();
		if (endReason.mayStartNext) {
			if (this.loop) {
				AudioTrack clone = track.makeClone();
				this.player.startTrack(clone, true);
			} else nextTrack();
		}
	}
	
	public BlockingQueue<AudioTrack> getQueue(){
		return this.queue;
	}
	
	public AudioPlayer getPlayer() {
		return this.player;
	}
	
	public int getSecondsIn() {
		return this.secondsIn;
	}
}