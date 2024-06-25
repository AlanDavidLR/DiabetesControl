package com.example.diabetescontrol;

import android.app.TimePickerDialog;
import android.os.AsyncTask;
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
import android.widget.Toast;
import java.util.Calendar;
import android.widget.EditText;
import android.widget.CheckBox;
import android.util.Log;
import java.util.ArrayList;
import android.widget.Spinner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONArray;
import android.content.SharedPreferences;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.net.URLEncoder;
import org.json.JSONException;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class RegistroTomaMedicamentos extends Fragment {

    private static final String TAG = "RegistroTomaMed";

    private int hour;
    private int minute;
    private RecyclerView recyclerView;
    private Button consultarMedicamentosButton;
    private ArrayList<Medicamento> medicamentosList;
    private MedicamentoAdapter adapter;
    private TextView textViewMedicineTime;
    private EditText editTextMedName;
    private EditText textViewDoseQuantity;
    private Spinner spinnerDoseUnits;
    private CheckBox checkBoxSunday, checkBoxMonday, checkBoxTuesday, checkBoxWednesday,
            checkBoxThursday, checkBoxFriday, checkBoxSaturday, checkBoxEveryDay;
    private SharedPreferences sharedPreferences;

    public RegistroTomaMedicamentos() {
        // Required empty public constructor
    }

    private void clearFields() {
        // Limpiar campos de texto
        editTextMedName.setText("");
        textViewDoseQuantity.setText("");
        textViewMedicineTime.setText("");

        // Deseleccionar checkboxes
        checkBoxSunday.setChecked(false);
        checkBoxMonday.setChecked(false);
        checkBoxTuesday.setChecked(false);
        checkBoxWednesday.setChecked(false);
        checkBoxThursday.setChecked(false);
        checkBoxFriday.setChecked(false);
        checkBoxSaturday.setChecked(false);
        checkBoxEveryDay.setChecked(false);

        // Resetear el spinner a la primera opción (puedes ajustar esto si es necesario)
        spinnerDoseUnits.setSelection(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registro_toma_medicamentos, container, false);

        sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        textViewMedicineTime = view.findViewById(R.id.tv_medicine_time);
        editTextMedName = view.findViewById(R.id.edit_med_name);
        textViewDoseQuantity = view.findViewById(R.id.tv_dose_quantity);
        spinnerDoseUnits = view.findViewById(R.id.spinner_dose_units);
        checkBoxSunday = view.findViewById(R.id.dv_sunday);
        checkBoxMonday = view.findViewById(R.id.dv_monday);
        checkBoxTuesday = view.findViewById(R.id.dv_tuesday);
        checkBoxWednesday = view.findViewById(R.id.dv_wednesday);
        checkBoxThursday = view.findViewById(R.id.dv_thursday);
        checkBoxFriday = view.findViewById(R.id.dv_friday);
        checkBoxSaturday = view.findViewById(R.id.dv_saturday);
        checkBoxEveryDay = view.findViewById(R.id.every_day);

        // Aplicar la animación al TextView
        Animation fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        textViewMedicineTime.startAnimation(fadeInAnimation);

        textViewMedicineTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });
        recyclerView = view.findViewById(R.id.consulmed);
        consultarMedicamentosButton = view.findViewById(R.id.consultamed);

        medicamentosList = new ArrayList<>();
        adapter = new MedicamentoAdapter(medicamentosList);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.HORIZONTAL));
        recyclerView.setLayoutManager(layoutManager);;
        recyclerView.setAdapter(adapter);

        consultarMedicamentosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consultarMedicamentos();
            }
        });
        // Configurar botón Borrar
        Button btnBorrar = view.findViewById(R.id.btnBorrar);
        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFields();
            }
        });

        Button btnCrearR = view.findViewById(R.id.btnCrearR);
        btnCrearR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlarm();
                saveDataToDatabase();
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
        String dosis = textViewDoseQuantity.getText().toString();
        String tipoDosis = spinnerDoseUnits.getSelectedItem().toString();

        if (medicamento.isEmpty() || dosis.isEmpty() || tipoDosis.isEmpty()) {
            Toast.makeText(getContext(), "Ninguna casilla puede estar vacía", Toast.LENGTH_SHORT).show();
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
        boolean todosLosDias = checkBoxEveryDay.isChecked();
        if (todosLosDias) {
            // Si se marca "Todos los días", establecer todos los días de la semana
            for (int i = Calendar.SUNDAY; i <= Calendar.SATURDAY; i++) {
                dias.add(i);
            }
        } else {
            // Establecer solo los días seleccionados
            for (int i = 0; i < diasSeleccionados.length; i++) {
                if (diasSeleccionados[i]) {
                    dias.add(i + 1); // Ajustar a los valores de Calendar
                }
            }
        }

        String mensajeAlarma = "Hora de tomar tu medicamento: " + medicamento + " (" + dosis + " " + tipoDosis + ")";
        String mensajeNotificacion = mensajeAlarma + ". La dosis es: " + dosis + ". Tipo de medicamento: " + tipoDosis;

        // Crear la alarma del sistema
        Intent alarmIntent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_HOUR, hour)
                .putExtra(AlarmClock.EXTRA_MINUTES, minute)
                .putExtra(AlarmClock.EXTRA_MESSAGE, mensajeAlarma)
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
        notificationIntent.putExtra("nota", mensajeNotificacion);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

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

    private void saveDataToDatabase() {
        String medicamento = editTextMedName.getText().toString();
        String dosis = textViewDoseQuantity.getText().toString();
        String tipoMedicamento = spinnerDoseUnits.getSelectedItem().toString();  // Actualizado
        String hora = textViewMedicineTime.getText().toString();

        if (medicamento.isEmpty() || dosis.isEmpty() || tipoMedicamento.isEmpty() || hora.isEmpty()) {
            Toast.makeText(getContext(), "Ninguna casilla puede estar vacía", Toast.LENGTH_SHORT).show();
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

        boolean todosLosDias = checkBoxEveryDay.isChecked();

        // Obtener idUsuario de SharedPreferences
        long idUsuario = sharedPreferences.getLong("idUsuario", -1);

        // Crear JSON con los datos
        try {
            JSONObject json = new JSONObject();
            json.put("medicamento", medicamento);
            json.put("dosis", dosis);
            json.put("tipoMedicamento", tipoMedicamento);  // Actualizado
            json.put("hora", hora);
            json.put("domingo", diasSeleccionados[0]);
            json.put("lunes", diasSeleccionados[1]);
            json.put("martes", diasSeleccionados[2]);
            json.put("miercoles", diasSeleccionados[3]);
            json.put("jueves", diasSeleccionados[4]);
            json.put("viernes", diasSeleccionados[5]);
            json.put("sabado", diasSeleccionados[6]);
            json.put("todosLosDias", todosLosDias);
            json.put("idUsuario", idUsuario);

            Log.d(TAG, "Datos enviados al servidor: " + json.toString());

            // Enviar datos al servidor
            new SendPostRequest().execute(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class SendPostRequest extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String jsonInput = params[0];
            try {
                URL url = new URL("http://glucocontrol.atwebpages.com/registromedicamento.php"); // Cambiar a la URL correcta
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInput.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    Log.d(TAG, "Respuesta del servidor: " + response.toString());
                    return response.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Toast.makeText(getContext(), "Registro guardado exitosamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Error al guardar el registro", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void consultarMedicamentos() {
        long idUsuario = sharedPreferences.getLong("idUsuario", -1);
        if (idUsuario != -1) {
            new ConsultarMedicamentosTask().execute(String.valueOf(idUsuario));
        } else {
            Toast.makeText(getContext(), "No se encontró el idUsuario", Toast.LENGTH_SHORT).show();
        }
    }

    private class ConsultarMedicamentosTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String idUsuario = params[0];

            StringBuilder result = new StringBuilder();
            HttpURLConnection urlConnection = null;

            try {
                URL url = new URL("http://glucocontrol.atwebpages.com/consultamedicamento.php");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                String parametros = "idUsuario=" + URLEncoder.encode(idUsuario, "UTF-8");
                OutputStream outputStream = urlConnection.getOutputStream();
                outputStream.write(parametros.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    reader.close();
                    return result.toString();
                } else {
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
                    String errorLine;
                    StringBuilder errorResponse = new StringBuilder();
                    while ((errorLine = errorReader.readLine()) != null) {
                        errorResponse.append(errorLine);
                    }
                    errorReader.close();
                    Log.e(TAG, "Error response from server: " + errorResponse.toString());
                    return null;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error al obtener datos: " + e.getMessage());
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result!= null) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    if (jsonArray.length() == 0) {
                        Toast.makeText(getContext(), "Usted no cuenta con medicamentos registrados en este momento", Toast.LENGTH_SHORT).show();
                    } else {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String nombreMedicamento = jsonObject.getString("nombreMedicamento");
                            String diasToma = jsonObject.getString("diasToma");
                            String hora = jsonObject.getString("hora");
                            String dosis = jsonObject.getString("dosis");
                            String tipoMedicamento = jsonObject.getString("tipoMedicamento");

                            Medicamento medicamento = new Medicamento(nombreMedicamento, diasToma, hora, dosis, tipoMedicamento);
                            medicamentosList.add(medicamento);
                        }

                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al procesar los datos del servidor", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "No se recibió respuesta del servidor", Toast.LENGTH_SHORT).show();
            }
        }
    }
}






