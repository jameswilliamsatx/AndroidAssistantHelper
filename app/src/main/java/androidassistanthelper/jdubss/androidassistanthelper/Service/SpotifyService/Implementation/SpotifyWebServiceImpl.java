package androidassistanthelper.jdubss.androidassistanthelper.Service.SpotifyService.Implementation;

import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import ai.api.model.Result;
import androidassistanthelper.jdubss.androidassistanthelper.Domain.AssistantUser.Internal.AssistantUser;
import androidassistanthelper.jdubss.androidassistanthelper.Domain.Spotify.SpotifyDTO;
import androidassistanthelper.jdubss.androidassistanthelper.Service.SpeechTranslationService.IExecutionTranslationCallback;
import androidassistanthelper.jdubss.androidassistanthelper.Service.SpotifyService.ISpotifyWebService;
import androidassistanthelper.jdubss.androidassistanthelper.Utility.SpotifyWebString;


public class SpotifyWebServiceImpl implements ISpotifyWebService {

    private static final String THIS = "this";
    private static final String CURRENTLY = "current";

    /**
     * Find Spotify User Information from Users Access Token
     * @param accessToken
     * @return
     */
    public JsonObject findSpotifyUserObjectByAccessToken(String accessToken) {
        SpotifyDTO dto = new SpotifyDTO(SpotifyWebString.SPOTIFY_ME_BASE_URI, accessToken, null, null);
        dto = new SpotifyWebServiceHelper().findSpotifyUserObjectByAccessToken(dto);
        return dto.getJsonObject();
    }

    /**
     * Create a Playlist with the Given Name
     * If the playlist Already exists give an error message
     * @param assistantUser
     * @param result
     * @param executionTranslationCallback
     */
    public void createPlaylist(AssistantUser assistantUser, Result result, IExecutionTranslationCallback executionTranslationCallback){

        StringBuilder url = new StringBuilder()
                .append(SpotifyWebString.SPOTIRY_USER_BASE_URI)
                .append(SpotifyWebString.PLAYLISTS.replace(SpotifyWebString.USER_ID, assistantUser.getUserId()));

        SpotifyDTO dto = new SpotifyDTO(url.toString(), assistantUser.getSpotifyAccessToken(), assistantUser.getUserId(), executionTranslationCallback);

        Map<String, Object> data = new HashMap<>();

        data.put("name", result.getParameters().get("playlistName").getAsString());

        dto.setData(data);
        dto.setResult(result);
        dto.setExecutionTranslationCallback(executionTranslationCallback);

        new SpotifyWebServiceHelper.CreatePlaylist().execute(dto);
    }

    /**
     * Add a song to a Playlist. If the song name is this or contains currently
     * then add the song from the Currently Playing web service for the current
     * User If an Artist name is not Given we default the limit to one and just
     * set the first. If the Artis name is not given we limit to ten and iterate
     * to make sure we get a song by that Artist
     *
     * @param assistantUser
     * @param result
     * @param executionTranslationCallback
     */
    public void addSong(AssistantUser assistantUser, Result result, IExecutionTranslationCallback executionTranslationCallback) {

        StringBuilder url = new StringBuilder();

        HashMap<String, JsonElement> parameters = result.getParameters();

        String songName = parameters.get("songName").getAsString();
        String playlistName = parameters.get("playlistName").getAsString();
        String artistName = parameters.get("artistName") != null ? parameters.get("artistName").getAsString() : null;

        if(songName.contains(THIS) || songName.contains(CURRENTLY)){
            url.append(SpotifyWebString.SPOTIFY_ME_BASE_URI)
                    .append(SpotifyWebString.CURRENTLY_PLAYING);
        }else{
            url.append(SpotifyWebString.SPOTIFY_SEARCH_BASE_URI);

            StringBuilder q = new StringBuilder()
                    .append("q=")
                    .append(songName)
                    .append("&type=track");

            if(artistName == null)
                q.append("&limit=1");

            else
                q.append("&limit=10");

            url.append(q.toString());
        }

        SpotifyDTO dto = new SpotifyDTO(url.toString(), assistantUser.getSpotifyAccessToken(), assistantUser.getUserId(), executionTranslationCallback);

        Map<String, Object> data = new HashMap<>();

        data.put("songName", songName);
        data.put("playlistName", playlistName);
        data.put("artistName", artistName);

        dto.setData(data);
        dto.setResult(result);
        dto.setExecutionTranslationCallback(executionTranslationCallback);

        new SpotifyWebServiceHelper.AddSong().execute(dto);
    }
}