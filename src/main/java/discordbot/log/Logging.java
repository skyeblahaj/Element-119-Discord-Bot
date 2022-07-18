package discordbot.log;

import java.awt.Color;

import discordbot.core.event.BasicEventRegistry;
import discordbot.utils.Functions;
import discordbot.utils.Info;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Logging extends BasicEventRegistry {

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		System.out.println("\nMember: " + event.getAuthor().toString() + "\n---\nIn channel: " +
						event.getChannel().toString() + " --- In Server: " + event.getGuild().toString() + "\n---\n" +
						event.getMessage().getContentRaw() + '\n');
		
		//command exceptions
		switch (event.getMessage().getContentRaw()) {
		case Info.PREFIX -> Functions.Messages.sendEmbeded(event.getChannel(), 
				Functions.Messages.buildEmbed("Commands", new Color(0x0000ff), 
						new Field("To get started:", "Use '" + Info.PREFIX + "commands'", false)));
		case "bug" -> Functions.Messages.sendMessage(event.getChannel(), "Report bugs to: https://github.com/skyeblahaj/Element-119-Discord-Bot");
		}
	}
}