package discordbot.core.command;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import discordbot.Element119;
import discordbot.log.Logging;
import discordbot.utils.Functions;
import discordbot.utils.Info;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.User;

public class CommandRegistry {
	
	public static final List<Command> COMMANDS = new ArrayList<>();
	
	private static Command register(String name, CommandAction action) {
		Command cmd = new Command(name, action);
		COMMANDS.add(cmd);
		return cmd;
	}
	
//////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static final Command INFORMATION = register("info", event -> {
		Functions.Messages.sendEmbeded(event.getChannel(),
				Functions.Messages.buildEmbed("Bot Information",
						new Color(0x42f598),
						new Field("Owner:", Info.OWNER_TAG, false),
						new Field("Language:", "Java", false)).setThumbnail(Element119.mainJDA.getSelfUser().getAvatarUrl()));
	});
	
	public static final Command HELP = register("help", event -> {
		MessageChannel c = event.getChannel();
		switch (Functions.RANDOM.nextInt(4)) {
		case 0 : Functions.Messages.sendMessage(c, "the walls are filled with j"); break;
		case 1 : Functions.Messages.sendMessage(c, "hello " + event.getAuthor().getAsTag()); break;
		case 2 : Functions.Messages.sendMessage(c, "مرحبا غروه الكرة لوغان"); break;
		case 3 : Functions.Messages.sendMessage(c, "burger credit card"); break;
		}
	});
	
	public static final Command AVATAR = register("avatar", event -> {
		String[] args = Functions.Messages.multiArgs(event.getMessage());
		if (args.length < 2) {
			Functions.Messages.sendEmbeded(event.getChannel(),
					Functions.Messages.buildEmbed(event.getMember().getNickname() + "'s Avatar",
					new Color(0xffffff),
					event.getMember().getEffectiveAvatarUrl()));
		} else if (Functions.Messages.isPinging(args[1])) {
			User pingedUser = event.getMessage().getMentionedUsers().get(0);
			Functions.Messages.sendEmbeded(event.getChannel(),
					Functions.Messages.buildEmbed(pingedUser.getName() + "'s Avatar",
					new Color(0xffffff),
					pingedUser.getEffectiveAvatarUrl()));
		} else if (args[1].length() > 0) {
			try {
				User pingedUser = event.getJDA().getUsersByName(args[1], true).get(0);
				Functions.Messages.sendEmbeded(event.getChannel(),
						Functions.Messages.buildEmbed(pingedUser.getName() + "'s Avatar",
						new Color(0xffffff),
						pingedUser.getEffectiveAvatarUrl()));
			} catch (IndexOutOfBoundsException e) {Functions.Messages.sendMessage(event.getChannel(), "Error: Could not grab avatar.");}
			
		} else Functions.Messages.sendMessage(event.getChannel(), "Error: Could not grab avatar.");
	});
	
//////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static void registerAll() {
		Element119.mainJDA.addEventListener(new Logging());
		for (Command cmd : COMMANDS) {
			cmd.register();
			System.out.println(cmd.getName() + " command is registered.");
		}
	}

}
