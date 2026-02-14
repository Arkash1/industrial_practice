package com.qrgen.app.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.qrgen.app.data.model.QRType;

import java.util.Arrays;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private final MutableLiveData<List<QRType>> qrTypes = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        loadQRTypes();
    }

    private void loadQRTypes() {
        List<QRType> types = Arrays.asList(QRType.values());
        qrTypes.setValue(types);
    }

    public LiveData<List<QRType>> getQrTypes() {
        return qrTypes;
    }

    /**
     * Возвращает иконку для типа QR-кода
     */
    public static String getTypeEmoji(QRType type) {
        switch (type) {
            case TEXT:    return "🔤";
            case URL:     return "🌐";
            case WIFI:    return "📶";
            case CONTACT: return "👤";
            case EMAIL:   return "📧";
            case PHONE:   return "📱";
            case SMS:     return "💬";
            case GEO:     return "📍";
            default:      return "📄";
        }
    }
}