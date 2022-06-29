package discordbot.utils.media;

import javax.annotation.Nonnull;

public interface MediaType {

	@Nonnull
	public String getExtension();
	
	public static MediaType findFormat(String extension) {
		for (VideoTypes vType : VideoTypes.values()) {
			if (vType.getExtension().equals(extension)) return vType;
		}
		for (AudioTypes aType : AudioTypes.values()) {
			if (aType.getExtension().equals(extension)) return aType;
		}
		for (ImageTypes iType : ImageTypes.values()) {
			if (iType.getExtension().equals(extension)) return iType;
		}
		return null;
	}
}