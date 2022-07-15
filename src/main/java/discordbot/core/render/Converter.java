package discordbot.core.render;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.io.FilenameUtils;

import de.sciss.jump3r.Main;
import discordbot.utils.Functions;
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
	
	public void convert(MediaType type, @Nullable AudioFormat formatOverride, @Nullable File outputOverride) throws IOException, UnsupportedAudioFileException, IllegalMediaException {
		
		this.outputPath = outputOverride == null ? OUTPUT_BUILDER + type.getExtension() : outputOverride.getPath();
		
		if (type instanceof VideoTypes && MediaType.findFormat(FilenameUtils.getExtension(this.file.getPath())) instanceof VideoTypes) { //video -> video
			
			String codec;
			String audioCodec;
			switch ((VideoTypes)type) {
			case MP4 -> {
				codec = "libx264";
				audioCodec = "aac";
			}
			case WEBM -> {
				codec = "libvpx";
				audioCodec = "opus";
			}
			case GIF -> {
				codec = "gif";
				audioCodec = "aac"; //cant set null, stops code
			}
			default -> {
				codec = "libx264";
				audioCodec = "mp3";
			}
			}
			FFmpegOutputBuilder config = this.builder.addOutput(this.outputPath);
			config.setFormat(type.getExtension()).setTargetSize(4_000_000).disableSubtitle().setAudioChannels(2)
					.setAudioCodec(audioCodec).setAudioSampleRate(48_000).setAudioBitRate(128_000).setVideoCodec(codec)
					.setVideoFrameRate(40, 1);

			FFmpegBuilder finished = config.done();
			Info.FFMPEG_EXECUTOR.createTwoPassJob(finished).run();
			
		} else if (type instanceof AudioTypes && MediaType.findFormat(FilenameUtils.getExtension(this.file.getPath())) instanceof AudioTypes) { //audio -> audio
			Type format = null;
			boolean mp3 = false;
			switch ((AudioTypes)type) {
			case AIFC -> format = Type.AIFC;
			case WAV -> format = Type.WAVE;
			case AIFF -> format = Type.AIFF;
			case MP3 -> mp3 = true;
			default -> mp3 = true;
			}
			if (mp3) {
				
				toMP3(this.file, getOutputFile());
				
			} else {
				AudioInputStream ais = AudioSystem.getAudioInputStream(this.file);
				if (formatOverride != null) {
					AudioInputStream ais$new = AudioSystem.getAudioInputStream(formatOverride, ais);
					AudioSystem.write(ais$new, format, (outputOverride == null) ? getOutputFile() : outputOverride);
					ais$new.close();
				} else {
					ais.reset();
					AudioSystem.write(ais, format, (outputOverride == null) ? getOutputFile() : outputOverride);
				}
				ais.close();
			}
		} else if (type instanceof ImageTypes && MediaType.findFormat(FilenameUtils.getExtension(this.file.getPath())) instanceof ImageTypes) { //image -> image
			
			BufferedImage img = ImageIO.read(this.file);
			ImageIO.write(img, type.getExtension(), getOutputFile());
			
		} else if (type instanceof AudioTypes && MediaType.findFormat(FilenameUtils.getExtension(this.file.getPath())) instanceof VideoTypes) { //video -> audio
			String dir = Functions.Utils.readToken(new File("src/main/resources/private/ffmpeg.prv")).replace('/', '\\');
			String[] cmdArgs = {dir.substring(0, dir.length() - 10) + "ffmpeg.exe", "-y", "-i", this.file.getAbsolutePath(), 
					(Functions.Utils.readToken(new File("src/main/resources/private/dir.prv")) + this.outputPath).replace('/', '\\')};
			for (String s : cmdArgs) System.out.println(s);
			try {
				new ProcessBuilder(cmdArgs).start().waitFor();
			} catch (Exception e) {e.printStackTrace();}
			
		} else throw new IllegalMediaException("Cannot find a media type related to file extension.");
	}
	
	private static void toMP3(File source, File output) throws IOException {
		String[] cmdArgs = {"--preset", "standard", "-q", "0", "-m", "s",
				source.getAbsolutePath(), output.getAbsolutePath()};
		new Main().run(cmdArgs);
	}
	
	/*private static void fromMP3(File source, File output) throws IOException, UnsupportedAudioFileException {
		AudioInputStream raw = AudioSystem.getAudioInputStream(source);
		AudioFormat sourceFormat = raw.getFormat();
		AudioFormat outputFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
				sourceFormat.getSampleRate(), 16, sourceFormat.getChannels(),
				sourceFormat.getChannels() * 2, sourceFormat.getSampleRate(), false);
		
		final AudioInputStream sourceStream = AudioSystem.getAudioInputStream(outputFormat, raw);
		final AudioInputStream streamer = AudioSystem.getAudioInputStream(sourceStream);
	}*/
	
	public File getOutputFile() {
		return new File(this.outputPath);
	}
}