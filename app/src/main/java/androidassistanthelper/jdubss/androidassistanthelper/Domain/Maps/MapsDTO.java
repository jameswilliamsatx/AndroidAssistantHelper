package androidassistanthelper.jdubss.androidassistanthelper.Domain.Maps;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.List;

public class MapsDTO {


    //Google MapsDTO
    private String origin;
    private String destination;
    private String mapsUrl;
    private Method responseMethod;
    private JsonObject jsonObject;

    //Google Roads
    private String roadsUrl;

    private List<RoadsDTO> roadsDTOList;


    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Method getResponseMethod() {
        return responseMethod;
    }

    public void setResponseMethod(Method responseMethod) {
        this.responseMethod = responseMethod;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getMapsUrl() {
        return mapsUrl;
    }

    public void setMapsUrl(String mapsUrl) {
        this.mapsUrl = mapsUrl;
    }

    public String getRoadsUrl() {
        return roadsUrl;
    }

    public void setRoadsUrl(String roadsUrl) {
        this.roadsUrl = roadsUrl;
    }

    public List<RoadsDTO> getRoadsDTOList() {
        return roadsDTOList;
    }

    public void setRoadsDTOList(List<RoadsDTO> roadsDTOList) {
        this.roadsDTOList = roadsDTOList;
    }


}
