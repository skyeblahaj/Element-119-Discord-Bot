package discordbot.inter_face.custom.user;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import discordbot.Element119;
import discordbot.utils.BusPassenger;
import discordbot.utils.RegistryBus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

@BusPassenger
public final class UserController {

	public static final Map<User, CustomUserFeatures> FEATURES = new HashMap<>();
	public static final Map<User, File> USER_DATA = new HashMap<>();
	
	@RegistryBus
	public static void bootAll() {
		for (Guild g : Element119.mainJDA.getGuilds()) {
			for (Member m : g.getMembers()) {
				User u = m.getUser();
				if (new File("src/main/resources/custom_interface/user/" + u.getId() + ".json").exists()) {
					try {
						new CustomUserFeatures(u).register();
					} catch (IOException e) {e.printStackTrace();}
					System.out.println(u.toString() + " custom features registered.");
				}
			}
		}
	}
	
}