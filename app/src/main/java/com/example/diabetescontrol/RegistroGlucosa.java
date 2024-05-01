package com.example.diabetescontrol;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.DatePicker;

import java.util.Calendar;

public class RegistroGlucosa extends Fragment {

    private EditText horaControl;
    private EditText diaControl;
    private Spinner spinnerType;

    private int mYear, mMonth, mDay, mHour, mMinute;

    public RegistroGlucosa() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_registro_glucosa, container, false);

        // Referencias a los elementos de la interfaz
        horaControl = root.findViewById(R.id.horaControl);
        diaControl = root.findViewById(R.id.diaControl);
        spinnerType = root.findViewById(R.id.spinnerType);

        // Configurar Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.tipos_toma_glucosa, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        // Configurar clic en EditText para seleccionar hora
        horaControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Mostrar diálogo de selección de hora
                TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                horaControl.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        // Configurar clic en EditText para seleccionar fecha
        diaControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                // Mostrar diálogo de selección de fecha
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                diaControl.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        // Configurar clic en botón Guardar
        Button btnGuardar = root.findViewById(R.id.modificarControl);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarControl();
            }
        });

        // Configurar clic en botón Cancelar
        Button btnCancelar = root.findViewById(R.id.cancelarGuardarControl);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelarGuardarControl();
            }
        });

        return root;
    }

    private void guardarControl() {
        // Aquí puedes implementar la lógica para guardar el registro de glucosa
        Toast.makeText(requireContext(), "Registro guardado", Toast.LENGTH_SHORT).show();
    }

    private void cancelarGuardarControl() {
        // Aquí puedes implementar la lógica para cancelar el registro de glucosa
        Toast.makeText(requireContext(), "Registro cancelado", Toast.LENGTH_SHORT).show();
    }
}
