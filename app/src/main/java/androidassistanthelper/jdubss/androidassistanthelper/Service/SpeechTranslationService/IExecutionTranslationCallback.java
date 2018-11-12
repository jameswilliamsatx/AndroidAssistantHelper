package androidassistanthelper.jdubss.androidassistanthelper.Service.SpeechTranslationService;

import androidassistanthelper.jdubss.androidassistanthelper.Domain.Spotify.SpotifyDTO;

/**
 * Interface for Activity to speek the results of
 * an Webservice Call
 */
public interface IExecutionTranslationCallback {

    public void speekResult(String speech);
}
