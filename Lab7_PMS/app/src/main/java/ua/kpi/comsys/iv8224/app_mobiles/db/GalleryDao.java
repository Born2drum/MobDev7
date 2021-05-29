package ua.kpi.comsys.iv8224.app_mobiles.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GalleryDao {
    @Query("SELECT * FROM Gallery")
    List<Gallery> getAll();

    @Query("SELECT imageUrl FROM Gallery")
    List<String> getAllUrls();

    @Query("SELECT * FROM Gallery WHERE id = :id")
    Gallery getById(long id);

    @Query("SELECT * FROM Gallery WHERE imageUrl = :url")
    Gallery getByUrl(String url);

    @Query("UPDATE Gallery SET imageData = :data WHERE imageUrl = :url")
    void setImageBitmapByUrl(String url, byte[] data);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Gallery gallery);

    @Update
    void update(Gallery gallery);

    @Delete
    void delete(Gallery gallery);
}
