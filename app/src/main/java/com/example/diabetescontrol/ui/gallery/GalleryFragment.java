package com.example.diabetescontrol.ui.gallery;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.example.diabetescontrol.AlarmReceiver;
import com.example.diabetescontrol.R;
import com.example.diabetescontrol.databinding.FragmentGalleryBinding;

import java.util.Calendar;
import java.util.Locale;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private Spinner spinnerType;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        spinnerType = root.findViewById(R.id.spinnerType);

        // Configurar Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.medications_shape_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        // Manejar la selección de fecha
        binding.textFieldFecha.getEditText().setOnClickListener(v -> mostrarSelectorFecha());

        // Manejar la selección de hora
        binding.textFieldHora.getEditText().setOnClickListener(v -> mostrarSelectorHora());

        // Guardar la nota cuando cambie
        binding.textFieldNota.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No se utiliza
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No se utiliza
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Guardar la nota
                String nota = s.toString();
                // Aquí puedes guardar la nota en una variable o en una base de datos
            }
        });

        // Crear la alarma cuando se presiona el botón
        binding.buttonCrearAlarma.setOnClickListener(v -> crearAlarma());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Método para mostrar el selector de fecha (DatePickerDialog)
    private void mostrarSelectorFecha() {
        // Obtener la fecha actual
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Crear un DatePickerDialog y mostrarlo
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year1, month1, dayOfMonth1) -> {
            // Establecer la fecha seleccionada en el campo de texto
            binding.textFieldFecha.getEditText().setText(String.format(Locale.getDefault(), "%d-%02d-%02d", year1, month1 + 1, dayOfMonth1));
        }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    // Método para mostrar el selector de hora (TimePickerDialog)
    private void mostrarSelectorHora() {
        // Obtener la hora actual
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Crear un TimePickerDialog y mostrarlo
        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), (view, hourOfDay1, minute1) -> {
            // Establecer la hora seleccionada en el campo de texto
            binding.textFieldHora.getEditText().setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay1, minute1));
        }, hourOfDay, minute, true);
        timePickerDialog.show();
    }

    // Método para crear la alarma
    private void crearAlarma() {
        String tipoConsulta = spinnerType.getSelectedItem().toString();
        String fecha = binding.textFieldFecha.getEditText().getText().toString();
        String hora = binding.textFieldHora.getEditText().getText().toString();
        String nota = binding.textFieldNota.getEditText().getText().toString();

        // Crear el título de la alarma
        String titulo = "Cita con: " + tipoConsulta;

        // Convertir la fecha y la hora en milisegundos
        Calendar calendar = Calendar.getInstance();
        // Asume que la fecha está en el formato "yyyy-MM-dd"
        String[] fechaParts = fecha.split("-");
        int year = Integer.parseInt(fechaParts[0]);
        int month = Integer.parseInt(fechaParts[1]) - 1; // Calendar.MONTH es 0-based
        int day = Integer.parseInt(fechaParts[2]);
        // Asume que la hora está en el formato "HH:mm"
        String[] horaParts = hora.split(":");
        int hour = Integer.parseInt(horaParts[0]);
        int minute = Integer.parseInt(horaParts[1]);

        calendar.set(year, month, day, hour, minute, 0);

        // Crear el Intent para la alarma
        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
        intent.putExtra("titulo", titulo);
        intent.putExtra("nota", nota);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE // Añade FLAG_IMMUTABLE aquí
        );

        // Configurar la alarma
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Toast.makeText(requireContext(), "Alarma creada", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "No se pudo crear la alarma", Toast.LENGTH_SHORT).show();
        }
    }
}

