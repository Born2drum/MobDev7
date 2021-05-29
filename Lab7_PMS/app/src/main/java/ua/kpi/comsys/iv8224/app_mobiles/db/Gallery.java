package ua.kpi.comsys.iv8224.app_mobiles.db;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.ByteArrayOutputStream;

@Entity
public class Gallery {
    @PrimaryKey
    public long id;
    public String imageUrl;
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    public byte[] imageData;

    public Gallery(long id, String imageUrl, byte[] imageData){
        this.id = id;
        this.imageUrl = imageUrl;
        this.imageData = imageData;
    }

    public Bitmap getBitmapImage() {
        return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }
}
