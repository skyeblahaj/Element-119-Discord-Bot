package discordbot.core.download;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import discordbot.utils.Functions;

public class Downloader {
	
	private static Downloader downloaderInstance = null;
	
	private int downloadedAmount;
	private List<String> links;
	
	private Downloader() {
		this.downloadedAmount = 0;
		this.links = new ArrayList<>();
	}
	
	public void download(String url, String output) throws IOException {
		this.links.add(url);
		this.downloadedAmount++;
		String[] cmdArgs = {(Functions.Utils.readToken(
				new File("src/main/resources/private/yt-dlp.prv")) + "yt-dlp").replace('/', '\\'), url, "--force-overwrites", "-o",
				(Functions.Utils.readToken(new File("src/main/resources/private/dir.prv")) + output).replace('/', '\\'),
				"--socket-timeout", "60"};
		Functions.Debug.printArray(cmdArgs);
		Runtime.getRuntime().exec(cmdArgs);
	}
	
	public static Downloader getInstance() {
		if (downloaderInstance == null) {
			downloaderInstance = new Downloader();
		}
		return downloaderInstance;
	}
	
}