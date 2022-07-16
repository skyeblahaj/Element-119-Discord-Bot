package discordbot.core.network;

public enum ContentType {

	JSON("application/json");
	
	private final String appName;
	
	ContentType(String appName) {
		this.appName = appName;
	}
	
	public String getAppName() {
		return this.appName;
	}
	
}