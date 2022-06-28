package discordbot.core.command;

import discordbot.inter_face.ManualControl;
import discordbot.utils.Functions;
import discordbot.utils.Info;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PermissionCommand extends Command {

	private Permission[] perms;
	
	public PermissionCommand(String name, CommandAction action, Permission... perms) {
		super(name, action);
		this.perms = perms;
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		boolean msgBool = event.getMessage().getContentRaw().startsWith(Info.PREFIX + this.name);
		boolean isAllowed = true;
		for (Permission p : this.perms) {
			if (!event.getMember().hasPermission(p)) {
				isAllowed = false;
				break;
			}
		}
		if (msgBool && isAllowed && ManualControl.commandToggle) this.action.action(event);
		else if (msgBool) {
			Functions.Messages.sendEmbeded(event.getChannel(),
					Functions.Messages.errorEmbed(event.getMessage(), "User does not have access to use this command."));
		}
	}
	
	public Permission[] getPerms() {
		return this.perms;
	}
}