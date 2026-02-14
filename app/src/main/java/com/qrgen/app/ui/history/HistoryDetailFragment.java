package com.qrgen.app.ui.history;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.zxing.WriterException;
import com.qrgen.app.R;
import com.qrgen.app.data.db.QRCodeEntity;
import com.qrgen.app.data.model.QRSource;
import com.qrgen.app.data.model.QRType;
import com.qrgen.app.databinding.FragmentHistoryDetailBinding;
import com.qrgen.app.generator.QRGeneratorService;
import com.qrgen.app.utils.ClipboardHelper;
import com.qrgen.app.utils.ImageSaver;
import com.qrgen.app.utils.ShareHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HistoryDetailFragment extends Fragment {

    private FragmentHistoryDetailBinding binding;
    private HistoryDetailViewModel viewModel;
    private Bitmap currentBitmap;
    private QRCodeEntity currentEntity;

    private ActivityResultLauncher<String> storagePermissionLauncher;

    private static final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storagePermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) {
                        saveToGallery();
                    } else {
                        Toast.makeText(getContext(),
                                "Разрешение необходимо для сохранения",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HistoryDetailViewModel.class);

        long entityId = -1;
        String content = "";
        String type = "text";
        int fgColor = 0xFF000000;
        int bgColor = 0xFFFFFFFF;

        if (getArguments() != null) {
            entityId = getArguments().getLong("entityId", -1);
            content = getArguments().getString("content", "");
            type = getArguments().getString("type", "text");
            fgColor = getArguments().getInt("fgColor", 0xFF000000);
            bgColor = getArguments().getInt("bgColor", 0xFFFFFFFF);
        }

        displayQRCode(content, type, fgColor, bgColor);

        if (entityId > 0) {
            viewModel.loadEntity(entityId);
            observeEntity();
        }

        setupButtons(content, entityId);
    }

    private void displayQRCode(String content, String type, int fgColor, int bgColor) {
        try {
            currentBitmap = QRGeneratorService.generate(content, 512, fgColor, bgColor);
            binding.imageQRCode.setImageBitmap(currentBitmap);
        } catch (WriterException e) {
            Toast.makeText(getContext(), "Ошибка генерации QR-кода", Toast.LENGTH_SHORT).show();
        }

        QRType qrType = QRType.fromValue(type);
        binding.textType.setText("Тип: " + qrType.getDisplayName());
        binding.textContent.setText(content);
    }

    private void observeEntity() {
        viewModel.getEntity().observe(getViewLifecycleOwner(), entity -> {
            if (entity != null) {
                currentEntity = entity;

                // Обновляем дополнительную информацию
                binding.textDate.setText("Создано: " +
                        dateFormat.format(new Date(entity.getCreatedAt())));

                QRSource source = QRSource.fromValue(entity.getSource());
                binding.textSource.setText("Источник: " +
                        (source == QRSource.GENERATED ? "Создан" : "Отсканирован"));

                updateFavoriteIcon(entity.isFavorite());
            }
        });
    }

    private void setupButtons(String content, long entityId) {
        // Сохранить
        binding.buttonSave.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                if (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    return;
                }
            }
            saveToGallery();
        });

        // Поделиться
        binding.buttonShare.setOnClickListener(v -> {
            if (currentBitmap != null) {
                ShareHelper.shareQRCode(requireContext(), currentBitmap);
            }
        });

        // Копировать
        binding.buttonCopy.setOnClickListener(v -> {
            ClipboardHelper.copyToClipboard(requireContext(), content);
        });

        // Избранное
        binding.buttonFavorite.setOnClickListener(v -> {
            if (entityId > 0) {
                viewModel.toggleFavorite(entityId);
            }
        });

        // Удалить
        binding.buttonDelete.setOnClickListener(v -> {
            if (currentEntity != null) {
                new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Удалить запись")
                        .setMessage("Вы уверены, что хотите удалить эту запись из истории?")
                        .setPositiveButton("Удалить", (dialog, which) -> {
                            viewModel.delete(currentEntity);
                            requireActivity().onBackPressed();
                        })
                        .setNegativeButton("Отмена", null)
                        .show();
            }
        });
    }

    private void saveToGallery() {
        if (currentBitmap != null) {
            try {
                ImageSaver.saveToGallery(requireContext(), currentBitmap);
                Toast.makeText(getContext(), "QR-код сохранён в галерею",
                        Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(getContext(), "Ошибка сохранения",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateFavoriteIcon(boolean isFavorite) {
        binding.buttonFavorite.setIconResource(
                isFavorite ? R.drawable.ic_star_filled : R.drawable.ic_star_outline
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}