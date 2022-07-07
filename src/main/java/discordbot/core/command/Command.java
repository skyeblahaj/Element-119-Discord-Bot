package discordbot.core.command;

import discordbot.core.event.BasicEventRegistry;
import discordbot.inter_face.debug.ManualControl;
import discordbot.utils.Functions;
import discordbot.utils.Info;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Command extends BasicEventRegistry {
	
	protected String name;
	protected String help;
	protected CommandAction action;
	
	public Command(String name, CommandAction action, String help) {
		this.name = name;
		this.action = action;
		this.help = help;
	}
	
	public Command(String name, CommandAction action) {
		this(name, action, null);
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (Functions.Utils.startsWithIgnoreCase(event.getMessage().getContentRaw(), Info.PREFIX + this.name) && ManualControl.commandToggle) this.action.action(event);
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getHelp() {
		return this.help;
	}
}