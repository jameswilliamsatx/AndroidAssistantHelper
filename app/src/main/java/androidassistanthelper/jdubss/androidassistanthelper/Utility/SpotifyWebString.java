package androidassistanthelper.jdubss.androidassistanthelper.Utility;

public class SpotifyWebString {

    public static final String[] SCOPES = new String[]{"user-read-private", "streaming",
            "user-library-read", "user-library-modify",
            "playlist-read-private", "playlist-modify-public", "playlist-modify-private",
            "playlist-read-collaborative", "app-remote-control", "user-read-currently-playing"};

    public static final String REDIRECT_URI = "yourcustomprotocol://login";

    public static final String SPOTIFY_ME_BASE_URI = "https://api.spotify.com/v1/me";
    public static final String SPOTIRY_USER_BASE_URI = "https://api.spotify.com/v1/users/";
    public static final String SPOTIFY_SEARCH_BASE_URI = "https://api.spotify.com/v1/search?";
    public static final String SPOTIFY_PLAYLIST_BASE_URI = "https://api.spotify.com/v1/playlists";

    public static final String CURRENTLY_PLAYING = "/player/currently-playing";
    public static final String PLAYLISTS = "{user_id}/playlists";
    public static final String TRACKS = "/{playlist_id}/tracks";
    public static final String USER_ID = "{user_id}";
}




