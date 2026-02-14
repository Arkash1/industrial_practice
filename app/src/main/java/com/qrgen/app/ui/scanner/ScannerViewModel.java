package com.qrgen.app.ui.scanner;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.qrgen.app.data.db.QRCodeEntity;
import com.qrgen.app.data.model.QRSource;
import com.qrgen.app.data.model.QRType;
import com.qrgen.app.data.repository.QRCodeRepository;

public class ScannerViewModel extends AndroidViewModel {

    private final QRCodeRepository repository;
    private final MutableLiveData<String> scanResult = new MutableLiveData<>();
    private String lastScannedContent = "";

    public ScannerViewModel(@NonNull Application application) {
        super(application);
        repository = new QRCodeRepository(application);
    }

    public void onQRCodeScanned(String content) {
        // Предотвращаем многократную обработку одного и того же QR-кода
        if (content.equals(lastScannedContent)) {
            return;
        }
        lastScannedContent = content;
        scanResult.setValue(content);

        // Сохраняем в историю
        QRCodeEntity entity = new QRCodeEntity();
        entity.setContent(content);
        entity.setType(detectType(content));
        entity.setSource(QRSource.SCANNED.getValue());
        entity.setTitle(generateTitle(content));
        repository.insert(entity);
    }

    public void clearResult() {
        lastScannedContent = "";
        scanResult.setValue(null);
    }

    public LiveData<String> getScanResult() {
        return scanResult;
    }

    private String detectType(String content) {
        if (content.startsWith("http://") || content.startsWith("https://")) {
            return QRType.URL.getValue();
        } else if (content.startsWith("WIFI:")) {
            return QRType.WIFI.getValue();
        } else if (content.startsWith("BEGIN:VCARD")) {
            return QRType.CONTACT.getValue();
        } else if (content.startsWith("mailto:")) {
            return QRType.EMAIL.getValue();
        } else if (content.startsWith("tel:")) {
            return QRType.PHONE.getValue();
        } else if (content.startsWith("smsto:") || content.startsWith("sms:")) {
            return QRType.SMS.getValue();
        } else if (content.startsWith("geo:")) {
            return QRType.GEO.getValue();
        }
        return QRType.TEXT.getValue();
    }

    private String generateTitle(String content) {
        if (content.length() > 50) {
            return content.substring(0, 50) + "...";
        }
        return content;
    }
}