package com.qrgen.app.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;

public class PermissionManager {

    /**
     * Проверяет, предоставлено ли разрешение на камеру
     */
    public static boolean hasCameraPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Проверяет, предоставлено ли разрешение на запись в хранилище
     * Для Android 10+ (API 29+) разрешение не требуется (используется MediaStore)
     */
    public static boolean hasStorageWritePermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return true; // Scoped Storage — разрешение не нужно
        }
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Проверяет, предоставлено ли разрешение на чтение из хранилища
     */
    public static boolean hasStorageReadPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return true;
        }
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Запрашивает разрешение на камеру через ActivityResultLauncher
     */
    public static void requestCameraPermission(ActivityResultLauncher<String> launcher) {
        launcher.launch(Manifest.permission.CAMERA);
    }

    /**
     * Запрашивает разрешение на запись в хранилище через ActivityResultLauncher
     * Только для Android < 10
     */
    public static void requestStorageWritePermission(ActivityResultLauncher<String> launcher) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    /**
     * Возвращает нужное разрешение для записи в зависимости от версии API
     */
    public static String getStorageWritePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return null; // Не нужно
        }
        return Manifest.permission.WRITE_EXTERNAL_STORAGE;
    }

    /**
     * Возвращает нужное разрешение для чтения в зависимости от версии API
     */
    public static String getStorageReadPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return Manifest.permission.READ_MEDIA_IMAGES;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return null; // Не нужно
        }
        return Manifest.permission.READ_EXTERNAL_STORAGE;
    }

    /**
     * Открывает настройки приложения (если пользователь навсегда отказал)
     */
    public static void openAppSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Проверяет, есть ли на устройстве камера
     */
    public static boolean hasCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }
}