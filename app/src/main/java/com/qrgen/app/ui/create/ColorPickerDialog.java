package com.qrgen.app.ui.create;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.qrgen.app.R;

public class ColorPickerDialog {

    private final Context context;
    private final int initialColor;
    private final OnColorSelectedListener listener;

    private static final int[] PRESET_COLORS = {
            Color.BLACK, Color.WHITE,
            0xFF212121, 0xFF424242, 0xFF757575, 0xFFBDBDBD,
            0xFFD32F2F, 0xFFE91E63, 0xFF9C27B0, 0xFF673AB7,
            0xFF3F51B5, 0xFF2196F3, 0xFF03A9F4, 0xFF00BCD4,
            0xFF009688, 0xFF4CAF50, 0xFF8BC34A, 0xFFCDDC39,
            0xFFFFEB3B, 0xFFFFC107, 0xFFFF9800, 0xFFFF5722,
            0xFF795548, 0xFF607D8B
    };

    public interface OnColorSelectedListener {
        void onColorSelected(int color);
    }

    public ColorPickerDialog(Context context, int initialColor, OnColorSelectedListener listener) {
        this.context = context;
        this.initialColor = initialColor;
        this.listener = listener;
    }

    public void show() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_color_picker, null);

        GridLayout gridLayout = view.findViewById(R.id.gridColors);
        View previewColor = view.findViewById(R.id.previewColor);
        TextView textHexColor = view.findViewById(R.id.textHexColor);

        final int[] selectedColor = {initialColor};
        previewColor.setBackgroundColor(initialColor);
        textHexColor.setText(String.format("#%06X", (0xFFFFFF & initialColor)));

        // Заполняем палитру
        for (int color : PRESET_COLORS) {
            View colorView = new View(context);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = dpToPx(40);
            params.height = dpToPx(40);
            params.setMargins(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4));
            colorView.setLayoutParams(params);
            colorView.setBackgroundColor(color);

            colorView.setOnClickListener(v -> {
                selectedColor[0] = color;
                previewColor.setBackgroundColor(color);
                textHexColor.setText(String.format("#%06X", (0xFFFFFF & color)));
            });

            gridLayout.addView(colorView);
        }

        new AlertDialog.Builder(context)
                .setTitle("Выберите цвет")
                .setView(view)
                .setPositiveButton("OK", (dialog, which) -> {
                    listener.onColorSelected(selectedColor[0]);
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private int dpToPx(int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}