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


public class RegistroGlucosa extends Fragment {

    private EditText horaControl;
    private EditText diaControl;
    private Spinner spinnerType;
    private EditText glucosa;
    private SharedPreferences sharedPreferences;
    private ImageView imageView2;
    private Button buttonShowImage;

    private int mYear, mMonth, mDay, mHour, mMinute;

    public RegistroGlucosa() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_registro_glucosa, container, false);

        // Referencias a los elementos de la interfaz
        horaControl = root.findViewById(R.id.horaControl);
        diaControl = root.findViewById(R.id.diaControl);
        spinnerType = root.findViewById(R.id.spinnerType);
        glucosa = root.findViewById(R.id.glucosa);
        imageView2 = root.findViewById(R.id.imageView2);
        buttonShowImage = root.findViewById(R.id.buttonShowImage);

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
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
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

        return root;
    }

    private void guardarControl() {
        String hora = horaControl.getText().toString();
        String fecha = diaControl.getText().toString();
        String tipoToma = spinnerType.getSelectedItem().toString();
        long idUsuario = sharedPreferences.getLong("idUsuario", -1);
        String nivelGlucosa = glucosa.getText().toString();

        if (!hora.isEmpty() && !fecha.isEmpty() && !tipoToma.isEmpty() && idUsuario != -1) {
            new InsertarRegistroGlucosaTask(requireContext()).execute(hora, fecha, tipoToma, String.valueOf(idUsuario),nivelGlucosa);
        } else {
            Toast.makeText(requireContext(), "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
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
            String nivelGlucosa = params[4];

            // URL del archivo PHP en tu servidor para insertar registros de glucosa
            String urlServidor = "http://10.0.2.2:8080/conexiondevelop/registroglucosa.php";

            try {
                // Crea la conexión HTTP
                URL url = new URL(urlServidor);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Configura la conexión
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                // Parámetros que enviarás al servidor PHP
                String parametros = "hora=" + URLEncoder.encode(hora, "UTF-8") +
                        "&fecha=" + URLEncoder.encode(fecha, "UTF-8") +
                        "&tipoToma=" + URLEncoder.encode(tipoToma, "UTF-8") +
                        "&pacienteID=" + pacienteID +
                        "&nivelGlucosa=" + nivelGlucosa; // Incluye el nivel de glucosa

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
                    // Muestra un mensaje dependiendo de la respuesta del servidor
                    if (response.equals("Success")) {
                        Toast.makeText(context, "Registro de glucosa guardado exitosamente", Toast.LENGTH_SHORT).show();
                        // Aquí puedes realizar cualquier otra acción necesaria después de guardar el registro de glucosa
                    } else {
                        Toast.makeText(context, "Error al guardar el registro de glucosa", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Error al guardar el registro de glucosa", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



}