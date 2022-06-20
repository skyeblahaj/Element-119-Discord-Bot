package discordbot.core.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@FunctionalInterface
public interface CommandAction {

	public void action(MessageReceivedEvent e);
	
}