package androidassistanthelper.jdubss.androidassistanthelper.Service.MapsService.Implementation;

import android.os.AsyncTask;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import androidassistanthelper.jdubss.androidassistanthelper.Domain.Maps.MapsDTO;
import androidassistanthelper.jdubss.androidassistanthelper.Domain.Spotify.SpotifyDTO;
import androidassistanthelper.jdubss.androidassistanthelper.Service.MapsService.Implementation.MapsServiceImplementation;

/**
 * ON HOLD
 */
public class MapsWebServiceHelper extends AsyncTask<MapsDTO, Void, MapsDTO> {

    @Override
    protected MapsDTO doInBackground(MapsDTO... mapsDTO) {
        MapsDTO dto = mapsDTO[0];
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity responseEntity = null;
        responseEntity = restTemplate.postForEntity(dto.getMapsUrl(), HttpMethod.GET, String.class);

        String json = (String) responseEntity.getBody();

        JsonObject jsonObject = null;

        if(json != null)
            jsonObject = (JsonObject) new JsonParser().parse(json);
        else
            jsonObject = new JsonObject();

        dto.setJsonObject(jsonObject);

        return dto;
    }

    @Override
    protected void onPostExecute(MapsDTO mapsDTO) {
        Method responseMethod = mapsDTO.getResponseMethod();
        try {
            responseMethod.invoke(new MapsServiceImplementation(), mapsDTO);
        } catch (IllegalAccessException e) {
            //TODO handle exception
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            //TODO handle exception
            e.printStackTrace();
        }
    }
}
