package discordbot.core.audio;

import discordbot.core.event.BasicEventRegistry;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;

public class AutoDisconnect extends BasicEventRegistry {

	@Override
	public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
		if (event.getGuild()
				.getAudioManager()
				.isConnected() && 
				event.getGuild()
				.getAudioManager()
				.getConnectedChannel()
				.getMembers()
				.size() <= 1) {
			
			event.getGuild().getAudioManager().closeAudioConnection();
			
		}
	}
	
}
