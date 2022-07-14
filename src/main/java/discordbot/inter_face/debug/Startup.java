package discordbot.inter_face.debug;

import discordbot.core.event.BasicEventRegistry;
import net.dv8tion.jda.api.events.ReadyEvent;

public class Startup extends BasicEventRegistry {

	@Override
	public void onReady(ReadyEvent event) {
		
		System.out.println("---START OF STARTUP DEBUG---");
		System.out.println("---END OF STARTUP DEBUG---");
	}
}