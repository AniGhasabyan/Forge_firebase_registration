package com.example.forge.ui.navbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.forge.R;
import com.example.forge.databinding.DialogChooseUserBinding;
import com.example.forge.ui.UserAdapter;

import java.util.ArrayList;

public class DialogChooseUserFragment extends DialogFragment {

    private DialogChooseUserBinding binding;
    private DialogChooseUserViewModel viewModel;
    private UserAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogChooseUserBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        viewModel = new ViewModelProvider(this).get(DialogChooseUserViewModel.class);

        adapter = new UserAdapter(new ArrayList<>(), R.id.nav_dialog);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        SharedPreferences preferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userRole = preferences.getString("UserRole", "Athlete");

        viewModel.getUserList1().observe(getViewLifecycleOwner(), userList1 -> {
            if (userList1 == null || userList1.isEmpty()) {
                binding.noUsersText.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.GONE);
                binding.dialogTitle.setVisibility(View.GONE);
            } else {
                binding.noUsersText.setVisibility(View.GONE);
                binding.recyclerView.setVisibility(View.VISIBLE);
                binding.dialogTitle.setVisibility(View.VISIBLE);
                adapter.setUserList(userList1);
                if(userRole.equals("Athlete")){
                    binding.dialogTitle.setText("Choose the coach");

                } else if (userRole.equals("Coach")){
                    binding.dialogTitle.setText("Choose the athlete");
                }
            }
        });

        viewModel.loadData(requireContext());

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
