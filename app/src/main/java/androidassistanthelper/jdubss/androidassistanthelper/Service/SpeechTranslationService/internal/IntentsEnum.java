package androidassistanthelper.jdubss.androidassistanthelper.Service.SpeechTranslationService.internal;

/**
 * Intents enum maps dialogflow intents to an Enum Value
 */
public enum IntentsEnum {


    DEFAULT_WELCOME("input.welcome", "Default Welcome Intent from DialogFlow"),
    CREATE_PLAYLIST("create_playlist", "Create a playlist for Given Name"),
    ADD_SONG("add_song", "Add a song to playlist"),
    FASTEST_ROUTE("fastest_route", "Give the fastest route by Road name for Given Location");


    private String intent;
    private String description;

    IntentsEnum(String intent, String description) {
        this.intent = intent;
        this.description = description;
    }


    public static IntentsEnum resolve(String intent) {
        for (IntentsEnum ie : IntentsEnum.values()) {
            if (ie.intent.equals(intent))
                return ie;
        }
        return null;
    }


    public String getDescription() {
        return description;
    }


}
