package com.qrgen.app.ui.history;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.qrgen.app.data.db.QRCodeEntity;
import com.qrgen.app.data.model.QRSource;
import com.qrgen.app.data.repository.QRCodeRepository;

import java.util.List;

public class HistoryViewModel extends AndroidViewModel {

    private final QRCodeRepository repository;
    private final MediatorLiveData<List<QRCodeEntity>> qrCodes = new MediatorLiveData<>();
    private LiveData<List<QRCodeEntity>> currentSource;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        repository = new QRCodeRepository(application);
        loadAll();
    }

    public void loadAll() {
        switchSource(repository.getAll());
    }

    public void loadGenerated() {
        switchSource(repository.getBySource(QRSource.GENERATED.getValue()));
    }

    public void loadScanned() {
        switchSource(repository.getBySource(QRSource.SCANNED.getValue()));
    }

    public void loadFavorites() {
        switchSource(repository.getFavorites());
    }

    public void search(String query) {
        switchSource(repository.search(query));
    }

    private void switchSource(LiveData<List<QRCodeEntity>> newSource) {
        if (currentSource != null) {
            qrCodes.removeSource(currentSource);
        }
        currentSource = newSource;
        qrCodes.addSource(currentSource, qrCodes::setValue);
    }

    public void toggleFavorite(long id) {
        repository.toggleFavorite(id);
    }

    public void delete(QRCodeEntity entity) {
        repository.delete(entity);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public LiveData<List<QRCodeEntity>> getQrCodes() {
        return qrCodes;
    }
}