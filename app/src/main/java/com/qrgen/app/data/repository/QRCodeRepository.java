package com.qrgen.app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.qrgen.app.data.db.AppDatabase;
import com.qrgen.app.data.db.QRCodeDao;
import com.qrgen.app.data.db.QRCodeEntity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QRCodeRepository {

    private final QRCodeDao qrCodeDao;
    private final ExecutorService executorService;

    private static final int MAX_HISTORY_SIZE = 1000;

    public QRCodeRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        qrCodeDao = db.qrCodeDao();
        executorService = Executors.newFixedThreadPool(2);
    }

    public LiveData<List<QRCodeEntity>> getAll() {
        return qrCodeDao.getAll();
    }

    public LiveData<List<QRCodeEntity>> getBySource(String source) {
        return qrCodeDao.getBySource(source);
    }

    public LiveData<List<QRCodeEntity>> getFavorites() {
        return qrCodeDao.getFavorites();
    }

    public LiveData<List<QRCodeEntity>> search(String query) {
        return qrCodeDao.search(query);
    }

    public LiveData<QRCodeEntity> getById(long id) {
        return qrCodeDao.getById(id);
    }

    public void insert(QRCodeEntity entity) {
        executorService.execute(() -> {
            qrCodeDao.insert(entity);
            // Автоочистка при превышении лимита
            int count = qrCodeDao.getCount();
            if (count > MAX_HISTORY_SIZE) {
                qrCodeDao.deleteOldest(count - MAX_HISTORY_SIZE);
            }
        });
    }

    public void update(QRCodeEntity entity) {
        executorService.execute(() -> qrCodeDao.update(entity));
    }

    public void delete(QRCodeEntity entity) {
        executorService.execute(() -> qrCodeDao.delete(entity));
    }

    public void deleteAll() {
        executorService.execute(qrCodeDao::deleteAll);
    }

    public void toggleFavorite(long id) {
        executorService.execute(() ->
                qrCodeDao.toggleFavorite(id, System.currentTimeMillis())
        );
    }
}