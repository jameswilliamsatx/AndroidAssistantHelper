package androidassistanthelper.jdubss.androidassistanthelper.Config.Dagger;

import android.app.Application;

import androidassistanthelper.jdubss.androidassistanthelper.Config.Dagger.Modules.AppModule;
import androidassistanthelper.jdubss.androidassistanthelper.Config.Dagger.Modules.AssistantHelperModule;

public class InitializeDagger extends Application{

    private AssistantHelperComponent mAssistantComponent;

    @Override
    public void onCreate(){
        super.onCreate();


        mAssistantComponent = DaggerAssistantHelperComponent.builder()
                .appModule(new AppModule(this))
                .assistantHelperModule(new AssistantHelperModule())
                .build();

    }

    public AssistantHelperComponent getmAssistantComponent() {
        return mAssistantComponent;
    }
}
