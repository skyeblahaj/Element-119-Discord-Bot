package discordbot.core.event.actions;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@FunctionalInterface
public interface MessageReceivedAction {

	public void action(MessageReceivedEvent e);
	
}