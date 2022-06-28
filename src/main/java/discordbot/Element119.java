package discordbot;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import javax.security.auth.login.LoginException;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import discordbot.utils.BusPassenger;
import discordbot.utils.Functions;
import discordbot.utils.Info;
import discordbot.utils.RegistryBus;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

public class Element119 {
	
	public static JDA mainJDA;
	
	public static void main(String args[]) throws LoginException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		mainJDA = JDABuilder.createDefault(Functions.Utils.readToken(new File("src/main/resources/private/jda_token.prv"))).build();
		System.out.println("Bot is configuring...");
		
		mainJDA.getPresence().setActivity(Activity.playing("Prefix: '" + Info.PREFIX + "'"));
		
		Reflections classTool = new Reflections(new ConfigurationBuilder()
				.setUrls(ClasspathHelper.forJavaClassPath())
				.setScanners(Scanners.MethodsAnnotated,
							 Scanners.TypesAnnotated));
		Set<Class<?>> busSubscribers = classTool.getTypesAnnotatedWith(BusPassenger.class);
		for (Class<?> c : busSubscribers) {
			for (Method m : c.getMethods()) {
				if (m.isAnnotationPresent(RegistryBus.class)) m.invoke(null);
			}
		}
		//System.err.println("\n--START OF LOGS--\n"); //err printstream for red color
	}
}