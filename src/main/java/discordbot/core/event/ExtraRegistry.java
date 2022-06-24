package discordbot.core.event;

import discordbot.core.audio.AutoDisconnect;
import discordbot.log.Logging;
import discordbot.utils.RegistryBus;

public class ExtraRegistry {
	private ExtraRegistry() {}
	
	@RegistryBus
	public static void registry() {
		new Logging().register();
		new AutoDisconnect().register();
	}
}
