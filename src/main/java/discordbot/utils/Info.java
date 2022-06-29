package discordbot.utils;

import java.io.File;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;

@BusPassenger
public class Info {

	public static final String PREFIX = "->";
	public static final String SUBPREFIX = "$$";
	public static final String OWNER_TAG = "cyclone#5387";
	public static final String OWNER_ID = Functions.Utils.readToken(new File("src/main/resources/private/my_id.prv"));
	
	public static FFmpeg FFMPEG;
	public static FFprobe FFPROBE;
	public static FFmpegExecutor FFMPEG_EXECUTOR;

	@RegistryBus
	public static void bypassUnhandledExceptions() {
		try {
			
			FFMPEG = new FFmpeg(Functions.Utils.readToken(new File("src/main/resources/private/ffmpeg.prv")));
			FFPROBE = new FFprobe(Functions.Utils.readToken(new File("src/main/resources/private/ffprobe.prv")));
			FFMPEG_EXECUTOR = new FFmpegExecutor(FFMPEG, FFPROBE);
			
		} catch (Exception e) {e.printStackTrace();}
	}
	
	private Info() {}
}