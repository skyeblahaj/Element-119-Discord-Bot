package discordbot.core.event;

import discordbot.core.event.actions.MessageReceivedAction;
import discordbot.utils.Functions;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RandomEvent extends BasicEventRegistry {

	private MessageReceivedAction action;
	private final int chance;
	
	public RandomEvent(int chance, MessageReceivedAction event) {
		this.action = event;
		this.chance = chance;
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (Functions.RANDOM.nextInt(this.chance) == 0) {
			this.action.action(event);
		}
	}
	
}