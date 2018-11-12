package androidassistanthelper.jdubss.androidassistanthelper.Domain.SpeechTranslator;

import android.widget.TextView;

import ai.api.android.AIService;

public class SpeechTranslatorDTO {

    private String speech;
    private AIService aiService;
    private Boolean needsAiService;
    private TextView speechToTextView;

    public SpeechTranslatorDTO(String speech, AIService aiService, Boolean needsAiService, TextView speechToTextView) {
        this.speech = speech;
        this.aiService = aiService;
        this.needsAiService = needsAiService;
        this.speechToTextView=speechToTextView;
    }

    public String getSpeech() {
        return speech;
    }

    public void setSpeech(String speech) {
        this.speech = speech;
    }

    public AIService getAiService() {
        return aiService;
    }

    public void setAiService(AIService aiService) {
        this.aiService = aiService;
    }

    public Boolean getNeedsAiService() {
        return needsAiService;
    }

    public void setNeedsAiService(Boolean needsAiService) {
        this.needsAiService = needsAiService;
    }


    public TextView getSpeechToTextView() {
        return speechToTextView;
    }

    public void setSpeechToTextView(TextView speechToTextView) {
        this.speechToTextView = speechToTextView;
    }

}
