package com.qrgen.app.ui.create;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.qrgen.app.R;
import com.qrgen.app.data.model.QRType;
import com.qrgen.app.databinding.FragmentCreateGeoBinding;
import com.qrgen.app.generator.QRContentBuilder;

public class CreateGeoFragment extends Fragment {

    private FragmentCreateGeoBinding binding;
    private CreateViewModel viewModel;

    private View headerCustomization;
    private View layoutCustomization;
    private ImageView iconExpand;
    private View buttonForegroundColor;
    private View buttonBackgroundColor;
    private View viewForegroundColor;
    private View viewBackgroundColor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCreateGeoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CreateViewModel.class);

        findCustomizationViews(view);
        setupUI();
        observeViewModel();
    }

    private void findCustomizationViews(View root) {
        headerCustomization = root.findViewById(R.id.headerCustomization);
        layoutCustomization = root.findViewById(R.id.layoutCustomization);
        iconExpand = root.findViewById(R.id.iconExpand);
        buttonForegroundColor = root.findViewById(R.id.buttonForegroundColor);
        buttonBackgroundColor = root.findViewById(R.id.buttonBackgroundColor);
        viewForegroundColor = root.findViewById(R.id.viewForegroundColor);
        viewBackgroundColor = root.findViewById(R.id.viewBackgroundColor);
    }

    private void setupUI() {
        binding.buttonGenerate.setOnClickListener(v -> {
            String latStr = binding.editTextLatitude.getText().toString().trim();
            String lonStr = binding.editTextLongitude.getText().toString().trim();
            String label = binding.editTextLabel.getText().toString().trim();

            if (latStr.isEmpty()) {
                binding.inputLayoutLatitude.setError("Введите широту");
                return;
            }
            binding.inputLayoutLatitude.setError(null);

            if (lonStr.isEmpty()) {
                binding.inputLayoutLongitude.setError("Введите долготу");
                return;
            }
            binding.inputLayoutLongitude.setError(null);

            double latitude, longitude;
            try {
                latitude = Double.parseDouble(latStr);
                if (latitude < -90 || latitude > 90) {
                    binding.inputLayoutLatitude.setError("Широта: от -90 до 90");
                    return;
                }
            } catch (NumberFormatException e) {
                binding.inputLayoutLatitude.setError("Некорректное число");
                return;
            }
            try {
                longitude = Double.parseDouble(lonStr);
                if (longitude < -180 || longitude > 180) {
                    binding.inputLayoutLongitude.setError("Долгота: от -180 до 180");
                    return;
                }
            } catch (NumberFormatException e) {
                binding.inputLayoutLongitude.setError("Некорректное число");
                return;
            }

            String content = QRContentBuilder.buildGeoContent(latitude, longitude,
                    label.isEmpty() ? null : label);
            String title = label.isEmpty()
                    ? String.format("%.4f, %.4f", latitude, longitude) : label;
            viewModel.generateQRCode(content, QRType.GEO.getValue(), title);
        });

        headerCustomization.setOnClickListener(v -> {
            boolean isVisible = layoutCustomization.getVisibility() == View.VISIBLE;
            layoutCustomization.setVisibility(isVisible ? View.GONE : View.VISIBLE);
            iconExpand.setRotation(isVisible ? 0 : 180);
        });

        buttonForegroundColor.setOnClickListener(v -> showColorPicker(true));
        buttonBackgroundColor.setOnClickListener(v -> showColorPicker(false));
    }

    private void showColorPicker(boolean isForeground) {
        ColorPickerDialog dialog = new ColorPickerDialog(
                requireContext(),
                isForeground ? viewModel.getCustomizationParams().getForegroundColor()
                        : viewModel.getCustomizationParams().getBackgroundColor(),
                color -> {
                    if (isForeground) {
                        viewModel.setForegroundColor(color);
                        viewForegroundColor.setBackgroundColor(color);
                    } else {
                        viewModel.setBackgroundColor(color);
                        viewBackgroundColor.setBackgroundColor(color);
                    }
                }
        );
        dialog.show();
    }

    private void observeViewModel() {
        viewModel.getGeneratedQR().observe(getViewLifecycleOwner(), bitmap -> {
            if (bitmap != null) {
                Bundle args = new Bundle();
                args.putString("content", viewModel.getLastContent());
                args.putString("type", QRType.GEO.getValue());
                args.putInt("fgColor", viewModel.getCustomizationParams().getForegroundColor());
                args.putInt("bgColor", viewModel.getCustomizationParams().getBackgroundColor());
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_createGeo_to_result, args);
            }
        });
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}