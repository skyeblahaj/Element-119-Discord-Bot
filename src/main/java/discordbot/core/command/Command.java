package discordbot.core.command;

import discordbot.core.event.BasicEventRegistry;
import discordbot.inter_face.ManualControl;
import discordbot.utils.Info;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Command extends BasicEventRegistry {
	
	protected String name;
	protected CommandAction action;
	
	public Command(String name, CommandAction action) {
		this.name = name;
		this.action = action;
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getMessage().getContentRaw().startsWith(Info.PREFIX + this.name) && ManualControl.commandToggle) this.action.action(event);
	}
	
	public String getName() {
		return this.name;
	}
	
}