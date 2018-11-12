package androidassistanthelper.jdubss.androidassistanthelper.Service.AssistantUsersService.Implementation;

import android.app.Application;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import androidassistanthelper.jdubss.androidassistanthelper.Domain.AssistantUser.Internal.AssistantUser;
import androidassistanthelper.jdubss.androidassistanthelper.Domain.AssistantUser.Internal.AssistantUserRepository;
import androidassistanthelper.jdubss.androidassistanthelper.Service.AssistantUsersService.IAssistantUsersBusiness;

public class AssistantUsersBusinessImplementation implements IAssistantUsersBusiness {

    private AssistantUserRepository mRepository;

    public AssistantUsersBusinessImplementation(AssistantUserRepository assistantUserRepository){
        this.mRepository = assistantUserRepository;
    }

    public void insertOrUpdateUserFromGoogleAccount( GoogleSignInAccount account){
        mRepository.insertOrUpdateUserFromGoogleAccount(account);
    }

    @Override
    public void updateSpotifyInformation(AssistantUser assistantUser){
        mRepository.updateSpotifyInformation(assistantUser);
    }

    @Override
    public void insertOrUpdateUser(AssistantUser assistantUser) {
        mRepository.insertOrUpdateUser(assistantUser);
    }

    @Override
    public void refreshCurrentUser() {
        mRepository.refreshCurrentUser();
    }

    public AssistantUser getCurrentUser() {
        return mRepository.getmCurrentUser();
    }

    public Boolean isUserCurrentlyLoggedIn(){
        return mRepository.isUserCurrentlyLoggedIn();
    }


}
