package androidassistanthelper.jdubss.androidassistanthelper.Service.SpeechTranslationService.internal;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;

import androidassistanthelper.jdubss.androidassistanthelper.Domain.SpeechTranslator.SpeechTranslatorDTO;
import androidassistanthelper.jdubss.androidassistanthelper.Service.SpeechTranslationService.ISpeechTranslator;
import androidassistanthelper.jdubss.androidassistanthelper.Utility.ConstantSpeech;

/**
 * Helper class to hand the translation of text to Speech
 */
public class SpeechTranslatorImpl implements ISpeechTranslator, TextToSpeech.OnInitListener {

    private final String ERROR_TAG = "SpeechTranslatorImpl";
    private Boolean isInitialized = Boolean.FALSE;
    private TextToSpeech mTts;
    private final Queue<SpeechTranslatorDTO> pendingSpeech = new LinkedList<>();

    public SpeechTranslatorImpl(Context context) {
        this.mTts = new TextToSpeech(context, this);
    }

    @Override
    public void onInit(int i) {

        if(i == TextToSpeech.SUCCESS) {
            int result = mTts.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                isInitialized = Boolean.FALSE;
                Log.e(ERROR_TAG, "This Language is not Supported");

            }else{
                isInitialized = Boolean.TRUE;

                while(!pendingSpeech.isEmpty())
                    convertTextToSpeech(pendingSpeech.poll());
            }

        }else
            Log.e(ERROR_TAG, "Error Initializing TTS engine");
    }

    public void resume(Context context){
        mTts = new TextToSpeech(context, this);
    }

    public void pause(){

        if(mTts != null) {
            isInitialized = Boolean.FALSE;
            mTts.stop();
            mTts.shutdown();
        }
    }

    public void convertTextToSpeech(SpeechTranslatorDTO speechDTO) {
        Integer status = null;


        if(isInitialized.equals(Boolean.TRUE)) {

            String speech = speechDTO.getSpeech();

            if (speech == null || speech.equals(""))
                status = mTts.speak(ConstantSpeech.MISSED_THAT, TextToSpeech.QUEUE_FLUSH, null);
            else
                status = mTts.speak(speech, TextToSpeech.QUEUE_FLUSH, null);

            while (mTts.isSpeaking()) {
                //DO NOTHING WHILE SPEEKING
            }

            if (status.compareTo(0) == -1)
                mTts.speak(ConstantSpeech.ERROR, TextToSpeech.QUEUE_FLUSH, null);

            else if (speechDTO.getNeedsAiService())
                speechDTO.getAiService().startListening();

            TextView speechToTextView = speechDTO.getSpeechToTextView();
            speechToTextView.setText(speech);

        }else
            pendingSpeech.add(speechDTO);
    }
}