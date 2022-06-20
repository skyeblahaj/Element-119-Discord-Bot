package discordbot.core.command;

import discordbot.Element119;
import discordbot.utils.Info;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Command extends ListenerAdapter {

	private String name;
	private CommandAction action;
	
	public Command(String name, CommandAction action) {
		this.name = name;
		this.action = action;
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getMessage().getContentRaw().equalsIgnoreCase(Info.PREFIX + this.name)) this.action.action(event);
	}
	
	public void register() {
		Element119.mainJDA.addEventListener(this);
	}
	
	public String getName() {
		return this.name;
	}
	
}