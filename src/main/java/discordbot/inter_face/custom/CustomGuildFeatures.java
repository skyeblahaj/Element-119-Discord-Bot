package discordbot.inter_face.custom;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;

import discordbot.Element119;
import discordbot.core.command.Command;
import discordbot.utils.Functions;
import discordbot.utils.Registrable;
import net.dv8tion.jda.api.entities.Guild;

public class CustomGuildFeatures implements Registrable {
	
	private List<Command> customCommands;
	private Guild guild;
	
	public CustomGuildFeatures(Guild guild) throws IOException {
		
		this.guild = guild;
		
		GuildController.REGISTERED_SERVERS.put(guild, new File("src/main/resources/custom_interface/" + guild.getId() + ".json"));
		
		JsonReader mainReader = Json.createReader(new FileInputStream(GuildController.REGISTERED_SERVERS.get(guild)));
		
		JsonObject main = mainReader.readObject();
		JsonObject commands = main.getJsonObject("commands");
		//JsonObject events = main.getJsonObject("events"); //coming soon
		
		List<Command> customCommands = new ArrayList<>();;
		
		commands.forEach((name, val) -> {
			JsonObject selector = commands.getJsonObject(name);
			selector.forEach((action, value) -> {
				switch (action) {
					case "message" -> {
						final Command cmd = new Command(name, event -> {
							if (event.getGuild().equals(guild)) {
								Functions.Messages.sendMessage(event.getChannel(), ((JsonString) value).getString());
							}
						});
						customCommands.add(cmd);
						break;
					}
				}
			});
		});
		mainReader.close();
		
		this.customCommands = customCommands;
	}
	
	public CustomGuildFeatures(String id) throws IOException {
		this(Element119.mainJDA.getGuildById(id));
	}
	
	@Override
	public void register() {
		for (Command cmd : this.customCommands) {
			cmd.register();
		}
		GuildController.CUSTOM_GUILD_COMMANDS.put(this.guild, this.customCommands);
		GuildController.FEATURES.put(this.guild, this);
	}
	
	@Override
	public void deregister() {
		for (Command cmd : this.customCommands) {
			cmd.deregister();
		}
		GuildController.CUSTOM_GUILD_COMMANDS.remove(this.guild);
		GuildController.REGISTERED_SERVERS.remove(this.guild);
		GuildController.FEATURES.remove(this.guild);
	}
}