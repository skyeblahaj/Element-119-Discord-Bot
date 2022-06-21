package discordbot.log;

import java.awt.Color;

import discordbot.utils.Functions;
import discordbot.utils.Info;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Logging extends ListenerAdapter {

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		System.out.println(event.getAuthor().getId() + "\n---\n" +
						event.getChannel().toString() + "\n---\n" +
						event.getMessage().getContentRaw() + '\n');
		
		//command exceptions
		if (event.getMessage().getContentRaw().equals(Info.PREFIX)) {
			Functions.Messages.sendEmbeded(event.getChannel(), Functions.Messages.buildEmbed("Commands", new Color(0x0000ff), new Field("To get started:", "Use '" + Info.PREFIX + "commands'", false)));
		};
	}
	
}
