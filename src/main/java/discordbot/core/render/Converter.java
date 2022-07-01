package discordbot.core.render;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.io.FilenameUtils;

import discordbot.utils.Info;
import discordbot.utils.media.AudioTypes;
import discordbot.utils.media.ImageTypes;
import discordbot.utils.media.MediaType;
import discordbot.utils.media.VideoTypes;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;

public class Converter {

	private final FFmpegBuilder builder;
	private String outputPath;
	private File file;
	
	private static final String OUTPUT_BUILDER = "src/main/resources/cache/convert_output.";
	
	public Converter(File file) throws IOException {
		this.file = file;
		this.builder = new FFmpegBuilder()
				.overrideOutputFiles(true)
				.setInput(Info.FFPROBE.probe(file.getPath()));
	}
	
	public void convert(MediaType type) throws IOException, UnsupportedAudioFileException, IllegalMediaFormatException {
		this.outputPath = OUTPUT_BUILDER + type.getExtension();
		if (type instanceof VideoTypes && MediaType.findFormat(FilenameUtils.getExtension(this.file.getPath())) instanceof VideoTypes) {
			
			String codec = "";
			switch ((VideoTypes)type) {
			case MP4 -> codec = "libx264";
			case WEBM -> codec = "libvpx";
			case GIF -> codec = "gif";
			default -> codec = "libx264";
			}
			FFmpegOutputBuilder config = this.builder.addOutput(this.outputPath);
			config.setFormat(type.getExtension()).setTargetSize(4_000_000).disableSubtitle().setAudioChannels(2)
					.setAudioCodec("opus").setAudioSampleRate(48_000).setAudioBitRate(32768).setVideoCodec(codec)
					.setVideoFrameRate(40, 1);

			FFmpegBuilder finished = config.done();
			Info.FFMPEG_EXECUTOR.createTwoPassJob(finished).run();
			
		} else if (type instanceof AudioTypes && MediaType.findFormat(FilenameUtils.getExtension(this.file.getPath())) instanceof AudioTypes) {
			Type format = null;
			switch ((AudioTypes)type) {
			case AIFC -> format = Type.AIFC;
			case WAV -> format = Type.WAVE;
			case AIFF -> format = Type.AIFF;
			default -> format = Type.WAVE;
			}
			AudioInputStream ais = AudioSystem.getAudioInputStream(this.file);
			ais.reset();
			AudioSystem.write(ais, format, getOutputFile());
			ais.close();
		} else if (type instanceof ImageTypes && MediaType.findFormat(FilenameUtils.getExtension(this.file.getPath())) instanceof ImageTypes) {
			
			
			
		} else throw new IllegalMediaFormatException("Cannot find a media type related to file extension.");
	}
	
	public File getOutputFile() {
		return new File(this.outputPath);
	}
}