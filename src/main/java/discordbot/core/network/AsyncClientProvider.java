package discordbot.core.network;

import java.io.Closeable;
import java.io.IOException;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;

public abstract class AsyncClientProvider implements Closeable {

	protected final AsyncHttpClient client;
	
	protected AsyncClientProvider() {
		this.client = new DefaultAsyncHttpClient();
	}
	
	@Override
	public void close() throws IOException {
		this.client.close();
	}
	
}