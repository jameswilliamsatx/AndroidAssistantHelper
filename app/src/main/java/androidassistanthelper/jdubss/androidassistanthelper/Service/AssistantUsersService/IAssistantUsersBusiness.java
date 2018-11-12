package androidassistanthelper.jdubss.androidassistanthelper.Service.AssistantUsersService;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import androidassistanthelper.jdubss.androidassistanthelper.Domain.AssistantUser.Internal.AssistantUser;

public interface IAssistantUsersBusiness {

    public void insertOrUpdateUserFromGoogleAccount( GoogleSignInAccount account);
    public void insertOrUpdateUser(AssistantUser assistantUser);
    public void updateSpotifyInformation(AssistantUser assistantUser);
    public void refreshCurrentUser();
    public AssistantUser getCurrentUser();
    public Boolean isUserCurrentlyLoggedIn();
}
