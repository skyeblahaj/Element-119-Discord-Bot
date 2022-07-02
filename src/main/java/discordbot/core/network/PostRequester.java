package discordbot.core.network;

import java.io.IOException;

import javax.annotation.Nonnull;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

public class PostRequester extends ClientProvider {
	
	private HttpPost post;
	
	public PostRequester(@Nonnull String uri) {
		super();
		this.post = new HttpPost(uri);
	}
	
	public CloseableHttpResponse post(String send, Header... headers) throws IOException {
		this.post.setEntity(new StringEntity(send));
		for (Header h : headers) {
			this.post.setHeader(h);
		}
		return client.execute(this.post);
	}
}