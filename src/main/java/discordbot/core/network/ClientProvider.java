package discordbot.core.network;

import java.io.Closeable;
import java.io.IOException;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public abstract class ClientProvider implements Closeable {

	protected CloseableHttpClient client;
	
	protected ClientProvider() {
		this.client = HttpClients.createDefault();
	}
	
	@Override
	public void close() throws IOException {
		this.client.close();
	}
}