package com.qrgen.app.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface QRCodeDao {

    @Query("SELECT * FROM qr_codes ORDER BY created_at DESC")
    LiveData<List<QRCodeEntity>> getAll();

    @Query("SELECT * FROM qr_codes WHERE source = :source ORDER BY created_at DESC")
    LiveData<List<QRCodeEntity>> getBySource(String source);

    @Query("SELECT * FROM qr_codes WHERE is_favorite = 1 ORDER BY created_at DESC")
    LiveData<List<QRCodeEntity>> getFavorites();

    @Query("SELECT * FROM qr_codes WHERE content LIKE '%' || :query || '%' OR title LIKE '%' || :query || '%' ORDER BY created_at DESC")
    LiveData<List<QRCodeEntity>> search(String query);

    @Query("SELECT * FROM qr_codes WHERE id = :id")
    LiveData<QRCodeEntity> getById(long id);

    @Query("SELECT * FROM qr_codes WHERE id = :id")
    QRCodeEntity getByIdSync(long id);

    @Insert
    long insert(QRCodeEntity entity);

    @Update
    void update(QRCodeEntity entity);

    @Delete
    void delete(QRCodeEntity entity);

    @Query("DELETE FROM qr_codes")
    void deleteAll();

    @Query("UPDATE qr_codes SET is_favorite = NOT is_favorite, updated_at = :timestamp WHERE id = :id")
    void toggleFavorite(long id, long timestamp);

    @Query("SELECT COUNT(*) FROM qr_codes")
    int getCount();

    @Query("DELETE FROM qr_codes WHERE id IN (SELECT id FROM qr_codes ORDER BY created_at ASC LIMIT :count)")
    void deleteOldest(int count);
}