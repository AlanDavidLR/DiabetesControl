package com.example.diabetescontrol.ui.gallery;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.text.TextWatcher;
import android.text.Editable;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.diabetescontrol.databinding.FragmentGalleryBinding;

import java.util.Calendar;
import java.util.Locale;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private AlarmManager alarmManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Manejar la selección de fecha
        binding.textFieldFecha.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarSelectorFecha();
            }
        });

        // Manejar la selección de hora
        binding.textFieldHora.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarSelectorHora();
            }
        });

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
        binding.buttonCrearAlarma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearAlarmaYRecordatorio();
            }
        });

        // Obtener el servicio de alarma
        alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);

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
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Establecer la fecha seleccionada en el campo de texto
                binding.textFieldFecha.getEditText().setText(String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth));
            }
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
        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // Establecer la hora seleccionada en el campo de texto
                binding.textFieldHora.getEditText().setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
            }
        }, hourOfDay, minute, true);
        timePickerDialog.show();
    }

    // Método para crear una alarma y un recordatorio
    private void crearAlarmaYRecordatorio() {
        // Obtener los valores de los campos de fecha, hora y nota
        String fecha = binding.textFieldFecha.getEditText().getText().toString();
        String hora = binding.textFieldHora.getEditText().getText().toString();
        String nota = binding.textFieldNota.getEditText().getText().toString();

        // Convertir la fecha y hora en milisegundos
        long tiempoAlarma = obtenerTiempoAlarma(fecha, hora);

        // Crear una intención para el broadcast de la alarma
        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
        intent.putExtra("NOTA", nota); // Pasar la nota como dato extra

        // Crear un PendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE); // O cambia a PendingIntent.FLAG_MUTABLE si es necesario
        // Configurar la alarma
        alarmManager.set(AlarmManager.RTC_WAKEUP, tiempoAlarma, pendingIntent);
        // Mostrar un mensaje de éxito
        Toast.makeText(requireContext(), "Alarma creada con éxito", Toast.LENGTH_SHORT).show();
    }

    // Método para obtener el tiempo de la alarma en milisegundos
    private long obtenerTiempoAlarma(String fecha, String hora) {
        // Dividir la fecha y la hora en año, mes, día, hora y minuto
        String[] fechaPartes = fecha.split("-");
        int year = Integer.parseInt(fechaPartes[0]);
        int month = Integer.parseInt(fechaPartes[1]) - 1; // Restar 1 porque en Calendar los meses empiezan desde 0
        int day = Integer.parseInt(fechaPartes[2]);

        String[] horaPartes = hora.split(":");
        int hourOfDay = Integer.parseInt(horaPartes[0]);
        int minute = Integer.parseInt(horaPartes[1]);

        // Crear un objeto Calendar y establecer la fecha y hora
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hourOfDay, minute);

        // Obtener el tiempo en milisegundos
        return calendar.getTimeInMillis();
    }
}

// Clase para manejar la recepción de la alarma
class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Aquí puedes manejar la acción cuando la alarma se active
        String nota = intent.getStringExtra("NOTA");
        Log.d("AlarmReceiver", "¡Alarma activada! Nota: " + nota);
        Toast.makeText(context, "¡Alarma activada! Nota: " + nota, Toast.LENGTH_SHORT).show();
    }
}
