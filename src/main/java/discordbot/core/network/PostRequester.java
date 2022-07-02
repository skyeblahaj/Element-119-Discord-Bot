package discordbot.core.network;

import java.io.Closeable;
import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class PostRequester implements Closeable {

	private CloseableHttpClient client;
	private HttpPost post;
	
	public PostRequester(String uri) {
		this.client = HttpClients.createDefault();
		this.post = new HttpPost(uri);
	}
	
	public CloseableHttpResponse post(String send, Header... headers) throws IOException {
		this.post.setEntity(new StringEntity(send));
		for (Header h : headers) {
			this.post.setHeader(h);
		}
		return client.execute(this.post);
	}
	
	@Override
	public void close() throws IOException {
		this.client.close();
	}
}