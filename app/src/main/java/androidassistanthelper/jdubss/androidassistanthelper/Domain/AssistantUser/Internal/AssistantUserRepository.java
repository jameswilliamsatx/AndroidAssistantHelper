package androidassistanthelper.jdubss.androidassistanthelper.Domain.AssistantUser.Internal;

import android.app.Application;
import android.os.AsyncTask;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.JsonObject;

import androidassistanthelper.jdubss.androidassistanthelper.Config.Room.AssistantRoomDatabase;
import androidassistanthelper.jdubss.androidassistanthelper.Domain.AssistantUser.AssistantUserDao;
import androidassistanthelper.jdubss.androidassistanthelper.Service.SpotifyService.ISpotifyWebService;

/**
 *  AssistantUserRepository is for interacting with the
 *  Users who have logged into the Application. User
 *  Information is stored in SqlLite DB with Room ORM
 *  so all necessary interactions for the user within the
 *  Database are done here
 */
public class AssistantUserRepository {

    private AssistantUserDao mAssistantUserDao;
    private AssistantUser mCurrentUser;
    private ISpotifyWebService mSpotifyWebService;

    public AssistantUserRepository(Application application, ISpotifyWebService spotifyWebService){
        AssistantRoomDatabase db = AssistantRoomDatabase.getDatabase(application);
        mAssistantUserDao = db.assistantUserDao();
        mSpotifyWebService = spotifyWebService;
        new findCurrentlyLoggedInUser(mAssistantUserDao, this).execute(getmCurrentUser());
    }

    public Boolean isUserCurrentlyLoggedIn(){
        return mCurrentUser != null;
    }


    public AssistantUser getmCurrentUser() {
        return mCurrentUser;
    }

    public void setmCurrentUser(AssistantUser mCurrentUser) {
        this.mCurrentUser=mCurrentUser;
    }

    public void refreshCurrentUser() {
        this.mCurrentUser = mAssistantUserDao.findCurrentlyLoggedInUser(Boolean.TRUE);
    }

    /**
     * Find the Currently logged in User from the Database and set in the mCurrentUser variable
     */
    private static class findCurrentlyLoggedInUser extends AsyncTask<AssistantUser, Void, Void>{

        private AssistantUserDao mAsyncTaskDao;
        private AssistantUserRepository mAssistantUserRepository;

        public findCurrentlyLoggedInUser(AssistantUserDao mAsyncTaskDao,AssistantUserRepository mAssistantUserRepository){
            this.mAsyncTaskDao = mAsyncTaskDao;
            this.mAssistantUserRepository = mAssistantUserRepository;
        }

        @Override
        protected Void doInBackground(AssistantUser... assistantUsers) {
            mAssistantUserRepository.setmCurrentUser(mAsyncTaskDao.findCurrentlyLoggedInUser(Boolean.TRUE));
            return null;
        }
    }

    public void updateSpotifyInformation(AssistantUser spotifyUser){
        new updateSpotifyInformation(mAssistantUserDao, mSpotifyWebService, this).execute(new AssistantUser[]{spotifyUser, mCurrentUser});
    }


    /**
     * Insert or Update a User
     * @param assistantUser
     */
    public void insertOrUpdateUser(AssistantUser assistantUser) {
        new insertOrUpdateUser(mAssistantUserDao, mSpotifyWebService, this).execute(new AssistantUser[]{assistantUser, mCurrentUser});
    }

    private static class insertOrUpdateUser extends AsyncTask<AssistantUser, Void, Void> {

        private AssistantUserDao mAsyncTaskDao;
        private AssistantUserRepository mAssistantUserRepository;

        public insertOrUpdateUser(AssistantUserDao asyncTaskDao, ISpotifyWebService spotifyWebService, AssistantUserRepository assistantUserRepository){
            this.mAsyncTaskDao = asyncTaskDao;
            this.mAssistantUserRepository = assistantUserRepository;
        }

        @Override
        protected Void doInBackground(final AssistantUser... assistantUsers) {
            AssistantUser assistantUser = assistantUsers[0];

            Boolean userExist = mAsyncTaskDao.findUserByEmail(assistantUser.getEmail()) != null;

            if(!userExist)
                mAsyncTaskDao.insert(assistantUser);
            else
                mAsyncTaskDao.update(assistantUser);

            mAssistantUserRepository.refreshCurrentUser();

            return null;
        }
    }

    /**
     * Update the information on the user Object with Relevant Spotify Information
     */
    private static class updateSpotifyInformation extends AsyncTask<AssistantUser, Void, Void> {

        private AssistantUserDao mAsyncTaskDao;
        private ISpotifyWebService mSpotifyWebService;
        private AssistantUserRepository mAssistantUserRepository;

        public updateSpotifyInformation(AssistantUserDao asyncTaskDao, ISpotifyWebService spotifyWebService, AssistantUserRepository assistantUserRepository){
            this.mAsyncTaskDao = asyncTaskDao;
            this.mSpotifyWebService = spotifyWebService;
            this.mAssistantUserRepository = assistantUserRepository;
        }

        @Override
        protected Void doInBackground(final AssistantUser... assistantUsers) {
            AssistantUser assistantUser = assistantUsers[0];
            AssistantUser mCurrentUser = assistantUsers[1];
            /*
            if the userId is null then Spotify Information has not been saved.
            Save Spotify infomation to user and set Logged In to Spotify To True
            Will need to analyze if we need to refresh more data on the Update.
            at the moment just setting logged in to True. May need more update data.
             */
            if(assistantUser.getUserId() == null) { //Spotify data has not been saved
                JsonObject spotifyUserResponse = mSpotifyWebService.findSpotifyUserObjectByAccessToken(assistantUser.getSpotifyAccessToken());
                assistantUser.updateDpotifyProperties(spotifyUserResponse);
            }

            assistantUser.setLoggedInSpotify(Boolean.TRUE);
            mAsyncTaskDao.update(assistantUser);
            mAssistantUserRepository.refreshCurrentUser();
            return null;
        }
    }

    /**
     * Insert or update a user from the Google Sign In Information
     * When a user signs in for the first time through google we are
     * using that information to persist to the User object and Room /
     * Sql Lite Database
     * @param account
     */
    public void insertOrUpdateUserFromGoogleAccount( GoogleSignInAccount account) {
        new insertOrUpdateUserFromGoogleAccount(mAssistantUserDao, mSpotifyWebService, this).execute(new GoogleSignInAccount[]{account});
    }

    private static class insertOrUpdateUserFromGoogleAccount extends AsyncTask<GoogleSignInAccount, Void, Void> {

        private AssistantUserDao mAsyncTaskDao;
        private AssistantUserRepository mAssistantUserRepository;

        public insertOrUpdateUserFromGoogleAccount(AssistantUserDao asyncTaskDao, ISpotifyWebService spotifyWebService, AssistantUserRepository assistantUserRepository){
            this.mAsyncTaskDao = asyncTaskDao;
            this.mAssistantUserRepository = assistantUserRepository;
        }

        @Override
        protected Void doInBackground(final GoogleSignInAccount... accounts) {
            GoogleSignInAccount account = accounts[0];

            AssistantUser user = mAsyncTaskDao.findUserByEmail(account.getEmail());

            if(user == null) {
                user = new AssistantUser(account);
                mAsyncTaskDao.insert(user);
            }else {
                user.updateUserFromAccount(account);
                mAsyncTaskDao.update(user);
            }

            mAssistantUserRepository.refreshCurrentUser();

            return null;
        }
    }
}
