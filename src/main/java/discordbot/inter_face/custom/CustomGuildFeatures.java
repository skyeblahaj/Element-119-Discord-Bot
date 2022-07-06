package discordbot.inter_face.custom;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.json.Json;
import javax.json.JsonObject;

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
		
		
		
	}
	
	public void register() {
		GuildController.addCustomServer(this.guild.getId());
	}
	
}