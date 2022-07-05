package discordbot.inter_face.custom;

import java.util.HashMap;
import java.util.Map;

public class GuildController {

	public static final Map<String, Integer> REGISTERED_SERVERS = new HashMap<>();
	
	private static int index = 0;
	
	public static void addCustomServer(String guildID) {
		REGISTERED_SERVERS.put(guildID, index);
		index++;
	}
	
	public static boolean isRegistered(String key) {
		return REGISTERED_SERVERS.containsKey(key);
	}
}