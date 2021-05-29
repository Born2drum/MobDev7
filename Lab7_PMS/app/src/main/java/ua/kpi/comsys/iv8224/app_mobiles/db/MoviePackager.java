package ua.kpi.comsys.iv8224.app_mobiles.db;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;

public class MoviePackager {
    @TypeConverter
    public String unpackageImdbIds(ArrayList<String> imdbIds) {
        StringBuilder result = new StringBuilder();
        for (String imdbId : imdbIds) {
            result.append(imdbId).append(" ");
        }
        return result.toString();
    }

    @TypeConverter
    public ArrayList<String> packageImdbIds(String data) {
        ArrayList<String> result = new ArrayList<>();
        if (data.length() > 0)
            result.addAll(Arrays.asList(data.split(" ")));
        return result;
    }
}
