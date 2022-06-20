package discordbot.log;

import discordbot.utils.ConsoleColors;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Logging extends ListenerAdapter {

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		System.out.println(ConsoleColors.CYAN + event.getAuthor().getId() + ConsoleColors.BLACK + "\n---\n" + ConsoleColors.RESET +
						ConsoleColors.GREEN + event.getChannel().toString() + ConsoleColors.BLACK + "\n---\n" + ConsoleColors.RESET +
						ConsoleColors.WHITE + event.getMessage().getContentRaw() + '\n');
	}
	
}
