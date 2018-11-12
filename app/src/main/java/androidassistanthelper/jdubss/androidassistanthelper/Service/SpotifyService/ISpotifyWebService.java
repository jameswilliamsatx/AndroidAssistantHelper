package androidassistanthelper.jdubss.androidassistanthelper.Service.SpotifyService;

import android.view.View;

import com.google.gson.JsonObject;

import ai.api.model.Result;
import androidassistanthelper.jdubss.androidassistanthelper.Domain.AssistantUser.Internal.AssistantUser;
import androidassistanthelper.jdubss.androidassistanthelper.Service.SpeechTranslationService.IExecutionTranslationCallback;

public interface ISpotifyWebService {

    public JsonObject findSpotifyUserObjectByAccessToken(String accessToken);

    public void createPlaylist(AssistantUser assistantUser, Result result, IExecutionTranslationCallback executionTranslationCallback);

    public void addSong(AssistantUser assistantUser, Result result, IExecutionTranslationCallback executionTranslationCallback);
}
