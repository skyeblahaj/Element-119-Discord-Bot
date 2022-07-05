package discordbot.core.download;

import com.sapher.youtubedl.DownloadProgressCallback;
import com.sapher.youtubedl.YoutubeDL;
import com.sapher.youtubedl.YoutubeDLException;
import com.sapher.youtubedl.YoutubeDLRequest;
import com.sapher.youtubedl.YoutubeDLResponse;


public class Downloader {

	private YoutubeDLRequest req;
	
	public Downloader(String url) {
		YoutubeDLRequest req = new YoutubeDLRequest(url, "C:/Program Files (x86)/Youtube-DL/");
		req.setOption("ignore-errors");
		req.setOption("output", "src/main/resources/cache/ydl_output");
		req.setOption("retries", 3);
		this.req = req;
	}
	
	public YoutubeDLResponse execute(DownloadProgressCallback callback) throws YoutubeDLException {
		return YoutubeDL.execute(this.req, callback);
	}
	
	public YoutubeDLResponse execute() throws YoutubeDLException {
		return execute(null);
	}
}