package androidassistanthelper.jdubss.androidassistanthelper.Config.Dagger;


import javax.inject.Singleton;

import androidassistanthelper.jdubss.androidassistanthelper.Activity.LoginActivity;
import androidassistanthelper.jdubss.androidassistanthelper.Activity.MainActivity;
import androidassistanthelper.jdubss.androidassistanthelper.Activity.SpotifyLoginActivity;
import androidassistanthelper.jdubss.androidassistanthelper.Activity.SpotifyRefreshTokenActivity;
import androidassistanthelper.jdubss.androidassistanthelper.Config.Dagger.Modules.AppModule;
import androidassistanthelper.jdubss.androidassistanthelper.Config.Dagger.Modules.AssistantHelperModule;
import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, AssistantHelperModule.class})
public interface AssistantHelperComponent {

    public void inject(MainActivity activity);
    public void inject(SpotifyLoginActivity activity);
    public void inject(SpotifyRefreshTokenActivity activity);
    public void inject(LoginActivity activity);

}
