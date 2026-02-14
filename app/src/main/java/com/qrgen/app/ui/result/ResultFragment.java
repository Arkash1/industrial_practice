package com.qrgen.app.ui.result;

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
import com.qrgen.app.data.model.QRType;
import com.qrgen.app.databinding.FragmentResultBinding;
import com.qrgen.app.generator.QRGeneratorService;
import com.qrgen.app.utils.ClipboardHelper;
import com.qrgen.app.utils.ImageSaver;
import com.qrgen.app.utils.ShareHelper;

import java.io.IOException;

public class ResultFragment extends Fragment {

    private FragmentResultBinding binding;
    private ResultViewModel viewModel;
    private Bitmap currentBitmap;
    private String content;
    private String type;

    private ActivityResultLauncher<String> storagePermissionLauncher;

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
        binding = FragmentResultBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ResultViewModel.class);

        if (getArguments() != null) {
            content = getArguments().getString("content", "");
            type = getArguments().getString("type", "text");
            int fgColor = getArguments().getInt("fgColor", 0xFF000000);
            int bgColor = getArguments().getInt("bgColor", 0xFFFFFFFF);

            generateAndShow(content, fgColor, bgColor);
        }

        setupButtons();
    }

    private void generateAndShow(String content, int fgColor, int bgColor) {
        try {
            currentBitmap = QRGeneratorService.generate(content, 512, fgColor, bgColor);
            binding.imageQRCode.setImageBitmap(currentBitmap);

            QRType qrType = QRType.fromValue(type);
            binding.textType.setText("Тип: " + qrType.getDisplayName());
            binding.textContent.setText(content);
        } catch (WriterException e) {
            Toast.makeText(getContext(), "Ошибка генерации", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupButtons() {
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
            if (content != null) {
                ClipboardHelper.copyToClipboard(requireContext(), content);
            }
        });

        // Избранное
        binding.buttonFavorite.setOnClickListener(v -> {
            viewModel.toggleFavorite();
            updateFavoriteIcon();
            Toast.makeText(getContext(),
                    viewModel.isFavorite() ? "Добавлено в избранное" : "Удалено из избранного",
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void saveToGallery() {
        if (currentBitmap != null) {
            try {
                ImageSaver.saveToGallery(requireContext(), currentBitmap);
                Toast.makeText(getContext(), "QR-код сохранён в галерею", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(getContext(), "Ошибка сохранения", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateFavoriteIcon() {
        binding.buttonFavorite.setIconResource(
                viewModel.isFavorite() ? R.drawable.ic_star_filled : R.drawable.ic_star_outline
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}