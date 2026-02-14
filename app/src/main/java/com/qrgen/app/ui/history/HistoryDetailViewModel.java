package com.qrgen.app.ui.history;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.qrgen.app.data.db.QRCodeEntity;
import com.qrgen.app.data.repository.QRCodeRepository;

public class HistoryDetailViewModel extends AndroidViewModel {

    private final QRCodeRepository repository;
    private LiveData<QRCodeEntity> entity;

    public HistoryDetailViewModel(@NonNull Application application) {
        super(application);
        repository = new QRCodeRepository(application);
    }

    public void loadEntity(long id) {
        entity = repository.getById(id);
    }

    public LiveData<QRCodeEntity> getEntity() {
        return entity;
    }

    public void toggleFavorite(long id) {
        repository.toggleFavorite(id);
    }

    public void delete(QRCodeEntity entity) {
        repository.delete(entity);
    }
}