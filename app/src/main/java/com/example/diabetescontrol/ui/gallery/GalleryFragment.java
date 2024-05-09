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
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.diabetescontrol.databinding.FragmentGalleryBinding;

import java.util.Calendar;
import java.util.Locale;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private AlarmManager alarmManager;
    private static final int REQUEST_CODE = 1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Obtener el servicio de alarma
        alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);

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

        // Manejar clic en el botón de crear alarma
        binding.buttonCrearAlarma.setOnClickListener(v -> crearAlarmaYRecordatorio());

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

    // Método para configurar la alarma y la notificación
    private void crearAlarmaYRecordatorio() {
        String fecha = binding.textFieldFecha.getEditText().getText().toString();
        String hora = binding.textFieldHora.getEditText().getText().toString();
        String nota = binding.textFieldNota.getEditText().getText().toString();

        // Verificar si la fecha y la hora están ingresadas
        if (fecha.isEmpty() || hora.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor ingrese la fecha y la hora", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtener el año, mes y día de la fecha seleccionada
        String[] partesFecha = fecha.split("-");
        int year = Integer.parseInt(partesFecha[0]);
        int month = Integer.parseInt(partesFecha[1]) - 1;
        int dayOfMonth = Integer.parseInt(partesFecha[2]);

        // Obtener la hora y el minuto de la hora seleccionada
        String[] partesHora = hora.split(":");
        int hourOfDay = Integer.parseInt(partesHora[0]);
        int minute = Integer.parseInt(partesHora[1]);

        // Crear un Calendar con la fecha y la hora seleccionadas
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth, hourOfDay, minute);

        // Configurar la alarma
        configurarAlarma(calendar.getTimeInMillis());
    }

    // Método para configurar la alarma
    private void configurarAlarma(long tiempoAlarma) {
        // Crear un PendingIntent para la alarma
        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
        // Configurar la alarma con el tiempo especificado
        alarmManager.set(AlarmManager.RTC_WAKEUP, tiempoAlarma, pendingIntent);
    }
}


