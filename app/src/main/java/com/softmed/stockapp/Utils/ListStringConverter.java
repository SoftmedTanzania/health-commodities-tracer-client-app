package com.softmed.stockapp.Utils;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * Created by issy on 22/01/2018.
 *
 * @issyzac issyzac.iz@gmail.com
 * On Project HFReferralApp
 */

public class ListStringConverter {

    @TypeConverter
    public static List<Long> stringToSomeObjectList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Gson gson = new Gson();
        Type listType = new TypeToken<List<String>>() {}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String someObjectListToString(List<Long> someObjects) {
        Gson gson = new Gson();
        return gson.toJson(someObjects);
    }

}
