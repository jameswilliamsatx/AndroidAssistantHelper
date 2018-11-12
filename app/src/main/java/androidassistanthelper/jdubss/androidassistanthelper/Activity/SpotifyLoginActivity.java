package androidassistanthelper.jdubss.androidassistanthelper.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import javax.inject.Inject;

import androidassistanthelper.jdubss.androidassistanthelper.Config.Dagger.InitializeDagger;
import androidassistanthelper.jdubss.androidassistanthelper.Domain.AssistantUser.Internal.AssistantUser;
import androidassistanthelper.jdubss.androidassistanthelper.R;
import androidassistanthelper.jdubss.androidassistanthelper.Service.AssistantUsersService.IAssistantUsersBusiness;
import androidassistanthelper.jdubss.androidassistanthelper.Utility.API_KEY;
import androidassistanthelper.jdubss.androidassistanthelper.Utility.Constants;
import androidassistanthelper.jdubss.androidassistanthelper.Utility.SpotifyWebString;

/**
 * Log in user to Spotify. Will use spotify account if user has one
 */
public class SpotifyLoginActivity extends Activity
{

    private IAssistantUsersBusiness usersBusiness;

    @Inject
    public void setUsersBusiness(IAssistantUsersBusiness usersBusiness){
        this.usersBusiness = usersBusiness;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_spotify_login);
        ((InitializeDagger)getApplication()).getmAssistantComponent().inject(this);
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(API_KEY.SPOTIFY_CLIENT_ID, AuthenticationResponse.Type.TOKEN, SpotifyWebString.REDIRECT_URI);

        builder.setScopes(SpotifyWebString.SCOPES);

        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, API_KEY.REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        // Check if result comes from the correct activity
        if (requestCode == API_KEY.REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {

                case TOKEN:
                    String accessToken = response.getAccessToken();

                    AssistantUser currentUser = usersBusiness.getCurrentUser() != null ? usersBusiness.getCurrentUser() :
                            new AssistantUser();
                    currentUser.setSpotifyAccessToken(accessToken);
                    //currentUser.setSpotifyTokenExpires(response.getExpiresIn());
                    usersBusiness.updateSpotifyInformation(currentUser);

                    SharedPreferences sp = getSharedPreferences(MainActivity.ASSISTANT_SHARED_PREFS, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean(Constants.LOGGED_INTO_SPOTIFY, Boolean.TRUE);
                    editor.commit();

                    Intent mainIntent = new Intent(this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                    break;


                case ERROR:

                    //TODO there was an error during login handle

                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }

    public void goToSpotify(){

    }



/*
    @Override
    public void onLoggedIn() {
        Log.d("SpotifyActivity", "User logged in");
        Intent intent = new Intent(this, SpotifyActivity.class);
        intent.putExtra("ACCESS_TOKEN", accessToken);
        startActivity(intent);
    }

    @Override
    public void onLoggedOut() {
        Log.d("SpotifyActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Error var1) {
        Log.d("SpotifyActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("SpotifyActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("SpotifyActivity", "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {

    }

    @Override
    public void onPlaybackError(Error error) {

    }*/
}
