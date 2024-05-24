package com.example.diabetescontrol.ui.historialglucosa;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diabetescontrol.R;
import com.example.diabetescontrol.databinding.FragmentHistorialGlucosaBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HistorialGlucosaFragment extends Fragment {

    private FragmentHistorialGlucosaBinding binding;
    private HistorialGlucosaViewModel historialViewModel;
    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private GlucosaAdapter adapter;
    private List<Glucosa> glucosaList = new ArrayList<>();

    private EditText fechiEditText;
    private EditText fechfEditText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHistorialGlucosaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        historialViewModel = new ViewModelProvider(this).get(HistorialGlucosaViewModel.class);
        binding.setViewModel(historialViewModel);
        binding.setLifecycleOwner(this);

        sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);
        long idUsuario = sharedPreferences.getLong("idUsuario", -1);

        adapter = new GlucosaAdapter(glucosaList); // Instanciar y asignar el adaptador antes de configurar el RecyclerView
        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);


        Button consultaButton = root.findViewById(R.id.cosulaglucosa);
        consultaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fechaInicio = fechiEditText.getText().toString();
                String fechaFin = fechfEditText.getText().toString();

                new ConsultarRegistroGlucosaTask().execute(String.valueOf(idUsuario), fechaInicio, fechaFin);
            }
        });

        // Asignar el clic en fechiEditText para mostrar el calendario
        fechiEditText = root.findViewById(R.id.fechi);
        fechiEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(fechiEditText);
            }
        });

        // Asignar el clic en fechfEditText para mostrar el calendario
        fechfEditText = root.findViewById(R.id.fechf);
        fechfEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(fechfEditText);
            }
        });

        return root;
    }

    private void showDatePickerDialog(final EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Mostrar la fecha seleccionada en el EditText
                        String selectedDate = year+"-"+(monthOfYear + 1) +"-"+ dayOfMonth;
                        editText.setText(selectedDate);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private class ConsultarRegistroGlucosaTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String idUsuario = params[0];
            String fechaInicio = params[1];
            String fechaFin = params[2];

            String urlServidor = "http://10.0.2.2:8080/conexiondevelop/consulta_registro_glucosa.php";

            try {
                URL url = new URL(urlServidor);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                String parametros = "idUsuario=" + URLEncoder.encode(idUsuario, "UTF-8") +
                        "&fechaInicio=" + URLEncoder.encode(fechaInicio, "UTF-8") +
                        "&fechaFin=" + URLEncoder.encode(fechaFin, "UTF-8");

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

                Log.d("ConsultarRegistroGlucosa", "Respuesta del servidor: " + response.toString());

                connection.disconnect();

                return response.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                parseAndDisplayData(result);
            } else {
                Toast.makeText(getActivity(), "Error al consultar los registros de glucosa", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void parseAndDisplayData(String result) {
        try {
            JSONArray jsonArray = new JSONArray(result);
            glucosaList.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String fecha = jsonObject.getString("fecha");
                String hora = jsonObject.getString("hora");
                String nivelGlucosa = jsonObject.getString("nivelGlucosa");
                String tipoToma = jsonObject.getString("tipoToma");
                glucosaList.add(new Glucosa(fecha, hora, nivelGlucosa, tipoToma));
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Error al procesar los datos", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
