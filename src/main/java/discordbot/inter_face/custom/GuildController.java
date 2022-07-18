package discordbot.inter_face.custom;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import discordbot.Element119;
import discordbot.core.event.BasicEventRegistry;
import discordbot.utils.BusPassenger;
import discordbot.utils.RegistryBus;
import net.dv8tion.jda.api.entities.Guild;

@BusPassenger
public class GuildController {

	public static final Map<Guild, File> REGISTERED_SERVERS = new HashMap<>();
	public static final Map<Guild, List<? extends BasicEventRegistry>> CUSTOM_GUILD_COMMANDS = new HashMap<>();
	public static final Map<Guild, CustomGuildFeatures> FEATURES = new HashMap<>();
	
	@RegistryBus
	public static void bootAll() {
		for (Guild g : Element119.mainJDA.getGuilds()) {
			if (new File("src/main/resources/custom_interface/" + g.getId() + ".json").exists()) {
				try {
					new CustomGuildFeatures(g).register();
					System.out.println(g.toString() + " custom features registered.");
				} catch (IOException e) {e.printStackTrace();}
			}
		}
	}
}