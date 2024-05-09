package com.example.diabetescontrol;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.content.Context;

import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;

public class RegistroPeso extends Fragment {

    private EditText weightInput, heightInput, fechaInput, weightInputPeso;
    private TextView resultText, descriptionText;

    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_registro_peso, container, false);
        initViews(view);

        Button calculateButton = view.findViewById(R.id.btnCalcular);
        Button clearButton = view.findViewById(R.id.btnClearData);
        Button saveButton = view.findViewById(R.id.btnGuardar);

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calcularIMC();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpiar();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarRegistroPeso();
            }
        });

        // Obtener referencia a SharedPreferences
        sharedPreferences = getActivity().getSharedPreferences("loginPrefs", getActivity().MODE_PRIVATE);

        // Configurar el clic del campo de fecha para abrir el DatePickerDialog
        fechaInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarCalendario();
            }
        });

        return view;
    }

    public void initViews(View view) {
        weightInput = view.findViewById(R.id.lblWeightValue);
        heightInput = view.findViewById(R.id.lblHeightValue);
        fechaInput = view.findViewById(R.id.fechaInput);
        resultText = view.findViewById(R.id.lblIMCValue);
        descriptionText = view.findViewById(R.id.lblDescription);
        weightInputPeso = view.findViewById(R.id.lblWeightValue1);
    }

    private void calcularIMC() {
        double weight = Double.parseDouble(weightInput.getText().toString());
        double height = Double.parseDouble(heightInput.getText().toString());
        double bmi = weight / (height * height);

        resultText.setText(String.format(getString(R.string.bmi_result_format), bmi));

        String description;
        if (bmi <= 0) {
            description = getString(R.string.zero_values);
        } else if (bmi >= 1 && bmi < 18.5) {
            description = getString(R.string.under_weight);
        } else if (bmi < 25) {
            description = getString(R.string.normal_weight);
        } else if (bmi < 30) {
            description = getString(R.string.overweight);
        } else if (bmi < 35) {
            description = getString(R.string.type_1_obesity);
        } else if (bmi < 40) {
            description = getString(R.string.type_2_obesity);
        } else {
            description = getString(R.string.type_3_obesity);
        }
        descriptionText.setText(description);
    }

    public void limpiar() {
        weightInput.setText("");
        heightInput.setText("");
        resultText.setText("");
        descriptionText.setText("");
    }

    public void guardarRegistroPeso() {
        String peso = weightInputPeso.getText().toString();
        Log.d("RegistroPeso", "Peso obtenido del EditText: " + peso);
        String fecha = fechaInput.getText().toString(); // Aquí obtienes la fecha del campo
        long idUsuario = sharedPreferences.getLong("idUsuario", -1);

        // Agregar un registro para verificar el ID del usuario obtenido
        Log.d("RegistroPeso", "ID del usuario obtenido de SharedPreferences: " + idUsuario);

        // Verificar si el peso es un valor válido antes de continuar
        try {
            double pesoValue = Double.parseDouble(peso);
            if (pesoValue <= 0) {
                Toast.makeText(getActivity(), "Error: El peso debe ser mayor que cero", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            // Capturar excepción si el valor de peso no es numérico
            Toast.makeText(getActivity(), "Error: El peso debe ser un número válido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (idUsuario != -1) {
            // Ejecutar la tarea asíncrona para insertar el registro de peso
            new InsertarRegistroPesoTask().execute(peso, fecha, String.valueOf(idUsuario));
        } else {
            Toast.makeText(getActivity(), "Error: No se pudo obtener el ID del paciente", Toast.LENGTH_SHORT).show();
        }
    }




    private class InsertarRegistroPesoTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String peso = String.valueOf(params[0]); // Convertir double a String
            String fecha = params[1];
            String pacienteID = params[2];

            // URL del archivo PHP en tu servidor para insertar registros de peso
            String urlServidor = "http://10.0.2.2:8080/conexiondevelop/registropeso.php";

            try {
                // Crea la conexión HTTP
                URL url = new URL(urlServidor);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Configura la conexión
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                // Parámetros que enviarás al servidor PHP
                String parametros = "peso=" + URLEncoder.encode(peso, "UTF-8") +
                        "&fecha=" + URLEncoder.encode(fecha, "UTF-8") +
                        "&pacienteID=" + pacienteID;



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
            if (response != null) {
                // Muestra un mensaje dependiendo de la respuesta del servidor
                if (response.equals("Success")) {
                    Toast.makeText(getActivity(), "Registro de peso guardado exitosamente", Toast.LENGTH_SHORT).show();
                    // Aquí puedes realizar cualquier otra acción necesaria después de guardar el registro de peso
                } else {
                    Toast.makeText(getActivity(), "Error al guardar el registro de peso", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Error al guardar el registro de peso", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void mostrarCalendario() {
        // Obtener la fecha actual
        Calendar calendar = Calendar.getInstance();
        int año = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        // Crear el DatePickerDialog y mostrarlo
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Establecer la fecha seleccionada en el EditText
                        String fechaSeleccionada = String.format("%04d-%02d-%02d", year, (monthOfYear + 1), dayOfMonth);
                        fechaInput.setText(fechaSeleccionada);
                    }
                }, año, mes, dia);
        datePickerDialog.show();
    }


}


