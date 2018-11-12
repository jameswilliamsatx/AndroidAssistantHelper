package androidassistanthelper.jdubss.androidassistanthelper.Domain.AssistantUser.Internal;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.Scope;
import com.google.gson.JsonObject;

import java.util.Date;
import java.util.Set;

@Entity(tableName = "assistant_user",
        indices = {@Index(value="email", unique = true), @Index(value="user_id", unique = true)}
        )
public class AssistantUser {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name="is_currently_logged_in")
    private Boolean currentlyLoggedIn;

    //Google Attributes
    @ColumnInfo(name="displayName")
    private String displayName;

    @ColumnInfo(name="googleId")
    private String googleId;

    @ColumnInfo(name="email")
    private String email;

    @ColumnInfo(name="is_logged_in_google")
    private Boolean loggedInGoogle;

    @ColumnInfo(name="googleScopes")
    private Set<Scope> googleScopes;

    @ColumnInfo(name="google_access_token")
    private String googleAccessToken;

    @ColumnInfo(name="google_refresh_token")
    private String googleRefreshToken;

    @ColumnInfo(name="googleServerAuthToken")
    private String googleServerAuthToken;

    @ColumnInfo(name="google_token_expires_in")
    private Integer googleTokenExpiresIn;


    //Spotify Attributes
    @ColumnInfo(name = "user_id")
    private String userId;

    @ColumnInfo(name="first_name")
    private String firstName;

    @ColumnInfo(name="last_name")
    private String lastName;

    @ColumnInfo(name = "is_logged_in_spotify")
    private Boolean loggedInSpotify;

    @ColumnInfo(name="spotify_access_token")
    private String spotifyAccessToken;

    @ColumnInfo(name="spotify_refresh_token")
    private String spotifyRefreshToken;

    @ColumnInfo(name="spotify_token_expires_In")
    private Integer spotifyTokenExpiresIn;

    public AssistantUser() {}

    public AssistantUser(GoogleSignInAccount account) {
        setAccountProperties(account);
    }

    public void updateUserFromAccount(GoogleSignInAccount account) {
        setAccountProperties(account);
    }

    public void updateDpotifyProperties(JsonObject spotifyUserResponse){
        String[] name = spotifyUserResponse.get("display_name").getAsString().split(" ");
        this.firstName = name[0];
        this.lastName = name[1];
        this.userId=spotifyUserResponse.get("id").getAsString();
        this.loggedInSpotify= Boolean.TRUE;
    }

    private void setAccountProperties(GoogleSignInAccount account){
        this.displayName = account.getDisplayName();
        this.email = account.getEmail();
        this.googleId = account.getId();
        this.googleServerAuthToken = account.getServerAuthCode();
        this.googleScopes = account.getGrantedScopes();
        this.loggedInGoogle = Boolean.TRUE;
        this.currentlyLoggedIn = Boolean.TRUE;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Boolean getCurrentlyLoggedIn() {
        return currentlyLoggedIn;
    }

    public void setCurrentlyLoggedIn(Boolean currentlyLoggedIn) {
        this.currentlyLoggedIn = currentlyLoggedIn;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getLoggedInGoogle() {
        return loggedInGoogle;
    }

    public void setLoggedInGoogle(Boolean loggedInGoogle) {
        this.loggedInGoogle = loggedInGoogle;
    }

    public String getGoogleAccessToken() {
        return googleAccessToken;
    }

    public void setGoogleAccessToken(String googleAccessToken) {
        this.googleAccessToken = googleAccessToken;
    }

    public String getGoogleRefreshToken() {
        return googleRefreshToken;
    }

    public void setGoogleRefreshToken(String googleRefreshToken) {
        this.googleRefreshToken = googleRefreshToken;
    }

    public Integer getGoogleTokenExpiresIn() {
        return googleTokenExpiresIn;
    }

    public void setGoogleTokenExpiresIn(Integer googleTokenExpiresIn) {
        this.googleTokenExpiresIn = googleTokenExpiresIn;
    }

    public Integer getSpotifyTokenExpiresIn() {
        return spotifyTokenExpiresIn;
    }

    public void setSpotifyTokenExpiresIn(Integer spotifyTokenExpiresIn) {
        this.spotifyTokenExpiresIn = spotifyTokenExpiresIn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getLoggedInSpotify() {
        return loggedInSpotify;
    }

    public void setLoggedInSpotify(Boolean loggedInSpotify) {
        this.loggedInSpotify = loggedInSpotify;
    }

    public String getSpotifyAccessToken() {
        return spotifyAccessToken;
    }

    public void setSpotifyAccessToken(String spotifyAccessToken) {
        this.spotifyAccessToken = spotifyAccessToken;
    }

    public String getSpotifyRefreshToken() {
        return spotifyRefreshToken;
    }

    public void setSpotifyRefreshToken(String spotifyRefreshToken) {
        this.spotifyRefreshToken = spotifyRefreshToken;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getGoogleServerAuthToken() {
        return googleServerAuthToken;
    }

    public void setGoogleServerAuthToken(String googleServerAuthToken) {
        this.googleServerAuthToken = googleServerAuthToken;
    }

    public Set<Scope> getGoogleScopes() {
        return googleScopes;
    }

    public void setGoogleScopes(Set<Scope> googleScopes) {
        this.googleScopes = googleScopes;
    }
}
