package com.qrgen.app.ui.history;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.qrgen.app.R;
import com.qrgen.app.databinding.FragmentHistoryBinding;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private HistoryViewModel viewModel;
    private HistoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        setupRecyclerView();
        setupTabs();
        setupSearch();
        setupDeleteAll();
        observeViewModel();
    }

    private void setupRecyclerView() {
        adapter = new HistoryAdapter(
                // onClick — переход на детальный экран
                entity -> {
                    Bundle args = new Bundle();
                    args.putLong("entityId", entity.getId());
                    args.putString("content", entity.getContent());
                    args.putString("type", entity.getType());
                    args.putInt("fgColor", entity.getForegroundColor());
                    args.putInt("bgColor", entity.getBackgroundColor());
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_history_to_historyDetail, args);
                },
                // onFavoriteClick
                entity -> viewModel.toggleFavorite(entity.getId()),
                // onDeleteClick
                entity -> {
                    new MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Удалить")
                            .setMessage("Удалить эту запись?")
                            .setPositiveButton("Удалить", (d, w) -> viewModel.delete(entity))
                            .setNegativeButton("Отмена", null)
                            .show();
                }
        );

        binding.recyclerViewHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewHistory.setAdapter(adapter);
    }

    private void setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: viewModel.loadAll(); break;
                    case 1: viewModel.loadGenerated(); break;
                    case 2: viewModel.loadScanned(); break;
                    case 3: viewModel.loadFavorites(); break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupSearch() {
        binding.editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    viewModel.loadAll();
                } else {
                    viewModel.search(query);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupDeleteAll() {
        binding.buttonDeleteAll.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Очистить историю")
                    .setMessage("Вы уверены, что хотите удалить все записи из истории?")
                    .setPositiveButton("Удалить", (dialog, which) -> viewModel.deleteAll())
                    .setNegativeButton("Отмена", null)
                    .show();
        });
    }

    private void observeViewModel() {
        viewModel.getQrCodes().observe(getViewLifecycleOwner(), entities -> {
            if (entities != null) {
                adapter.submitList(entities);
                binding.textEmptyState.setVisibility(
                        entities.isEmpty() ? View.VISIBLE : View.GONE);
                binding.recyclerViewHistory.setVisibility(
                        entities.isEmpty() ? View.GONE : View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}