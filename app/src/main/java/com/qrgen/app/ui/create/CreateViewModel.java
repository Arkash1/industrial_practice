package com.qrgen.app.ui.create;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.zxing.WriterException;
import com.qrgen.app.data.db.QRCodeEntity;
import com.qrgen.app.data.model.QRCustomizationParams;
import com.qrgen.app.data.model.QRSource;
import com.qrgen.app.data.repository.QRCodeRepository;
import com.qrgen.app.generator.QRGeneratorService;

public class CreateViewModel extends AndroidViewModel {

    private final QRCodeRepository repository;
    private final MutableLiveData<Bitmap> generatedQR = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private final QRCustomizationParams customizationParams = new QRCustomizationParams();
    private String lastContent;
    private String lastType;
    private String lastTitle;

    public CreateViewModel(@NonNull Application application) {
        super(application);
        repository = new QRCodeRepository(application);
    }

    public void generateQRCode(String content, String type, String title) {
        if (content == null || content.trim().isEmpty()) {
            errorMessage.setValue("Введите данные для генерации");
            return;
        }

        if (content.length() > 4296) {
            errorMessage.setValue("Данные слишком длинные (макс. 4296 символов)");
            return;
        }

        isLoading.setValue(true);
        lastContent = content;
        lastType = type;
        lastTitle = title;

        new Thread(() -> {
            try {
                Bitmap bitmap = QRGeneratorService.generate(
                        content,
                        customizationParams
                );
                generatedQR.postValue(bitmap);

                // Сохранение в историю
                QRCodeEntity entity = new QRCodeEntity();
                entity.setContent(content);
                entity.setType(type);
                entity.setSource(QRSource.GENERATED.getValue());
                entity.setTitle(title);
                entity.setForegroundColor(customizationParams.getForegroundColor());
                entity.setBackgroundColor(customizationParams.getBackgroundColor());
                repository.insert(entity);

            } catch (WriterException e) {
                errorMessage.postValue("Ошибка генерации: " + e.getMessage());
            } finally {
                isLoading.postValue(false);
            }
        }).start();
    }

    public void setForegroundColor(int color) {
        customizationParams.setForegroundColor(color);
    }

    public void setBackgroundColor(int color) {
        customizationParams.setBackgroundColor(color);
    }

    public void setSize(int size) {
        customizationParams.setSize(size);
    }

    public boolean hasLowContrast() {
        return customizationParams.hasLowContrast();
    }

    public LiveData<Bitmap> getGeneratedQR() { return generatedQR; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public QRCustomizationParams getCustomizationParams() { return customizationParams; }
    public String getLastContent() { return lastContent; }
}