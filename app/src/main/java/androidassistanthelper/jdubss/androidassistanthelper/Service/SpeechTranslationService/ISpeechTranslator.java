package androidassistanthelper.jdubss.androidassistanthelper.Service.SpeechTranslationService;

import android.content.Context;

import androidassistanthelper.jdubss.androidassistanthelper.Domain.SpeechTranslator.SpeechTranslatorDTO;

/**
 * Interface for converting Text to Speech
 */
public interface ISpeechTranslator {

    public void resume(Context context);

    public void pause();

    public void convertTextToSpeech(SpeechTranslatorDTO speechDTO);

}