package discordbot.inter_face.custom;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import discordbot.Element119;
import discordbot.utils.BusPassenger;
import discordbot.utils.RegistryBus;
import net.dv8tion.jda.api.entities.Guild;

@BusPassenger
public class GuildController {

	public static final Map<Guild, File> REGISTERED_SERVERS = new HashMap<>();
	
	public static void addCustomServer(String guildID) {
		addCustomServer(Element119.mainJDA.getGuildById(guildID));
	}
	
	public static void addCustomServer(Guild guild) {
		REGISTERED_SERVERS.put(guild, new File("src/main/resources/custom_interface/" + guild.getId() + ".json"));
	}
	
	public static void removeCustomServer(Guild guild) {
		REGISTERED_SERVERS.remove(guild);
	}
	
	@RegistryBus
	public static void bootAll() {
		for (Guild g : Element119.mainJDA.getGuilds()) {
			if (new File("src/main/resources/custom_interface/" + g.getId() + ".json").exists()) {
				try {
					new CustomGuildFeatures(g);
					System.out.println(g.getName() + " custom features registered.");
				} catch (IOException e) {e.printStackTrace();}
			}
		}
	}
}