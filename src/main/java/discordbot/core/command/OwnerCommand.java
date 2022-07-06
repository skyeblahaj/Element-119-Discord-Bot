package discordbot.core.command;

import discordbot.utils.Functions;
import discordbot.utils.Info;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class OwnerCommand extends Command {

	public OwnerCommand(String name, CommandAction action) {
		super(name, action);
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		boolean msgBool = Functions.Utils.startsWithIgnoreCase(event.getMessage().getContentRaw(), Info.PREFIX + this.name);
		if (msgBool && event.getAuthor().getId().equals(Info.OWNER_ID)) {
			this.action.action(event);
		} else if (msgBool) {
			Functions.Messages.sendEmbeded(event.getChannel(),
					Functions.Messages.errorEmbed(event.getMessage(), "User does not have access to use this command."));
		}
	}
	
}
