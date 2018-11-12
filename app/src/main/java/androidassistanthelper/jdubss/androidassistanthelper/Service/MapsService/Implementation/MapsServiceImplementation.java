package androidassistanthelper.jdubss.androidassistanthelper.Service.MapsService.Implementation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import androidassistanthelper.jdubss.androidassistanthelper.Domain.Maps.MapsDTO;
import androidassistanthelper.jdubss.androidassistanthelper.Domain.Maps.RoadsDTO;
import androidassistanthelper.jdubss.androidassistanthelper.Service.MapsService.IMapsService;
import androidassistanthelper.jdubss.androidassistanthelper.Utility.API_KEY;
import androidassistanthelper.jdubss.androidassistanthelper.Utility.GoogleMapsString;
import androidassistanthelper.jdubss.androidassistanthelper.Utility.GoogleRoadsString;

/**
 * This has been put on hold.
 *
 * It seems to use the Speedlimits API you not only need
 * a premium google maps account but also need an Asset Tracking
 * lisence... That is completely paid. There are a couple of
 * ways to get this data but much larger job than a simple
 * web service call so holding off for now. There must be a way to get
 * this for free...
 *
 *
 **/
public class MapsServiceImplementation implements IMapsService {


    public void findFastestRouteHomeFromWork() throws NoSuchMethodException {
        String beginningLocation = "";
        String endingLocation = "";

        String url = getRoutQueryUrl(beginningLocation,endingLocation);
        MapsDTO mapsDTO = new MapsDTO();
        mapsDTO.setMapsUrl(url);
        Method m = this.getClass().getMethod("findFastestRoutHomeFromWork", MapsDTO.class);
        mapsDTO.setResponseMethod(m);
        new MapsWebServiceHelper().execute(mapsDTO);
    }

    public void findFastestRoutHomeFromWork(MapsDTO mapsDTO) {
        StringBuilder roadsForUrl = new StringBuilder();
        List<RoadsDTO> roads = findRoadsDTO(mapsDTO, roadsForUrl);
        String roadsUrl = getRoadsQueryUrl(roadsForUrl);
        mapsDTO.setRoadsUrl(roadsUrl);
        new RoadsWebServiceHelper().execute(mapsDTO);
    }

    private List<RoadsDTO>  findRoadsDTO(MapsDTO mapsDTO, StringBuilder roadsForUrl) {
        List<RoadsDTO> roads = new ArrayList<>();

        JsonArray routes = mapsDTO.getJsonObject().getAsJsonArray("routes");

        if(routes.size() > 0){

            JsonElement fastestRoute = routes.get(0);

            for(JsonElement leg : fastestRoute.getAsJsonObject().getAsJsonArray("legs")) {

                JsonArray steps = leg.getAsJsonObject().getAsJsonArray("steps");
                int count = 0;

                for (JsonElement step : steps) {

                    JsonElement latAndLon = step.getAsJsonObject().get("end_location");

                    Double endLat = latAndLon.getAsJsonObject().get("lat").getAsDouble();
                    Double endLng = latAndLon.getAsJsonObject().get("lng").getAsDouble();

                    roads.add(new RoadsDTO(endLat, endLng));

                    roadsForUrl
                            .append("path=")
                            .append(endLat)
                            .append(",")
                            .append(endLng);

                    if (count < steps.size() - 1)
                        roadsForUrl.append("|");

                    count++;
                }
            }
        }

        return roads;
    }

    private String getRoutQueryUrl(String startLocation, String endLocation) {
        StringBuilder url = new StringBuilder()
                .append(GoogleMapsString.GOOGLE_DIRECTIONS_BASE_URI)
                .append(GoogleMapsString.FORMAT)
                .append(GoogleMapsString.ORIGIN)
                .append(startLocation)
                .append(GoogleMapsString.DESTINATION)
                .append(endLocation)
                .append(API_KEY.GOOGLE_MAPS_API_KEY);

        return url.toString();
    }


    private String getRoadsQueryUrl(StringBuilder roadsForUrl) {
        StringBuilder url = new StringBuilder()
                .append(GoogleRoadsString.SPEED_LIMITS_BASE_URI)
                .append(roadsForUrl)
                .append(API_KEY.GOOGLE_ROADS_API_KEY);

        return url.toString();
    }
}
