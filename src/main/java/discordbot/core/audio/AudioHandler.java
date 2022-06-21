package discordbot.core.audio;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;

import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.AudioSendHandler;

public class AudioHandler implements AudioSendHandler, AudioReceiveHandler{

	private static final int MEMORY_USAGE = 32768;
	
	private final AudioPlayer player;
	private final ByteBuffer stream;
	private final MutableAudioFrame frame;
	
	public AudioHandler(AudioPlayer player) {
		this.player = player;
		this.stream = ByteBuffer.allocate(MEMORY_USAGE);
		this.frame = new MutableAudioFrame();
		this.frame.setBuffer(stream);
	}
	
	@Override
	public boolean canProvide() {
		return this.player.provide(this.frame);
	}
	
	@Override
	public ByteBuffer provide20MsAudio() {
		final Buffer tmp = ((Buffer) this.stream).flip();
		return (ByteBuffer) tmp;
	}
	
	@Override
	public boolean isOpus() {
		return true;
	}
	
}