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
import android.widget.ImageView;
import android.content.SharedPreferences;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import androidx.fragment.app.Fragment;
import android.content.Context;

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
import android.widget.ImageButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.github.mikephil.charting.components.Description;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class RegistroGlucosa extends Fragment {

    private EditText horaControl;
    private EditText diaControl;
    private EditText fechainig;
    private EditText fechafing ;
    private Spinner spinnerType;
    private EditText glucosa;
    private SharedPreferences sharedPreferences;
    private ImageView imageView2;
    private Button buttonShowImage;
    private LineChart lineChart;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private ImageButton eliminargfButton;

    public RegistroGlucosa() {
        // Constructor vacío
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_registro_glucosa, container, false);

        // Referencias a los elementos de la interfaz
        horaControl = root.findViewById(R.id.horaControl);
        diaControl = root.findViewById(R.id.diaControl);
        fechainig = root.findViewById(R.id.fechainig); // Referencia a fechainig
        fechafing = root.findViewById(R.id.fechafing);
        spinnerType = root.findViewById(R.id.spinnerType);
        glucosa = root.findViewById(R.id.glucosa);
        imageView2 = root.findViewById(R.id.imageView2);
        buttonShowImage = root.findViewById(R.id.buttonShowImage);
        lineChart = root.findViewById(R.id.lineChartglucosa);

        // Configurar Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.tipos_toma_glucosa, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        // Obtener referencia a SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);

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
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Formatear la fecha como "YYYY-MM-DD"
                                String formattedDate = String.format("%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                                diaControl.setText(formattedDate);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        // Configurar clic en botón Guardar
        Button btnGuardar = root.findViewById(R.id.guardargluco);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarControl();
            }
        });

        // Configurar clic en botón "Saber más"
        buttonShowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageView2.getVisibility() == View.VISIBLE) {
                    imageView2.setVisibility(View.GONE);
                } else {
                    imageView2.setVisibility(View.VISIBLE);
                }
            }
        });

        // Configurar clic en botón Consultar
        Button btnConsultar = root.findViewById(R.id.btnConsultag);
        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consultarGlucosa();
            }
        });
        // Configurar clic en EditText para seleccionar fecha de inicio
        fechainig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(fechainig); // Llama a la función showDatePicker pasando el EditText correspondiente
            }
        });

        // Configurar clic en EditText para seleccionar fecha de fin
        fechafing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(fechafing); // Llama a la función showDatePicker pasando el EditText correspondiente
            }
        });

        // Inicializar y configurar el listener para eliminargfButton
        eliminargfButton = root.findViewById(R.id.eliminargf);
        eliminargfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Limpiar los EditText fechainig y fechafing
                fechainig.setText("");
                fechafing.setText("");
            }
        });

        Button btnBorrarConsultar = root.findViewById(R.id.btnBorrarConsultag);
        btnBorrarConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lineChart.clear();
                lineChart.invalidate();
            }
        });


        return root;
    }

    // Función para mostrar el selector de fecha
    public void showDatePicker(final EditText editText) { // Pasa el EditText como parámetro
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        // Mostrar diálogo de selección de fecha
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Formatear la fecha como "YYYY-MM-DD"
                        String formattedDate = String.format("%04d-%02d-%02d", year, monthOfYear + 1, dayOfMonth);
                        editText.setText(formattedDate); // Usa el EditText pasado como parámetro
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void guardarControl() {
        String hora = horaControl.getText().toString();
        String fecha = diaControl.getText().toString();
        String tipoToma = spinnerType.getSelectedItem().toString();
        long idUsuario = sharedPreferences.getLong("idUsuario", -1);
        String nivelGlucosa = glucosa.getText().toString();

        if (!hora.isEmpty() && !fecha.isEmpty() && !tipoToma.isEmpty() && idUsuario != -1) {
            new InsertarRegistroGlucosaTask(requireContext()).execute(hora, fecha, tipoToma, String.valueOf(idUsuario), nivelGlucosa);
        } else {
            Toast.makeText(requireContext(), "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void consultarGlucosa() {
        long idUsuario = sharedPreferences.getLong("idUsuario", -1);
        if (idUsuario != -1) {
            String fechaInicio = fechainig.getText().toString(); // Utiliza fechainig para fechaInicio
            String fechaFin = fechafing.getText().toString(); // Utiliza fechafing para fechaFin
            new ConsultarRegistroGlucosaTask(requireContext(), lineChart).execute(String.valueOf(idUsuario), fechaInicio, fechaFin);
        } else {
            Toast.makeText(requireContext(), "Error al obtener el ID del usuario", Toast.LENGTH_SHORT).show();
        }
    }

    private static class InsertarRegistroGlucosaTask extends AsyncTask<String, Void, String> {
        private WeakReference<Context> contextRef;

        InsertarRegistroGlucosaTask(Context context) {
            contextRef = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(String... params) {
            String hora = params[0];
            String fecha = params[1];
            String tipoToma = params[2];
            String pacienteID = params[3];
            String nivelGlucosa =  params[4];

            String urlServidor = "http://glucocontrol.atwebpages.com/registroglucosa.php";

            try {
                // Crea la conexión HTTP
                URL url = new URL(urlServidor);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Configura la conexión
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                // Parámetros que se nviarán al servidor PHP
                String parametros = "hora=" + URLEncoder.encode(hora, "UTF-8") +
                        "&fecha=" + URLEncoder.encode(fecha, "UTF-8") +
                        "&tipoToma=" + URLEncoder.encode(tipoToma, "UTF-8") +
                        "&pacienteID=" + pacienteID +
                        "&nivelGlucosa=" + nivelGlucosa;

                // Envía los datos al servidor
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(parametros.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                // Lee la respuesta del servidor
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }
                bufferedReader.close();
                Log.d("InsertarRegistroGlucosa", "Respuesta del servidor: " + response.toString());
                // Cierra la conexión
                connection.disconnect();

                return response.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Context context = contextRef.get();
            if (context != null) {
                if (response != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if (status.equals("Success")) {
                            Toast.makeText(context, "Registro de glucosa guardado exitosamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error al guardar el registro de glucosa", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error al parsear la respuesta del servidor", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Error al guardar el registro de glucosa", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private static class ConsultarRegistroGlucosaTask extends AsyncTask<String, Void, String> {
        private WeakReference<Context> contextRef;
        private WeakReference<LineChart> chartRef;

        ConsultarRegistroGlucosaTask(Context context, LineChart chart) {
            contextRef = new WeakReference<>(context);
            chartRef = new WeakReference<>(chart);
        }

        @Override
        protected String doInBackground(String... params) {
            String idUsuario = params[0];
            String fechaInicio = params[1];
            String fechaFin = params[2];

            String urlServidor = "http://glucocontrol.atwebpages.com/graficaglucosa.php";

            try {
                URL url = new URL(urlServidor);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                // Parámetros que enviará al servidor PHP
                String parametros = "idUsuario=" + URLEncoder.encode(idUsuario, "UTF-8") +
                        "&fechaInicio=" + URLEncoder.encode(fechaInicio, "UTF-8") +
                        "&fechaFin=" + URLEncoder.encode(fechaFin, "UTF-8");

                // Log de los parámetros enviados al servidor
                Log.d("ConsultarRegistroGlucosa", "Parámetros enviados: " + parametros);

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(parametros.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }
                bufferedReader.close();

                // Log de la respuesta obtenida del servidor
                Log.d("ConsultarRegistroGlucosa", "Respuesta del servidor: " + response.toString());

                connection.disconnect();

                return response.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Context context = contextRef.get();
            LineChart lineChart = chartRef.get();
            if (context != null && lineChart != null) {
                if (response != null) {
                    ArrayList<Entry> entries = parsearDatos(response);
                    ArrayList<String> fechas = parsearFechas(response);
                    setupChart(lineChart, entries, fechas);
                } else {
                    Toast.makeText(context, "Error al consultar los registros de glucosa", Toast.LENGTH_SHORT).show();
                }
            }
        }

        private static ArrayList<Entry> parsearDatos(String response) {
            ArrayList<Entry> entries = new ArrayList<>();
            ArrayList<String> fechas = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String glucosaString = jsonObject.getString("glucosa");
                    float glucosaFloat = Float.parseFloat(glucosaString);
                    String fechaString = jsonObject.getString("fecha");
                    entries.add(new Entry(i, glucosaFloat));
                    fechas.add(fechaString);
                }
                // Ordenar los datos por fecha
                Collections.sort(fechas, new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        try {
                            Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(s1);
                            Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(s2);
                            return date1.compareTo(date2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    }
                });
                // Reordenar los entries según la orden de las fechas
                ArrayList<Entry> entriesOrdered = new ArrayList<>();
                for (String fecha : fechas) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String fechaString = jsonObject.getString("fecha");
                        if (fechaString.equals(fecha)) {
                            String glucosaString = jsonObject.getString("glucosa");
                            float glucosaFloat = Float.parseFloat(glucosaString);
                            entriesOrdered.add(new Entry(entriesOrdered.size(), glucosaFloat));
                            break;
                        }
                    }
                }
                return entriesOrdered;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return entries;
        }

        private static ArrayList<String> parsearFechas(String response) {
            ArrayList<String> fechas = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String fechaString = jsonObject.getString("fecha");
                    fechas.add(fechaString);
                }
                // Ordenar las fechas
                Collections.sort(fechas, new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        try {
                            Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(s1);
                            Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(s2);
                            return date1.compareTo(date2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return fechas;
        }

        private static void setupChart(LineChart lineChart, ArrayList<Entry> entries, ArrayList<String> fechas) {
            if (lineChart != null) {
                LineDataSet dataSet = new LineDataSet(entries, "Glucosa");
                dataSet.setColor(Color.BLUE);
                dataSet.setValueTextColor(Color.RED);

                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(dataSet);

                LineData lineData = new LineData(dataSets);

                XAxis xAxis = lineChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

                xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(fechas));
                xAxis.setGranularity(1f); // only intervals of 1 day
                xAxis.setLabelRotationAngle(-45);

                YAxis leftAxis = lineChart.getAxisLeft();
                leftAxis.setTextColor(Color.BLACK);
                leftAxis.setDrawGridLines(true);

                YAxis rightAxis = lineChart.getAxisRight();
                rightAxis.setEnabled(false); // Desactiva el eje derecho

                Description description = new Description();
                description.setText("Fecha");
                lineChart.setDescription(description);

                lineChart.setData(lineData);
                lineChart.invalidate();
            } else {
                Log.e("RegistroGlucosa", "LineChart es nulo. No se puede configurar el gráfico.");
            }
        }
    }
}