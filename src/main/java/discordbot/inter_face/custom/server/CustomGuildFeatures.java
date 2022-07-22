package discordbot.inter_face.custom.server;

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
import discordbot.core.audio.PlayerManager;
import discordbot.core.command.Command;
import discordbot.core.event.BasicEventRegistry;
import discordbot.utils.Functions;
import discordbot.utils.Registrable;
import net.dv8tion.jda.api.entities.Guild;

public class CustomGuildFeatures implements Registrable {
	
	private List<Command> customCommands;
	private List<BasicEventRegistry> events;
	private Guild guild;
	
	//scope workarounds
	private String eventActionSelector = null;
	private String eventActionValue = null;
	private int randomChance = 0;
	
	public CustomGuildFeatures(Guild guild) throws IOException {
		
		this.guild = guild;
		
		GuildController.REGISTERED_SERVERS.put(guild, new File("src/main/resources/custom_interface/" + guild.getId() + ".json"));
		
		JsonReader mainReader = Json.createReader(new FileInputStream(GuildController.REGISTERED_SERVERS.get(guild)));
		
		JsonObject main = mainReader.readObject();
		JsonObject commands = main.getJsonObject("commands");
		//JsonObject events = main.getJsonObject("events");
		
		List<Command> customCommands = new ArrayList<>();;
		
		if (commands != null) {
			commands.forEach((name, val) -> {
				JsonObject selector = commands.getJsonObject(name);
				selector.forEach((action, value) -> {
					switch (action) {
						case "message" -> {
							final Command cmd = new Command(name, event -> {
								if (isGuild(event.getGuild())) {
									Functions.Messages.sendMessage(event.getChannel(), ((JsonString) value).getString());
								}
							});
							customCommands.add(cmd);
							break;
						}
						case "sfx" -> {
							final Command cmd = new Command(name, event -> {
								if (isGuild(event.getGuild())) {
									if (event.getMember().getVoiceState().inAudioChannel()) {
										event.getGuild().getAudioManager().openAudioConnection(event.getMember().getVoiceState().getChannel());
										PlayerManager.getInstance().load_play(event, ((JsonString) value).getString().replace("shorts/", "watch?v="));
									} else {
										Functions.Messages.sendEmbeded(event.getChannel(),
												Functions.Messages.errorEmbed(event.getMessage(), "User is not connected to a voice channel."));
									}
								}
							});
							customCommands.add(cmd);
							break;
						}
					}
				});
			});
		}
	
		List<BasicEventRegistry> customEvents = new ArrayList<>();
		
		/*if (events != null) {
			events.forEach((name0, val0) -> {
				JsonObject selector = events.getJsonObject(name0);
				selector.forEach((name1, val1) -> {
					JsonObject event = selector.getJsonObject(name1);
					switch (name1) {
						case "random" -> {
							JsonObject random = event.getJsonObject("random");
							random.forEach((name2, val2) -> {
								if (val2 instanceof JsonString) {
									this.eventActionSelector = name2;
									this.eventActionValue = ((JsonString) val2).getString();
								} else if (val2 instanceof JsonNumber) {
									this.randomChance = ((JsonNumber) val2).intValue();
								}
							});
							switch (this.eventActionSelector) {
								case "message" -> {
									final RandomEvent rnd = new RandomEvent(this.randomChance, msgEvent -> {
										if (isGuild(msgEvent.getGuild())) {
											Functions.Messages.sendMessage(msgEvent.getChannel(), this.eventActionValue);
										}
									});
									customEvents.add(rnd);
								}
							}
						}
					}
				});
			});
		}*/
		
		mainReader.close();
		
		this.customCommands = customCommands;
		this.events = customEvents;
	}
	
	public CustomGuildFeatures(String id) throws IOException {
		this(Element119.mainJDA.getGuildById(id));
	}
	
	@Override
	public void register() {
		for (Command cmd : this.customCommands) {
			cmd.register();
		}
		for (BasicEventRegistry ber : this.events) {
			ber.register();
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
	
	private boolean isGuild(Guild g) {
		return g.equals(this.guild);
	}
	
}