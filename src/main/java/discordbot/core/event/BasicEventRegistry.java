package discordbot.core.event;

import discordbot.Element119;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class BasicEventRegistry extends ListenerAdapter {

	public final void register() {
		Element119.mainJDA.addEventListener(this);
	}
	
}