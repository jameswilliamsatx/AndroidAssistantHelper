package androidassistanthelper.jdubss.androidassistanthelper.Config.Room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import androidassistanthelper.jdubss.androidassistanthelper.Domain.AssistantUser.AssistantUserDao;
import androidassistanthelper.jdubss.androidassistanthelper.Domain.AssistantUser.Internal.AssistantUser;

/**
 * Room Database
 *
 * Handles creating and managing Database
 *
 * If you need more tables and entities add
 * here
 */
@Database(entities = {AssistantUser.class}, version = 1, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class AssistantRoomDatabase extends RoomDatabase{

    public abstract AssistantUserDao assistantUserDao();

    public static AssistantRoomDatabase INSTANCE;

    public static AssistantRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AssistantRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AssistantRoomDatabase.class, "test_database")
                            .fallbackToDestructiveMigration() //TODO you cannot do this here =)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
