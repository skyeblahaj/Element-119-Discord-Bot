package discordbot.core.command;

import discordbot.core.event.actions.MessageReceivedAction;
import discordbot.inter_face.debug.ManualControl;
import discordbot.utils.Functions;
import discordbot.utils.Info;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

public class PermissionCommand extends Command {

	private Permission[] perms;
	
	public PermissionCommand(String name, MessageReceivedAction action, Permission... perms) {
		this(name, action, null, perms);
	}
	
	public PermissionCommand(String name, MessageReceivedAction action, String help, Permission... perms) {
		this(name, action, help, false, perms);
	}
	
	public PermissionCommand(String name, MessageReceivedAction action, String help, boolean debug, Permission...perms) {
		super(name, action, help, debug);
		this.perms = perms;
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		try {
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
			boolean owner = event.getAuthor().getId().equals(Info.OWNER_ID);
			if (ManualControl.overrideToggle && owner) { //debug in servers where i miss perms (vulnerable to exploiting if the bot has permissions, so owners will need to trust me until bot is out of heavy development)
				isAllowed = true;
			}
			if (msgBool && isAllowed && ManualControl.commandToggle && !this.debug) this.action.action(event);
			else if (msgBool && this.debug && owner) {
				this.action.action(event);
			}
			else if (msgBool) {
				Functions.Messages.sendEmbeded(event.getChannel(),
						Functions.Messages.errorEmbed(event.getMessage(), "User does not have access to use this command."));
			}
		} catch (InsufficientPermissionException e) {
			System.err.println("Bot does not have permission to send messages in channel \"" + event.getChannel().toString() + "\"");
		}
	}
	
	public Permission[] getPerms() {
		return this.perms;
	}
}