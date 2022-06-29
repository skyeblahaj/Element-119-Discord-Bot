package discordbot.utils.media;

public enum ImageTypes implements MediaType {

	PNG, JPG, WEBP, JPEG, BMP, GIF, EMPTY;
	
	private final String extension;
	
	ImageTypes(){
		this.extension = this.name().toLowerCase();
	}

	@Override
	public String getExtension() {
		return this.extension;
	}
	
}