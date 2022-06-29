package discordbot.utils.media;

public enum VideoTypes implements MediaType {

	MP4, THREEGP("3gp"), WEBM, MOV, WMV, AVI, FLV, M4A, GIF;

	private final String extension;

	VideoTypes(){
		this.extension = this.name().toLowerCase();
	}
	
	VideoTypes(String override){
		this.extension = override;
	}

	@Override
	public String getExtension() {
		return this.extension;
	}
}