package discordbot.core.command;

import discordbot.core.event.actions.MessageReceivedAction;
import discordbot.utils.Functions;
import discordbot.utils.Info;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

public class OwnerCommand extends Command {

	public OwnerCommand(String name, MessageReceivedAction action) {
		super(name, action);
	}
	
	public OwnerCommand(String name, MessageReceivedAction action, String help) {
		super(name, action, help);
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		try {
			boolean msgBool = Functions.Utils.startsWithIgnoreCase(event.getMessage().getContentRaw(), Info.PREFIX + this.name);
			if (msgBool && event.getAuthor().getId().equals(Info.OWNER_ID)) {
				this.action.action(event);
			} else if (msgBool) {
				Functions.Messages.sendEmbeded(event.getChannel(),
						Functions.Messages.errorEmbed(event.getMessage(), "User does not have access to use this command."));
			}
		} catch (InsufficientPermissionException e) {
		System.err.println("Bot does not have permission to send messages in channel \"" + event.getChannel().toString() + "\"");
		}
	}	
}