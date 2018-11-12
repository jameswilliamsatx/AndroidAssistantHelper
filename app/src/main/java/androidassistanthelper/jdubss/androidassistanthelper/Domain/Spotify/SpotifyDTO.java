package androidassistanthelper.jdubss.androidassistanthelper.Domain.Spotify;

import android.view.View;

import com.google.gson.JsonObject;

import org.springframework.http.HttpMethod;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import ai.api.model.Result;
import androidassistanthelper.jdubss.androidassistanthelper.Service.SpeechTranslationService.IExecutionTranslationCallback;

public class SpotifyDTO implements Serializable{

    //Request items
    private String url;
    private String accessToken;
    private String userId;
    private JsonObject jsonObject;
    private Map<String, Object> data = new HashMap<>();


    private Result result;
    private IExecutionTranslationCallback executionTranslationCallback;


    public SpotifyDTO(String url, String accessToken, String userId, IExecutionTranslationCallback executionTranslationCallback) {
        this.url = url;
        this.accessToken = accessToken;
        this.userId = userId;
        this.executionTranslationCallback=executionTranslationCallback;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public IExecutionTranslationCallback getExecutionTranslationCallback() {
        return executionTranslationCallback;
    }

    public void setExecutionTranslationCallback(IExecutionTranslationCallback executionTranslationCallback) {
        this.executionTranslationCallback = executionTranslationCallback;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
