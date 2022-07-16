package discordbot.core.network;

import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.BoundRequestBuilder;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;

public class AsyncPostRequester extends AsyncClientProvider {

	private BoundRequestBuilder requestBuild;
	private Request request;
	
	public AsyncPostRequester(String uri) {
		this.requestBuild = this.client.preparePost(uri);
	}
	
	public void build(ContentType contentType, ContentType acceptType, String data) {
		this.request = this.requestBuild.setHeader("Content-Type", contentType.getAppName())
					.setHeader("Accept", acceptType.getAppName())
					.setBody(data)
					.build();
	}
	
	public void build(ContentType type, String data) {
		build(type, type, data);
	}
	
	public ListenableFuture<Integer> execute(){
		return this.client.executeRequest(this.request, new AsyncCompletionHandler<Integer>() {

			@Override
			public Integer onCompleted(Response response) throws Exception {
				return response.getStatusCode();
			}
			
		});
	}
	
}