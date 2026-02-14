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
import com.qrgen.app.databinding.FragmentCreateContactBinding;
import com.qrgen.app.generator.QRContentBuilder;

public class CreateContactFragment extends Fragment {

    private FragmentCreateContactBinding binding;
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
        binding = FragmentCreateContactBinding.inflate(inflater, container, false);
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
            String name = binding.editTextName.getText().toString().trim();
            String phone = binding.editTextPhone.getText().toString().trim();
            String email = binding.editTextEmail.getText().toString().trim();
            String organization = binding.editTextOrganization.getText().toString().trim();
            String address = binding.editTextAddress.getText().toString().trim();

            if (name.isEmpty()) {
                binding.inputLayoutName.setError("Введите имя");
                return;
            }
            binding.inputLayoutName.setError(null);

            try {
                String content = QRContentBuilder.buildVCardContent(
                        name, phone, email, organization, address);
                viewModel.generateQRCode(content, QRType.CONTACT.getValue(), name);
            } catch (IllegalArgumentException e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
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
                args.putString("type", QRType.CONTACT.getValue());
                args.putInt("fgColor", viewModel.getCustomizationParams().getForegroundColor());
                args.putInt("bgColor", viewModel.getCustomizationParams().getBackgroundColor());
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_createContact_to_result, args);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
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