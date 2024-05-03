package com.example.diabetescontrol;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.os.AsyncTask;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.core.util.PatternsCompat;
import com.example.diabetescontrol.Sign_inActivity;
import com.example.diabetescontrol.databinding.ActivityLoginBinding;
import com.example.diabetescontrol.ui.home.HomeFragment;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import androidx.biometric.BiometricManager;
import androidx.annotation.NonNull;

import java.util.Calendar;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Button registrarButton = findViewById(R.id.btnRegistrar);
        registrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, Sign_inActivity.class);
                startActivity(intent);
            }
        });

        binding.btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndLogin();
            }
        });

        ImageButton huellaButton = findViewById(R.id.huella);
        huellaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBiometricPrompt();
            }
        });

        String saludo = getGreeting();
        Toast.makeText(this, saludo, Toast.LENGTH_SHORT).show();
    }

    private String getGreeting() {
        Calendar fecha = Calendar.getInstance();
        int hours = fecha.get(Calendar.HOUR_OF_DAY);
        if (hours > 0 && hours < 12) {
            return "Buenos días";
        } else if (hours >= 12 && hours < 18) {
            return "Buenas tardes";
        } else {
            return "Buenas noches";
        }
    }

    private void validateAndLogin() {
        boolean[] result = {validateEmail(), validatePassword()};
        if (!containsFalse(result)) {
            String email = binding.textFieldEmail.getEditText().getText().toString();
            String password = binding.textFieldPassword.getEditText().getText().toString();
            new LoginTask().execute(email, password);
        }
    }

    private boolean containsFalse(boolean[] array) {
        for (boolean item : array) {
            if (!item) {
                return true;
            }
        }
        return false;
    }

    private boolean validateEmail() {
        String email = binding.textFieldEmail.getEditText().getText().toString();
        if (email.isEmpty()) {
            showAlertDialog("Advertencia", "El campo Email no puede estar vacío. Ingrese correo.");
            return false;
        } else if (!PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()) {
            showAlertDialog("Advertencia", "Correo no válido, debe tener estructura de correo electrónico.");
            return false;
        } else {
            binding.textFieldEmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String password = binding.textFieldPassword.getEditText().getText().toString();
        Pattern passwordRegex = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&!_+*=])(?=\\S+$).{4,}$");
        if (password.isEmpty()) {
            showAlertDialog("Advertencia", "El campo Password no puede estar vacío. Ingrese contraseña.");
            return false;
        } else if (!passwordRegex.matcher(password).matches()) {
            showAlertDialog("Advertencia", "La contraseña debe contener al menos una mayúscula, una minúscula, un número, un carácter especial y tener al menos 4 caracteres.");
            return false;
        } else {
            binding.textFieldPassword.setError(null);
            return true;
        }
    }

    private void showBiometricPrompt() {
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                biometricPrompt = new BiometricPrompt(this, ContextCompat.getMainExecutor(this), new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        Toast.makeText(LoginActivity.this, "Error de autenticación: " + errString, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        Toast.makeText(LoginActivity.this, "Autenticación exitosa", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, Navegacion.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(LoginActivity.this, "Autenticación fallida", Toast.LENGTH_SHORT).show();
                    }
                });

                promptInfo = new BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Autenticación requerida")
                        .setSubtitle("Inicia sesión usando tu huella dactilar")
                        .setNegativeButtonText("Cancelar")
                        .build();

                biometricPrompt.authenticate(promptInfo);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(LoginActivity.this, "El dispositivo no tiene hardware de autenticación biométrica", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(LoginActivity.this, "El hardware de autenticación biométrica no está disponible", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(LoginActivity.this, "No hay huellas dactilares registradas en el dispositivo", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("Aceptar", null);
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String email = params[0];
            String password = params[1];
            String urlServidor = "http://10.0.2.2:8080/conexiondevelop/login.php";
            try {
                URL url = new URL(urlServidor);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                String parametros = "email=" + URLEncoder.encode(email, "UTF-8") +
                        "&password=" + URLEncoder.encode(password, "UTF-8");
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

                // Log para verificar la respuesta del servidor
                Log.d("LoginActivity", "Respuesta del servidor: " + response.toString());

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
                Log.d("LoginActivity", "Respuesta del servidor en onPostExecute: " + response);

                if (response.equals("Conexión exitosaSuccess")) {
                    Toast.makeText(LoginActivity.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, Navegacion.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Inicio de sesión fallido. Por favor, verifica tus credenciales.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Error al conectarse al servidor", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
