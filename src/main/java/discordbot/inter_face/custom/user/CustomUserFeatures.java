package discordbot.inter_face.custom.user;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonValue;

import discordbot.utils.Registrable;
import net.dv8tion.jda.api.entities.User;

public class CustomUserFeatures implements Registrable {
	
	private final User user;
	private final JsonReader mainReader;
	
	public CustomUserFeatures(User user) throws IOException {
		this.user = user;
		UserController.USER_DATA.put(user, new File("src/main/resources/custom_interface/user/" + user.getId() + ".json"));
		
		this.mainReader = Json.createReader(new FileInputStream(UserController.USER_DATA.get(user)));
		
	}
	
	public JsonValue getData(String search) {
		return this.mainReader.readObject().getValue(search);
	}

	@Override
	public void register() {
		UserController.FEATURES.put(this.user, this);
	}

	@Override
	public void deregister() {
		this.mainReader.close();
		UserController.USER_DATA.remove(this.user);
		UserController.FEATURES.remove(this.user);
	}
	
}