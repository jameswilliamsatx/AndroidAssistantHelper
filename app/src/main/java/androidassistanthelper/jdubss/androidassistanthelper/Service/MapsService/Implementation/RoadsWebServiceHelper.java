package androidassistanthelper.jdubss.androidassistanthelper.Service.MapsService.Implementation;

import android.os.AsyncTask;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import androidassistanthelper.jdubss.androidassistanthelper.Domain.Maps.MapsDTO;
import androidassistanthelper.jdubss.androidassistanthelper.Domain.Maps.RoadsDTO;

/**
 * On HOld
 */
public class RoadsWebServiceHelper extends AsyncTask<MapsDTO, Void, MapsDTO>{

    @Override
    protected MapsDTO doInBackground(MapsDTO... mapsDTO) {
        MapsDTO mapDTO = mapsDTO[0];
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity responseEntity = null;
        responseEntity = restTemplate.postForEntity(mapDTO.getRoadsUrl(), HttpMethod.GET, String.class);

        String json = (String) responseEntity.getBody();

        JsonObject jsonObject = null;

        if(json != null)
            jsonObject = (JsonObject) new JsonParser().parse(json);
        else
            jsonObject = new JsonObject();


        List<RoadsDTO> roadsDTO = new ArrayList<>();



        return mapDTO;
    }
}
