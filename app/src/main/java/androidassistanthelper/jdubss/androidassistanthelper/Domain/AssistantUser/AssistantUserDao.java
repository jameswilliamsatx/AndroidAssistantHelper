package androidassistanthelper.jdubss.androidassistanthelper.Domain.AssistantUser;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import androidassistanthelper.jdubss.androidassistanthelper.Domain.AssistantUser.Internal.AssistantUser;

@Dao
public interface AssistantUserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(AssistantUser user);

    @Update
    public void update(AssistantUser user);

    @Query("SELECT * FROM assistant_user WHERE user_id = :userId")
    public AssistantUser findUserByUserId(String userId);

    @Query("SELECT * FROM assistant_user WHERE email = :email")
    public AssistantUser findUserByEmail(String email);

    @Query("SELECT * FROM assistant_user WHERE is_currently_logged_in = :currentlyLoggedIn")
    public AssistantUser findCurrentlyLoggedInUser(Boolean currentlyLoggedIn);

}
