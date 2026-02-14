package com.qrgen.app.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.qrgen.app.R;
import com.qrgen.app.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupTypeCards();
    }

    private void setupTypeCards() {
        binding.cardText.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_createText));

        binding.cardUrl.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_createUrl));

        binding.cardWifi.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_createWifi));

        binding.cardContact.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_createContact));

        binding.cardEmail.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_createEmail));

        binding.cardPhone.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_createPhone));

        binding.cardSms.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_createSms));

        binding.cardGeo.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_createGeo));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}