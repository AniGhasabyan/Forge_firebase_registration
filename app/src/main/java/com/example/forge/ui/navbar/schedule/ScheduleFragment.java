package com.example.forge.ui.navbar.schedule;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forge.R;
import com.example.forge.User;
import com.example.forge.databinding.FragmentScheduleBinding;
import com.example.forge.ui.UserAdapter;
import com.example.forge.ui.navbar.DialogChooseUserFragment;
import com.example.forge.ui.navbar.diet.DietViewModel;
import com.example.forge.ui.navbar.diet.DietViewModelFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScheduleFragment extends Fragment {
    private FragmentScheduleBinding binding;
    private TextView mondayTextView, tuesdayTextView, wednesdayTextView, thursdayTextView,
            fridayTextView, saturdayTextView, sundayTextView;
    private ScheduleViewModel scheduleViewModel;
    private String username;
    private String email;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences preferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userRole = preferences.getString("UserRole", "Athlete");

        scheduleViewModel = new ViewModelProvider(this, new ScheduleViewModelFactory(userRole))
                .get(ScheduleViewModel.class);

        binding = FragmentScheduleBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Bundle args = getArguments();
        if (args != null) {
            username = args.getString("username", "");
            email = args.getString("email", "");
        }

        if (username != null) {
            TextView usernameTextView = binding.textUsername;
            String menuSchedule = getString(R.string.menu_schedule);
            usernameTextView.setText("This is " + username + "'s " + menuSchedule);
            usernameTextView.setVisibility(View.VISIBLE);
        }

        mondayTextView = root.findViewById(R.id.mondayText);
        tuesdayTextView = root.findViewById(R.id.tuesdayText);
        wednesdayTextView = root.findViewById(R.id.wednesdayText);
        thursdayTextView = root.findViewById(R.id.thursdayText);
        fridayTextView = root.findViewById(R.id.fridayText);
        saturdayTextView = root.findViewById(R.id.saturdayText);
        sundayTextView = root.findViewById(R.id.sundayText);

        View.OnClickListener dayClickListener = v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    requireActivity(),
                    (view, selectedHour, selectedMinute) -> {
                        String selectedTime = selectedHour + ":" + selectedMinute;
                        TextView textView = (TextView) v;

                        String currentText = textView.getText().toString().trim();
                        if (!currentText.isEmpty()) {
                            currentText += "\n" + selectedTime;
                        } else {
                            currentText = selectedTime;
                        }

                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        textView.setText(currentText);

                        if (username != null) {
                            scheduleViewModel.saveTime(getDayOfWeekFromView(v), selectedTime, username, userRole);
                            textView.setText(currentText + " - " + username);
                        } else {
                            DialogChooseUserFragment dialogFragment = new DialogChooseUserFragment();
                            dialogFragment.show(getChildFragmentManager(), "choose_user_dialog");
                        }
                    },
                    hour,
                    minute,
                    true
            );
            timePickerDialog.show();
        };

        mondayTextView.setOnClickListener(dayClickListener);
        tuesdayTextView.setOnClickListener(dayClickListener);
        wednesdayTextView.setOnClickListener(dayClickListener);
        thursdayTextView.setOnClickListener(dayClickListener);
        fridayTextView.setOnClickListener(dayClickListener);
        saturdayTextView.setOnClickListener(dayClickListener);
        sundayTextView.setOnClickListener(dayClickListener);

        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences preferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userRole = preferences.getString("UserRole", "Athlete");
        observeScheduleData(userRole);
    }

    private void observeScheduleData(String userRole) {
        scheduleViewModel.loadScheduleData(userRole).observe(getViewLifecycleOwner(), scheduleMap -> {
            if (scheduleMap != null) {
                float textSize = 20;

                mondayTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                mondayTextView.setText(scheduleMap.get("monday"));

                tuesdayTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                tuesdayTextView.setText(scheduleMap.get("tuesday"));

                wednesdayTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                wednesdayTextView.setText(scheduleMap.get("wednesday"));

                thursdayTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                thursdayTextView.setText(scheduleMap.get("thursday"));

                fridayTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                fridayTextView.setText(scheduleMap.get("friday"));

                saturdayTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                saturdayTextView.setText(scheduleMap.get("saturday"));

                sundayTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
                sundayTextView.setText(scheduleMap.get("sunday"));
            }
        });
    }

    private String getDayOfWeekFromView(View v) {
        String day = "";

        Object tag = v.getTag();
        if (tag instanceof String) {
            day = (String) tag;
        }

        return day;
    }
}
