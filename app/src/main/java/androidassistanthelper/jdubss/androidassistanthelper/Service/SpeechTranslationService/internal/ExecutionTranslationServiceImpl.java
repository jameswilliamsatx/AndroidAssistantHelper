package androidassistanthelper.jdubss.androidassistanthelper.Service.SpeechTranslationService.internal;

import android.util.Log;

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ai.api.model.Result;
import androidassistanthelper.jdubss.androidassistanthelper.Domain.AssistantUser.Internal.AssistantUser;
import androidassistanthelper.jdubss.androidassistanthelper.Service.AssistantUsersService.IAssistantUsersBusiness;
import androidassistanthelper.jdubss.androidassistanthelper.Service.SpeechTranslationService.IExecutionTranslationCallback;
import androidassistanthelper.jdubss.androidassistanthelper.Service.SpeechTranslationService.IExecutionTranslationService;
import androidassistanthelper.jdubss.androidassistanthelper.Service.MapsService.IMapsService;
import androidassistanthelper.jdubss.androidassistanthelper.Service.SpotifyService.ISpotifyWebService;

/**
 * ExecutionTranslationServiceImpl will map an Intent String
 * Returned from Dialogflow Request to the Business Layery method
 * that will execute functionality within Android Applicaiton
 */
public class ExecutionTranslationServiceImpl implements IExecutionTranslationService{

    private final String TAG = "TranslationService";

    private IAssistantUsersBusiness usersBusiness;
    private IMapsService mapsService;
    private ISpotifyWebService spotifyService;

    public ExecutionTranslationServiceImpl(IAssistantUsersBusiness usersBusiness, IMapsService mapsService, ISpotifyWebService spotifyService) {
        this.usersBusiness = usersBusiness;
        this.mapsService = mapsService;
        this.spotifyService = spotifyService;
    }

    @Override
    public Boolean translateAndExecute(Result result, IExecutionTranslationCallback executionTranslationCallback) {
        Boolean success = Boolean.TRUE;
        AssistantUser user = usersBusiness.getCurrentUser();
        IntentsEnum action = IntentsEnum.resolve(result.getAction());

        try{

            switch (action){

                case DEFAULT_WELCOME:
                    break;

                case CREATE_PLAYLIST: spotifyService.createPlaylist(user, result, executionTranslationCallback);
                    break;

                case ADD_SONG:  spotifyService.addSong(user, result, executionTranslationCallback);

                    break;

                case FASTEST_ROUTE: mapsService.findFastestRouteHomeFromWork();
                    break;
            }
        }catch(Exception e){
            Log.w(TAG, "Error translating method from result \\n" + e.getMessage());
            success = Boolean.FALSE;
        }

        return success;
    }
}
