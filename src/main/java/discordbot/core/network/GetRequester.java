package discordbot.core.network;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;

public class GetRequester extends ClientProvider {

	private HttpGet get;
	
	public GetRequester(@Nonnull String uri) {
		super();
		this.get = new HttpGet(uri);
	}
	
	public CloseableHttpResponse get() throws IOException {
		return this.client.execute(this.get);
	}
}