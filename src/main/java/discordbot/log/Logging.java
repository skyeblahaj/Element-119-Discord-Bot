package discordbot.log;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Logging extends ListenerAdapter {

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		System.out.println(event.getAuthor().getId() + "\n---\n" +
						event.getChannel().toString() + "\n---\n" +
						event.getMessage().getContentRaw() + '\n');
	}
	
}
