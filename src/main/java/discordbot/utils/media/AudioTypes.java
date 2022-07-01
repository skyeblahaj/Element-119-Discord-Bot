package discordbot.utils.media;

public enum AudioTypes implements MediaType {

	MP3, OGG, WAV, OPUS, M4P, AIFC, AIFF;

	private final String extension;
	
	AudioTypes(){
		this.extension = this.name().toLowerCase();
	}
	
	@Override
	public String getExtension() {
		return this.extension;
	}
}