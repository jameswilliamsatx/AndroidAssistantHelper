package androidassistanthelper.jdubss.androidassistanthelper.Config.Dagger.Modules;

import android.app.Application;

import javax.inject.Singleton;

import androidassistanthelper.jdubss.androidassistanthelper.Domain.AssistantUser.Internal.AssistantUserRepository;
import androidassistanthelper.jdubss.androidassistanthelper.Service.AssistantUsersService.IAssistantUsersBusiness;
import androidassistanthelper.jdubss.androidassistanthelper.Service.AssistantUsersService.Implementation.AssistantUsersBusinessImplementation;
import androidassistanthelper.jdubss.androidassistanthelper.Service.SpeechTranslationService.IExecutionTranslationService;
import androidassistanthelper.jdubss.androidassistanthelper.Service.SpeechTranslationService.internal.ExecutionTranslationServiceImpl;
import androidassistanthelper.jdubss.androidassistanthelper.Service.MapsService.IMapsService;
import androidassistanthelper.jdubss.androidassistanthelper.Service.MapsService.Implementation.MapsServiceImplementation;
import androidassistanthelper.jdubss.androidassistanthelper.Service.SpotifyService.ISpotifyWebService;
import androidassistanthelper.jdubss.androidassistanthelper.Service.SpotifyService.Implementation.SpotifyWebServiceImpl;
import dagger.Module;
import dagger.Provides;

/**
 * Dagger2 Initializations
 */
@Module
public class AssistantHelperModule {

    public AssistantHelperModule(){}

    @Provides
    @Singleton
    public ISpotifyWebService spotifyWebService(){
        return new SpotifyWebServiceImpl();
    }

    @Provides
    @Singleton
    public AssistantUserRepository assistantUserRepository(Application application){
        return new AssistantUserRepository(application, spotifyWebService());
    }

    @Provides
    @Singleton
    public IAssistantUsersBusiness assistantUsersBusinessImplementation(AssistantUserRepository assistantUserRepository){
        return new AssistantUsersBusinessImplementation(assistantUserRepository);
    }


    @Provides
    @Singleton
    public IMapsService mapsServiceImplementation(){
        return new MapsServiceImplementation();
    }

    @Provides
    @Singleton
    public IExecutionTranslationService executionTranslationService(AssistantUserRepository assistantUserRepository){
        return new ExecutionTranslationServiceImpl(assistantUsersBusinessImplementation(assistantUserRepository), mapsServiceImplementation(), spotifyWebService());
    }
}
