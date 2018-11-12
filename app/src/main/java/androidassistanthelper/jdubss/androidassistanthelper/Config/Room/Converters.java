package androidassistanthelper.jdubss.androidassistanthelper.Config.Room;

import android.arch.persistence.room.TypeConverter;

import com.google.android.gms.common.api.Scope;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Set;

public class Converters {


    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }


    @TypeConverter
    public static String fromOptionValuesList(Set<Scope> scopeValuesToString) {
        if (scopeValuesToString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Set<Scope>>() {
        }.getType();
        String json = gson.toJson(scopeValuesToString, type);
        return json;
    }

    @TypeConverter
    public static Set<Scope> toOptionValuesList(String scopeValuesFromString) {
        if (scopeValuesFromString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Set<Scope>>() {
        }.getType();
        Set<Scope> scopes = gson.fromJson(scopeValuesFromString, type);
        return scopes;
    }

}