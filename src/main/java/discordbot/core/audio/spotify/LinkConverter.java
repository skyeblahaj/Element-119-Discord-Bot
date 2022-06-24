package discordbot.core.audio.spotify;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hc.core5.http.ParseException;

import discordbot.utils.Functions;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.ClientCredentials;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import se.michaelthelin.spotify.requests.data.playlists.GetPlaylistRequest;
import se.michaelthelin.spotify.requests.data.tracks.GetTrackRequest;

public class LinkConverter {

	private SpotifyApi api;
	private String id, type;

	private static final File ID = new File("src/main/resources/private/spotify_id.prv"),
			SECRET = new File("src/main/resources/private/spotify_secret.prv");

	public LinkConverter() {
		try {
			initSpotify();
		} catch (ParseException | SpotifyWebApiException | IOException e) {
			e.printStackTrace();
		}
	}

	private void initSpotify() throws ParseException, SpotifyWebApiException, IOException {
		this.api = new SpotifyApi.Builder().setClientId(Functions.Utils.readToken(ID))
				.setClientSecret(Functions.Utils.readToken(SECRET)).build();

		ClientCredentialsRequest.Builder req = new ClientCredentialsRequest.Builder(this.api.getClientId(),
				this.api.getClientSecret());
		ClientCredentials creds = req.grant_type("client_credentials").build().execute();
		this.api.setAccessToken(creds.getAccessToken());
	}

	public List<String> convert(String link) throws ParseException, SpotifyWebApiException, IOException {
		
		String[] firstSplit = link.split("/"),
				 secondSplit;
		
		if (firstSplit.length > 5) {
			secondSplit = firstSplit[6].split("\\?");
			this.type = firstSplit[5];
		} else {
			secondSplit = firstSplit[4].split("\\?");
			this.type = firstSplit[3];
		}
		this.id = secondSplit[0];
		
		List<String> listOfTracks = new ArrayList<>();
		
		if (type.contentEquals("track")) {
			listOfTracks.add(getArtistAndName(id));
			return listOfTracks;
		}
		
		if (type.contentEquals("playlist")) {
			GetPlaylistRequest playlistRequest = api.getPlaylist(id).build();
			Playlist playlist = playlistRequest.execute();
			Paging<PlaylistTrack> playlistPaging = playlist.getTracks();
			PlaylistTrack[] playlistTracks = playlistPaging.getItems();
			
			for (PlaylistTrack i : playlistTracks) {
				Track track = (Track) i.getTrack();
				String trackID = track.getId();
				listOfTracks.add(getArtistAndName(trackID));
			}
			
			return listOfTracks;
		}
		return null;
	}

	private String getArtistAndName(String trackID) throws ParseException, SpotifyWebApiException, IOException {
		String artistNameAndTrackName = "";
		GetTrackRequest trackRequest = api.getTrack(trackID).build();

		Track track = trackRequest.execute();
		artistNameAndTrackName = track.getName() + " - ";

		ArtistSimplified[] artists = track.getArtists();
		for (ArtistSimplified i : artists) {
			artistNameAndTrackName += i.getName() + " ";
		}

		return artistNameAndTrackName;
	}
}