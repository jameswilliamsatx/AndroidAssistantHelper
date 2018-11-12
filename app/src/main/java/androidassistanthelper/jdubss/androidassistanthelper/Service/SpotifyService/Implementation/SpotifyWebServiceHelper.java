package androidassistanthelper.jdubss.androidassistanthelper.Service.SpotifyService.Implementation;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidassistanthelper.jdubss.androidassistanthelper.Domain.Spotify.SpotifyDTO;
import androidassistanthelper.jdubss.androidassistanthelper.Utility.ConstantSpeech;
import androidassistanthelper.jdubss.androidassistanthelper.Utility.SpotifyWebString;

/**
 * This class consists necessary web service calls
 *
 * We started with one AsyncTask and migrated to Individual methods for
 * Web Service Calls. This affords much more flexibility
 */
public class SpotifyWebServiceHelper {


    /**
     * This is called from another Async Tack so no need for Async Task
     * This method will get the Spotify information for the Logged in User
     *
     * @param spotifyDTO
     * @return
     */
    public SpotifyDTO findSpotifyUserObjectByAccessToken(SpotifyDTO spotifyDTO) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(new MediaType[]{MediaType.APPLICATION_JSON}));
        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.add("Authorization", "Bearer " + spotifyDTO.getAccessToken());

        HttpEntity<String> request = null;
        ResponseEntity responseEntity = null;

        request = new HttpEntity<String>(headers);
        responseEntity = restTemplate.exchange(spotifyDTO.getUrl(), HttpMethod.GET, request, String.class);

        String json = (String) responseEntity.getBody();

        JsonObject jsonObject = null;

        if (json != null)
            jsonObject = (JsonObject) new JsonParser().parse(json);
        else
            jsonObject = new JsonObject();

        spotifyDTO.setJsonObject(jsonObject);

        return spotifyDTO;
    }

    /**
     * This method creates a playlist for a given Spotify User
     * If the playlist already exists a playlist will not be created and
     * an Error message is given
     */
    public static class CreatePlaylist extends AsyncTask<SpotifyDTO, Void, Void> {

        @Override
        protected Void doInBackground(SpotifyDTO... spotifyDTOS) {
            StringBuilder speech = new StringBuilder();
            SpotifyDTO dto = spotifyDTOS[0];
            String playlistName = (String) dto.getData().get("name");
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(new MediaType[]{MediaType.APPLICATION_JSON}));
            headers.setContentType(MediaType.APPLICATION_JSON);

            headers.add("Authorization", "Bearer " + dto.getAccessToken());

            HttpEntity<String> request = null;
            ResponseEntity responseEntity = null;

            request = new HttpEntity<String>(headers);

            Boolean playlistExists = playlisExists(playlistName, request, dto) != null;

            if (!playlistExists) {
                createPlaylist(playlistName, headers, dto);
                speech.append(dto.getResult().getFulfillment().getSpeech());
            } else
                speech.append(ConstantSpeech.PLAYLIST_ALREADY_EXIST.replace("{playlistName}", playlistName));

            dto.getExecutionTranslationCallback().speekResult(speech.toString());

            return null;
        }
    }

    /**
     * This method will add a song to a playlist. If this or currently playing is the name
     * of the song we will get the currently playing song that is within the Spotify Account
     * currently playing Service
     */
    public static class AddSong extends AsyncTask<SpotifyDTO, Void, Void> {

        @Override
        protected Void doInBackground(SpotifyDTO... spotifyDTOS) {

            StringBuilder speech = new StringBuilder();
            SpotifyDTO dto = spotifyDTOS[0];
            String artistName = (String) dto.getData().get("artistName");
            String playlistName = (String) dto.getData().get("playlistName");

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(new MediaType[]{MediaType.APPLICATION_JSON}));
            headers.setContentType(MediaType.APPLICATION_JSON);

            headers.add("Authorization", "Bearer " + dto.getAccessToken());

            HttpEntity<String> request = null;
            ResponseEntity responseEntity = null;

            request = new HttpEntity<String>(headers);

            //Query for the Song
            responseEntity = restTemplate.exchange(dto.getUrl(), HttpMethod.GET, request, String.class);

            String json = (String) responseEntity.getBody();

            JsonObject jsonObject = null;

            if (json != null) {
                jsonObject = (JsonObject) new JsonParser().parse(json);

                String spotifyURI = null;

                for (JsonElement track : jsonObject.get("tracks").getAsJsonObject().get("items").getAsJsonArray()) {
                    if (artistName == null)
                        spotifyURI = track.getAsJsonObject().get("uri").getAsString();

                    else {
                        if (artistName.equalsIgnoreCase(track.getAsJsonObject().get("artists").getAsString()))
                            spotifyURI = track.getAsJsonObject().get("uri").getAsString();
                    }
                }

                if (spotifyURI != null) {
                    String playlistId = playlisExists(playlistName, request, dto);
                    Boolean created = Boolean.FALSE;

                    if (playlistId == null) {
                        playlistId = createPlaylist(playlistName, headers, dto);

                        //Error condition exit method
                        if (playlistId == null) {
                            speech.append(ConstantSpeech.ERROR);
                            dto.getExecutionTranslationCallback().speekResult(speech.toString());
                            return null;
                        }
                    }

                    StringBuilder addSongUrl = new StringBuilder()
                            .append(SpotifyWebString.SPOTIFY_PLAYLIST_BASE_URI)
                            .append(SpotifyWebString.TRACKS.replace("{playlist_id}", playlistId));


                    List<String> uris = Arrays.asList(new String[]{spotifyURI});
                    Map<String, Object> data = new HashMap<>();
                    data.put("uris", uris);

                    request = new HttpEntity<String>(new Gson().toJson(data), headers);
                    responseEntity = restTemplate.exchange(addSongUrl.toString(), HttpMethod.POST, request, String.class);

                    if (responseEntity.getBody() != null)
                        speech.append(dto.getResult().getFulfillment().getSpeech());

                } else
                    speech.append(ConstantSpeech.SONG_NOT_FOUND.replace("{songName}", (String) dto.getData().get("songName")));

            } else
                speech.append(ConstantSpeech.SONG_NOT_FOUND.replace("{songName}", (String) dto.getData().get("songName")));

            dto.getExecutionTranslationCallback().speekResult(speech.toString());

            return null;
        }
    }

    /**
     * Helper method to determine if a playlist Exists
     *
     * @param playlistName
     * @param request
     * @param spotifyDTO
     * @return
     */
    private static String playlisExists(String playlistName, HttpEntity<String> request, SpotifyDTO spotifyDTO) {
        RestTemplate restTemplate = new RestTemplate();

        StringBuilder url = new StringBuilder()
                .append(SpotifyWebString.SPOTIRY_USER_BASE_URI)
                .append(SpotifyWebString.PLAYLISTS.replace(SpotifyWebString.USER_ID, spotifyDTO.getUserId()));

        //Query for the Playlists
        ResponseEntity responseEntity = restTemplate.exchange(url.toString(), HttpMethod.GET, request, String.class);

        String playlistId = null;

        String playlistString = (String) responseEntity.getBody();
        JsonObject playlistJson = null;
        JsonArray playlistArray = null;

        if (playlistString != null)
            playlistJson = (JsonObject) new JsonParser().parse(playlistString);

        playlistArray = playlistJson.getAsJsonArray("items");

        for (JsonElement p : playlistArray) {
            if (p.getAsJsonObject().get("name").getAsString().equals(playlistName))
                playlistId = p.getAsJsonObject().get("id").getAsString();
        }

        return playlistId;
    }

    /**
     * Helper method to create a Playlist
     *
     * @param playlistName
     * @param headers
     * @param spotifyDTO
     * @return
     */
    private static String createPlaylist(String playlistName, HttpHeaders headers, SpotifyDTO spotifyDTO) {

        RestTemplate restTemplate = new RestTemplate();

        StringBuilder url = new StringBuilder()
                .append(SpotifyWebString.SPOTIRY_USER_BASE_URI)
                .append(SpotifyWebString.PLAYLISTS.replace(SpotifyWebString.USER_ID, spotifyDTO.getUserId()));


        Map<String, Object> data = new HashMap<>();
        data.put("name", playlistName);

        HttpEntity<String> request = new HttpEntity<String>(new Gson().toJson(data), headers);
        ResponseEntity responseEntity = restTemplate.exchange(url.toString(), HttpMethod.POST, request, String.class);

        String json = (String) responseEntity.getBody();

        String playlistId = null;

        if (json != null)
            playlistId = ((JsonObject) new JsonParser().parse(json)).get("id").getAsString();

        return playlistId;
    }
}