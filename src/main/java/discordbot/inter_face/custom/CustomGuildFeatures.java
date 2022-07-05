package discordbot.inter_face.custom;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;

import discordbot.core.command.Command;
import discordbot.utils.Functions;
import net.dv8tion.jda.api.entities.Guild;

public class CustomGuildFeatures {

	private Guild guild;
	private File json;
	
	public CustomGuildFeatures(Guild guild, File json) throws IOException {
		this.guild = guild;
		this.json = json;
		
		JsonObject main = Json.createReader(new FileInputStream(json)).readObject();
		
		JsonObject commands = main.getJsonObject("commands");
		JsonObject events = main.getJsonObject("events");
		
		List<JsonValue> allCommands = new ArrayList<>();;
		commands.forEach((key, val) -> {
			allCommands.add(val);
			Command cmd = new Command(key, event -> {
				String str = val.asJsonObject().getString("message");
				Functions.Messages.sendMessage(event.getChannel(), str);
			});
			cmd.register();
		});
		
		
	}
	
	public void register() {
		GuildController.addCustomServer(this.guild.getId());
	}
	
}