package com.qrgen.app.generator;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.qrgen.app.data.model.QRCustomizationParams;

import java.util.HashMap;
import java.util.Map;

public class QRGeneratorService {

    private static final int DEFAULT_SIZE = 512;
    private static final int DEFAULT_MARGIN = 2;

    public static Bitmap generate(String content) throws WriterException {
        return generate(content, DEFAULT_SIZE, Color.BLACK, Color.WHITE);
    }

    public static Bitmap generate(String content, int size,
                                  int foregroundColor, int backgroundColor)
            throws WriterException {

        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("Содержимое QR-кода не может быть пустым");
        }

        if (size < 100 || size > 2048) {
            throw new IllegalArgumentException("Размер должен быть от 100 до 2048");
        }

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, DEFAULT_MARGIN);

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hints);

        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y * width + x] = bitMatrix.get(x, y) ? foregroundColor : backgroundColor;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public static Bitmap generate(String content, QRCustomizationParams params)
            throws WriterException {
        return generate(
                content,
                params.getSize(),
                params.getForegroundColor(),
                params.getBackgroundColor()
        );
    }

    public static boolean isValidContent(String content) {
        return content != null && !content.trim().isEmpty() && content.length() <= 4296;
    }
}