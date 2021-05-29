package ua.kpi.comsys.iv8224.app_mobiles.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {TableMovies.class, SearchTable.class, Gallery.class}, version = 6)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TableMoviesDao movieDao();
    public abstract SearchTableDao searchTableDao();
    public abstract GalleryDao galleryDao();
}
