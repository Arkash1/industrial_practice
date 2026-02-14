package com.qrgen.app.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageSaver {

    private static final String FOLDER_NAME = "QR Codes";
    private static final int IMAGE_QUALITY = 100;

    public static Uri saveToGallery(Context context, Bitmap bitmap) throws IOException {
        String fileName = generateFileName();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return saveWithMediaStore(context, bitmap, fileName);
        } else {
            return saveToExternalStorage(context, bitmap, fileName);
        }
    }

    private static Uri saveWithMediaStore(Context context, Bitmap bitmap, String fileName)
            throws IOException {
        ContentResolver resolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + File.separator + FOLDER_NAME);

        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        if (imageUri != null) {
            try (OutputStream outputStream = resolver.openOutputStream(imageUri)) {
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, IMAGE_QUALITY, outputStream);
                }
            }
        }

        return imageUri;
    }

    @SuppressWarnings("deprecation")
    private static Uri saveToExternalStorage(Context context, Bitmap bitmap, String fileName)
            throws IOException {
        File directory = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                FOLDER_NAME
        );

        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Не удалось создать директорию");
        }

        File file = new File(directory, fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, IMAGE_QUALITY, fos);
        }

        return Uri.fromFile(file);
    }

    public static Uri saveTempFile(Context context, Bitmap bitmap) throws IOException {
        File cacheDir = new File(context.getCacheDir(), "shared_qr");
        if (!cacheDir.exists()) cacheDir.mkdirs();

        File file = new File(cacheDir, "qr_code_" + System.currentTimeMillis() + ".png");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, IMAGE_QUALITY, fos);
        }
        return Uri.fromFile(file);
    }

    private static String generateFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        return "QR_" + sdf.format(new Date()) + ".png";
    }
}