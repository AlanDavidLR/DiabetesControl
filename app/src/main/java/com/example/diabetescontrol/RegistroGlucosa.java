package com.example.diabetescontrol;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import java.lang.ref.WeakReference;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RegistroGlucosa extends Fragment {

    private EditText horaControl;
    private EditText diaControl;
    private Spinner spinnerType;
    private LineChart lineChart;

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
        lineChart = root.findViewById(R.id.lineChart);

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

        // Iniciar la tarea asíncrona para obtener los datos de la gráfica
        new ObtenerDatosGraficaTask(this).execute();

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

    private static class ObtenerDatosGraficaTask extends AsyncTask<Void, Void, List<Pair<Float, Float>>> {

        private WeakReference<RegistroGlucosa> registroGlucosaWeakReference;

        ObtenerDatosGraficaTask(RegistroGlucosa registroGlucosa) {
            registroGlucosaWeakReference = new WeakReference<>(registroGlucosa);
        }

        @Override
        protected List<Pair<Float, Float>> doInBackground(Void... voids) {
            // Aquí puedes implementar la lógica para obtener los datos de la gráfica
            // por ejemplo, hacer una petición HTTP a tu servidor o base de datos
            // y parsear la respuesta para obtener los datos necesarios
            // Por ahora, devolvemos datos de ejemplo
            List<Pair<Float, Float>> datos = new ArrayList<>();
            // Agrega datos de ejemplo
            datos.add(new Pair<>(1f, 80f)); // Fecha y nivel de glucosa
            datos.add(new Pair<>(2f, 85f));
            datos.add(new Pair<>(3f, 90f));
            return datos;
        }

        @Override
        protected void onPostExecute(List<Pair<Float, Float>> data) {
            super.onPostExecute(data);
            RegistroGlucosa registroGlucosa = registroGlucosaWeakReference.get();
            if (registroGlucosa != null) {
                registroGlucosa.configurarGrafica(data);
            }
        }
    }

    private void configurarGrafica(List<Pair<Float, Float>> data) {
        // Configuración de la gráfica
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setBackgroundColor(Color.WHITE);

        // Crear una lista de entradas para el gráfico
        List<Entry> entries = new ArrayList<>();
        for (Pair<Float, Float> pair : data) {
            entries.add(new Entry(pair.first, pair.second));
        }

        // Crear un conjunto de datos y asignarle las entradas
        LineDataSet dataSet = new LineDataSet(entries, "Nivel de Glucosa");

        // Personalizar el conjunto de datos si es necesario
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.BLUE);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);

        // Crear una lista de conjuntos de datos y agregar el conjunto de datos
        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);

        // Crear un objeto de tipo LineData y asignarle la lista de conjuntos de datos
        LineData lineData = new LineData(dataSets);

        // Configurar la gráfica con los datos
        lineChart.setData(lineData);

        // Configurar los ejes
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);

        // Actualizar la gráfica
        lineChart.invalidate();
    }
}
