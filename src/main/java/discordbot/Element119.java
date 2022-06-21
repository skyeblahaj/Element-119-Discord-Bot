package discordbot;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.security.auth.login.LoginException;

import discordbot.core.command.CommandRegistry;
import discordbot.utils.Functions;
import discordbot.utils.Info;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

public class Element119 {
	
	public static JDA mainJDA;
	
	public static void main(String args[]) throws LoginException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		mainJDA = JDABuilder.createDefault(Functions.Utils.readToken(new File("src/main/resources/private/jda_token.prv"))).build();
		System.out.println("Bot is configuring...");
		
		mainJDA.getPresence().setActivity(Activity.playing("Prefix: '" + Info.PREFIX + "'"));
		
		CommandRegistry.registerAll();
	}
	
}
