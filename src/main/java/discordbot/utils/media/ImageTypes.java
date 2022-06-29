package discordbot.utils.media;

public enum ImageTypes {

	PNG, JPG, WEBP, JPEG, BMP, GIF(true);
	
	private final boolean isAnimated;
	
	ImageTypes() {
		this.isAnimated = false;
	}
	
	ImageTypes(boolean anim){
		this.isAnimated = anim;
	}
	
}