package com.example.diabetescontrol.ui.gallery;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import org.json.JSONObject;
import org.json.JSONException;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.diabetescontrol.AlarmReceiver;
import com.example.diabetescontrol.R;
import com.example.diabetescontrol.databinding.FragmentGalleryBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import androidx.recyclerview.widget.LinearLayoutManager;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private Spinner spinnerType;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        spinnerType = root.findViewById(R.id.spinnerType);

        binding.cosulacitas.setOnClickListener(v -> {
            consultarCitas();
        });


        // Obtener referencia a SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);

        // Configurar Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.citas_medicas, android.R.layout.simple_spinner_item);
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
        binding.buttonCrearAlarma.setOnClickListener(v -> {
            crearAlarma();
            guardarCita();
        });

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

        // Validar el formato de la fecha y la hora
        if (!isValidDateFormat(fecha, "yyyy-MM-dd") || !isValidDateFormat(hora, "HH:mm")) {
            Toast.makeText(requireContext(), "Formato de fecha u hora inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear el título de la alarma
        String titulo = "Cita con: " + tipoConsulta;

        Log.d("GalleryFragment", "Fecha antes de la conversión: " + fecha);
        Log.d("GalleryFragment", "Hora antes de la conversión: " + hora);

        // Convertir la fecha y la hora en milisegundos
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            Date date = dateFormat.parse(fecha + " " + hora);
            if (date != null) {
                calendar.setTime(date);

                // Crear el Intent para la alarma
                Intent intent = new Intent(requireContext(), AlarmReceiver.class);
                intent.putExtra("titulo", titulo);
                intent.putExtra("nota", nota);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        requireContext(),
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );

                // Configurar la alarma
                AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);

                if (alarmManager != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // Para Android M (6.0) y posteriores, utiliza setExactAndAllowWhileIdle
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    } else {
                        // Para versiones anteriores a Android M, utiliza setExact
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    }
                    Log.d("GalleryFragment", "Alarma creada para: " + calendar.getTime().toString());
                    Toast.makeText(requireContext(), "Alarma creada", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("GalleryFragment", "No se pudo obtener el servicio AlarmManager");
                    Toast.makeText(requireContext(), "No se pudo obtener el servicio AlarmManager", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("GalleryFragment", "Error al convertir la fecha y hora");
                Toast.makeText(requireContext(), "Error al convertir la fecha y hora", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("GalleryFragment", "ParseException: " + e.getMessage());
            Toast.makeText(requireContext(), "Error al convertir la fecha y hora", Toast.LENGTH_SHORT).show();
        }
    }

    // Método para guardar la cita en la base de datos
    private void guardarCita() {
        String tipoConsulta = spinnerType.getSelectedItem().toString();
        String fecha = binding.textFieldFecha.getEditText().getText().toString();
        String hora = binding.textFieldHora.getEditText().getText().toString();
        String nota = binding.textFieldNota.getEditText().getText().toString();
        long idUsuario = sharedPreferences.getLong("idUsuario", -1);

        if (!tipoConsulta.isEmpty() && !fecha.isEmpty() && !hora.isEmpty() && idUsuario != -1) {
            new InsertarRegistroCitaTask(requireContext()).execute(tipoConsulta, fecha, hora, String.valueOf(idUsuario), nota);
        } else {
            Toast.makeText(requireContext(), "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    // Clase para insertar el registro de la cita en segundo plano
    private static class InsertarRegistroCitaTask extends AsyncTask<String, Void, String> {
        private WeakReference<Context> contextRef;

        InsertarRegistroCitaTask(Context context) {
            contextRef = new WeakReference<>(context);
        }

        @Override
        protected String doInBackground(String... params) {
            String tipoConsulta = params[0];
            String fecha = params[1];
            String hora = params[2];
            String pacienteID = params[3];
            String nota = params[4];


            String urlServidor = "http://10.0.2.2:8080/conexiondevelop/registrocitas.php";

            try {
                // Crea la conexión HTTP
                URL url = new URL(urlServidor);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Configura la conexión
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                // Parámetros que enviarás al servidor PHP
                String parametros = "tipoConsulta=" + URLEncoder.encode(tipoConsulta, "UTF-8") +
                        "&fecha=" + URLEncoder.encode(fecha, "UTF-8") +
                        "&hora=" + URLEncoder.encode(hora, "UTF-8") +
                        "&pacienteID=" + pacienteID +
                        "&nota=" + URLEncoder.encode(nota, "UTF-8");

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
                return null; // Devuelve null si hay una excepción
            }
        }


        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Context context = contextRef.get();
            if (context != null) {
                if (response != null) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        String message = jsonResponse.getString("message");

                        if (success && "Success".equals(message)) { // Verifica si el mensaje es "Success"
                            Toast.makeText(context, "Cita guardada exitosamente", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error al guardar la cita: " + message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error al procesar la respuesta del servidor", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Error al guardar la cita", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // Método para validar el formato de fecha u hora
    private boolean isValidDateFormat(String value, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(value);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private void consultarCitas() {
        // Obtener el ID de usuario almacenado en SharedPreferences
        long idUsuario = sharedPreferences.getLong("idUsuario", -1);

        // Verificar si se pudo obtener el ID de usuario
        if (idUsuario != -1) {
            // Obtener la fecha actual del sistema
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String fechaConsulta = dateFormat.format(calendar.getTime());

            // Configurar el LayoutManager (puedes usar LinearLayoutManager u otro que desees)
            LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
            binding.recyclerViewcita.setLayoutManager(layoutManager);
            // Realizar la consulta en segundo plano
            new ConsultarRegistroCitaTask(requireContext(), binding.recyclerViewcita).execute(String.valueOf(idUsuario), fechaConsulta);
        } else {
            Toast.makeText(requireContext(), "No se pudo obtener el ID de usuario", Toast.LENGTH_SHORT).show();
        }
    }

    // Clase para consultar los registros de citas en segundo plano
    private static class ConsultarRegistroCitaTask extends AsyncTask<String, Void, String> {
        private WeakReference<Context> contextRef;
        private RecyclerView recyclerView;

        ConsultarRegistroCitaTask(Context context, RecyclerView recyclerView) {
            contextRef = new WeakReference<>(context);
            this.recyclerView = recyclerView;
        }
        @Override
        protected String doInBackground(String... params) {
            String idUsuario = params[0];
            String fechaConsulta = params[1];

            String urlServidor = "http://10.0.2.2:8080/conexiondevelop/consultarcitas.php";

            try {
                URL url = new URL(urlServidor);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                String parametros = "idUsuario=" + URLEncoder.encode(idUsuario, "UTF-8") +
                        "&fechaConsulta=" + URLEncoder.encode(fechaConsulta, "UTF-8");

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

                connection.disconnect();

                return response.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Context context = contextRef.get();
            if (context != null) {
                if (result != null) {
                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        // Crear una lista para almacenar los datos de las citas
                        ArrayList<Cita> citasList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String fecha = jsonObject.getString("fecha");
                            String hora = jsonObject.getString("hora");
                            String tipoConsulta = jsonObject.getString("tipoConsulta");
                            String nota = jsonObject.getString("nota");
                            // Agregar los datos de la cita a la lista
                            citasList.add(new Cita(fecha, hora, tipoConsulta, nota));
                        }
                        // Configurar el adaptador con la lista de citas
                        CitaAdapter adapter = new CitaAdapter(citasList);
                        recyclerView.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Error al consultar los registros de citas", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}

