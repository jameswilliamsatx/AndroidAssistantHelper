package androidassistanthelper.jdubss.androidassistanthelper.Domain.Maps;

import com.google.gson.JsonObject;

import java.io.Serializable;

public class RoadsDTO implements Serializable {

    private static final long serialVersionUID = -1617152334373055296L;
    private String name;
    private Double latitude;
    private Double longitude;
    private Integer speedLimit;

    private JsonObject jsonObject;

    public RoadsDTO(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(Integer speedLimit) {
        this.speedLimit = speedLimit;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }
}
