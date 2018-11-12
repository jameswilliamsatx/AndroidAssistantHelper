package androidassistanthelper.jdubss.androidassistanthelper.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import androidassistanthelper.jdubss.androidassistanthelper.Config.Dagger.InitializeDagger;
import androidassistanthelper.jdubss.androidassistanthelper.R;
import androidassistanthelper.jdubss.androidassistanthelper.Service.AssistantUsersService.IAssistantUsersBusiness;
import androidassistanthelper.jdubss.androidassistanthelper.Utility.Constants;

/**
 * Forcing Google and Spotify Logins
 *
 * Normally would not do this and give a choice of
 * which services a user would like to use but in
 * the development of this forcing Google / Spotify Logins
 */
public class LoginActivity extends Activity implements View.OnClickListener{


    private final String LOGIN_TAG = "Google Login Tag";

    private static final Integer RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;
    private IAssistantUsersBusiness usersBusiness;

    @Inject
    public void setUsersBusiness(IAssistantUsersBusiness usersBusiness){
        this.usersBusiness = usersBusiness;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        findViewById(R.id.sign_in_google_button).setOnClickListener(this);
        findViewById(R.id.spotify_sign_in_button).setOnClickListener(this);

        ((InitializeDagger)getApplication()).getmAssistantComponent().inject(this);


        Boolean loggedInSpotify = getIntent().getExtras().get(Constants.LOGGED_INTO_SPOTIFY) != null
                && (Boolean)getIntent().getExtras().get(Constants.LOGGED_INTO_SPOTIFY);
        Boolean loggedIntoGoogle = getIntent().getExtras().get(Constants.LOGGED_INTO_GOOGLE) != null
                && (Boolean)getIntent().getExtras().get(Constants.LOGGED_INTO_GOOGLE);

        if(loggedIntoGoogle && !loggedInSpotify){
            hideGoogleLogin();
        }else {

            //Sign in with Google is required as of now
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    //.requestScopes()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_google_button:
                signInWithGoogle();
                break;
            case R.id.spotify_sign_in_button:
                //This would most likely be a webservice
                //i would create instead of using the spotify android
                //auth library
                signInWithSpotify();
        }
    }

    private void signInWithSpotify() {
        Intent intent = new Intent(this, SpotifyLoginActivity.class);
        startActivity(intent);
    }

    public void signInWithGoogle(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            insertOrUpdateUserFromAccount(account);
            SharedPreferences sp = getSharedPreferences(MainActivity.ASSISTANT_SHARED_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(Constants.LOGGED_INTO_GOOGLE, Boolean.TRUE);
            editor.commit();
            hideGoogleLogin();

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(LOGIN_TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void hideGoogleLogin() {
        com.google.android.gms.common.SignInButton googleLogInButton = findViewById( R.id.sign_in_google_button);
        googleLogInButton.setVisibility(View.INVISIBLE);

        Button logInToSpotifyButton = findViewById(R.id.spotify_sign_in_button);
        logInToSpotifyButton.setVisibility(View.VISIBLE);

        TextView view = findViewById(R.id.welcomeText);
        view.setText(R.string.please_log_in_to_spotify);
    }

    private void insertOrUpdateUserFromAccount(GoogleSignInAccount account) {
        usersBusiness.insertOrUpdateUserFromGoogleAccount(account);
    }
}
