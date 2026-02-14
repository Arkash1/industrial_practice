package com.qrgen.app.ui.scanner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.qrgen.app.databinding.BottomSheetScanResultBinding;
import com.qrgen.app.utils.ClipboardHelper;
import com.qrgen.app.utils.ShareHelper;

public class ScanResultBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetScanResultBinding binding;
    private static final String ARG_CONTENT = "content";

    public static ScanResultBottomSheet newInstance(String content) {
        ScanResultBottomSheet fragment = new ScanResultBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_CONTENT, content);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetScanResultBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String content = getArguments() != null ? getArguments().getString(ARG_CONTENT, "") : "";
        binding.textScanResult.setText(content);

        // Определяем тип и показываем соответствующую кнопку действия
        if (content.startsWith("http://") || content.startsWith("https://")) {
            binding.buttonAction.setText("Открыть в браузере");
            binding.buttonAction.setVisibility(View.VISIBLE);
            binding.buttonAction.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(content));
                startActivity(intent);
            });
        } else if (content.startsWith("tel:")) {
            binding.buttonAction.setText("Позвонить");
            binding.buttonAction.setVisibility(View.VISIBLE);
            binding.buttonAction.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(content));
                startActivity(intent);
            });
        } else if (content.startsWith("mailto:")) {
            binding.buttonAction.setText("Отправить email");
            binding.buttonAction.setVisibility(View.VISIBLE);
            binding.buttonAction.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(content));
                startActivity(intent);
            });
        } else {
            binding.buttonAction.setVisibility(View.GONE);
        }

        // Копировать
        binding.buttonCopy.setOnClickListener(v -> {
            ClipboardHelper.copyToClipboard(requireContext(), content);
        });

        // Поделиться
        binding.buttonShare.setOnClickListener(v -> {
            ShareHelper.shareText(requireContext(), content);
        });

        // Закрыть
        binding.buttonClose.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}