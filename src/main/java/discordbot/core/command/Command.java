package discordbot.core.command;

import discordbot.core.event.BasicEventRegistry;
import discordbot.core.event.actions.MessageReceivedAction;
import discordbot.inter_face.debug.ManualControl;
import discordbot.utils.Functions;
import discordbot.utils.Info;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

public class Command extends BasicEventRegistry {
	
	protected String name;
	protected String help;
	protected boolean debug;
	protected MessageReceivedAction action;
	
	public Command(String name, MessageReceivedAction action, String help, boolean debug) {
		this.name = name;
		this.action = action;
		this.help = help;
		this.debug = debug;
	}
	
	public Command(String name, MessageReceivedAction action, String help) {
		this(name, action, help, false);
	}
	
	public Command(String name, MessageReceivedAction action) {
		this(name, action, null);
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getAuthor().isBot()) return;
		boolean msgBool = Functions.Utils.startsWithIgnoreCase(event.getMessage().getContentRaw(), Info.PREFIX + this.name) && ManualControl.commandToggle;
		try {
			if (msgBool && !this.debug) {
				this.action.action(event);
			} else if (msgBool && this.debug && event.getAuthor().getId().equals(Info.OWNER_ID)) {
				this.action.action(event);
			}
		} catch (InsufficientPermissionException e) {
			System.err.println("Bot does not have permission to send messages in channel \"" + event.getChannel().toString() + "\"");
		}
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getHelp() {
		return this.help;
	}
	
	public boolean isDebug() {
		return this.debug;
	}
}