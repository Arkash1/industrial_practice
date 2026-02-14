package com.qrgen.app.ui.result;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.qrgen.app.data.repository.QRCodeRepository;

public class ResultViewModel extends AndroidViewModel {

    private final QRCodeRepository repository;
    private boolean favorite = false;
    private long currentEntityId = -1;

    public ResultViewModel(@NonNull Application application) {
        super(application);
        repository = new QRCodeRepository(application);
    }

    public void setCurrentEntityId(long id) {
        this.currentEntityId = id;
    }

    public void toggleFavorite() {
        favorite = !favorite;
        if (currentEntityId > 0) {
            repository.toggleFavorite(currentEntityId);
        }
    }

    public boolean isFavorite() {
        return favorite;
    }
}