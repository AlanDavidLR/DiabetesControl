package com.example.diabetescontrol;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.AlarmClock;
import android.widget.Button;
import android.widget.TimePicker;
import java.util.Calendar;
import android.widget.EditText;
import android.widget.CheckBox;
import android.util.Log;
import java.util.ArrayList;

public class RegistroTomaMedicamentos extends Fragment {

    private static final String TAG = "RegistroTomaMed";

    private int hour;
    private int minute;
    private TextView textViewMedicineTime;
    private EditText editTextMedName;
    private CheckBox checkBoxSunday, checkBoxMonday, checkBoxTuesday, checkBoxWednesday,
            checkBoxThursday, checkBoxFriday, checkBoxSaturday;

    public RegistroTomaMedicamentos() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registro_toma_medicamentos, container, false);

        textViewMedicineTime = view.findViewById(R.id.tv_medicine_time);
        editTextMedName = view.findViewById(R.id.edit_med_name);
        checkBoxSunday = view.findViewById(R.id.dv_sunday);
        checkBoxMonday = view.findViewById(R.id.dv_monday);
        checkBoxTuesday = view.findViewById(R.id.dv_tuesday);
        checkBoxWednesday = view.findViewById(R.id.dv_wednesday);
        checkBoxThursday = view.findViewById(R.id.dv_thursday);
        checkBoxFriday = view.findViewById(R.id.dv_friday);
        checkBoxSaturday = view.findViewById(R.id.dv_saturday);

        textViewMedicineTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        Button btnCrearR = view.findViewById(R.id.btnCrearR);
        btnCrearR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlarm();
            }
        });

        return view;
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfDay) {
                        hour = hourOfDay;
                        minute = minuteOfDay;
                        String time = String.format("%02d:%02d", hourOfDay, minuteOfDay);
                        textViewMedicineTime.setText(time);
                        Log.d(TAG, "Time set: " + time);
                    }
                },
                hour,
                minute,
                true
        );
        timePickerDialog.show();
    }

    private void createAlarm() {
        String medicamento = editTextMedName.getText().toString();
        if (medicamento.isEmpty()) {
            Log.e(TAG, "createAlarm: Medicamento name is empty");
            return;
        }

        boolean[] diasSeleccionados = {
                checkBoxSunday.isChecked(),
                checkBoxMonday.isChecked(),
                checkBoxTuesday.isChecked(),
                checkBoxWednesday.isChecked(),
                checkBoxThursday.isChecked(),
                checkBoxFriday.isChecked(),
                checkBoxSaturday.isChecked()
        };

        ArrayList<Integer> dias = new ArrayList<>();
        if (diasSeleccionados[0]) dias.add(Calendar.SUNDAY);
        if (diasSeleccionados[1]) dias.add(Calendar.MONDAY);
        if (diasSeleccionados[2]) dias.add(Calendar.TUESDAY);
        if (diasSeleccionados[3]) dias.add(Calendar.WEDNESDAY);
        if (diasSeleccionados[4]) dias.add(Calendar.THURSDAY);
        if (diasSeleccionados[5]) dias.add(Calendar.FRIDAY);
        if (diasSeleccionados[6]) dias.add(Calendar.SATURDAY);

        // Crear la alarma del sistema
        Intent alarmIntent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_HOUR, hour)
                .putExtra(AlarmClock.EXTRA_MINUTES, minute)
                .putExtra(AlarmClock.EXTRA_MESSAGE, "Hora de tomar tu medicamento: " + medicamento)
                .putExtra(AlarmClock.EXTRA_DAYS, dias)
                .putExtra(AlarmClock.EXTRA_SKIP_UI, true);

        if (alarmIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(alarmIntent);
        } else {
            Log.e(TAG, "createAlarm: No app found to handle alarm intent");
        }

        // Configurar la notificación
        Intent notificationIntent = new Intent(requireContext(), AlarmReceiver.class);
        notificationIntent.putExtra("titulo", "Hora de tomar tu medicamento");
        notificationIntent.putExtra("nota", medicamento);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        for (int i = 0; i < diasSeleccionados.length; i++) {
            if (diasSeleccionados[i]) {
                calendar.set(Calendar.DAY_OF_WEEK, i + 1);  // Days of week in Calendar class starts from 1 (Sunday)
                long triggerTime = calendar.getTimeInMillis();
                Log.d(TAG, "createAlarm: Setting alarm for day " + (i + 1) + " at " + hour + ":" + minute + " (triggerTime: " + triggerTime + ")");
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
        }

        Log.d(TAG, "createAlarm: Alarm set for medication: " + medicamento);
    }
}
