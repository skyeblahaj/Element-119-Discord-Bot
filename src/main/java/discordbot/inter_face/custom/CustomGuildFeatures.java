package discordbot.inter_face.custom;

import java.io.FileInputStream;
import java.io.IOException;

import javax.json.Json;
import javax.json.JsonObject;

import discordbot.Element119;
import discordbot.core.command.Command;
import discordbot.utils.Functions;
import net.dv8tion.jda.api.entities.Guild;

public class CustomGuildFeatures {
	
	public CustomGuildFeatures(Guild guild) throws IOException {
		
		GuildController.addCustomServer(guild.getId());
		
		JsonObject main = Json.createReader(new FileInputStream(GuildController.REGISTERED_SERVERS.get(guild))).readObject();
		JsonObject commands = main.getJsonObject("commands");
		//JsonObject events = main.getJsonObject("events");
		
		commands.forEach((name, val) -> {
			JsonObject selector = commands.getJsonObject(name);
			selector.forEach((action, value) -> {
				switch (action) {
					case "message" -> {
						final Command cmd = new Command(name, event -> {
							if (event.getGuild().equals(guild)) {
								Functions.Messages.sendMessage(event.getChannel(), value.toString().substring(1, value.toString().length() - 1));
							}
						});
						cmd.register();
						break;
					}
				}
			});	
		});
	}
	
	public CustomGuildFeatures(String id) throws IOException {
		this(Element119.mainJDA.getGuildById(id));
	}
}