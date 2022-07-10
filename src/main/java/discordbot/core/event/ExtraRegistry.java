package discordbot.core.event;

import java.util.HashMap;
import java.util.Map;

import discordbot.core.audio.AutoDisconnect;
import discordbot.log.Logging;
import discordbot.utils.BusPassenger;
import discordbot.utils.RegistryBus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

@BusPassenger
public class ExtraRegistry {
	private ExtraRegistry() {}
	
	public static final Map<Guild, Member> BOT_MEMBER = new HashMap<>();
	
	@RegistryBus
	public static void registry() {
		
		System.out.println("---START OF EXTRA REGISTRIES---");
		
		new Logging().register();
		new AutoDisconnect().register();
		
		System.out.println("---END OF EXTRA REGISTRIES---");
	}
}