package discordbot.core.command;

import discordbot.inter_face.debug.ManualControl;
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
	
	public PermissionCommand(String name, CommandAction action, String help, Permission... perms) {
		super(name, action, help);
		this.perms = perms;
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getAuthor().isBot()) return;
		boolean msgBool = Functions.Utils.startsWithIgnoreCase(event.getMessage().getContentRaw(), Info.PREFIX + this.name);
		boolean isAllowed = true;
		if (msgBool) {
			for (Permission p : this.perms) {
				if (!event.getMember().hasPermission(p)) {
					isAllowed = false;
					break;
				}
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