package com.example.diabetescontrol;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.core.util.PatternsCompat;
import com.example.diabetescontrol.Sign_inActivity;
import com.example.diabetescontrol.databinding.ActivityLoginBinding;
import com.example.diabetescontrol.ui.home.HomeFragment;

import java.util.Calendar;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
                validate();
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

    private void validate() {
        boolean[] result = {validateEmail(), validatePassword()};
        if (!containsFalse(result)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
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
        biometricPrompt = new BiometricPrompt(this,
                ContextCompat.getMainExecutor(this),
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(
                            BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);

                        Toast.makeText(LoginActivity.this, "Autenticación biométrica exitosa", Toast.LENGTH_SHORT).show();

                        // Agrega un registro aquí para verificar si se ejecuta este método correctamente
                        Log.d("BiometricPrompt", "onAuthenticationSucceeded");

                        Intent intent = new Intent(LoginActivity.this, Navegacion.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);

                        // Agrega un registro aquí para verificar si se ejecuta este método correctamente
                        Log.e("BiometricPrompt", "onAuthenticationError: " + errString);
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();

                        // Agrega un registro aquí para verificar si se ejecuta este método correctamente
                        Log.d("BiometricPrompt", "onAuthenticationFailed");
                    }
                });


        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Autenticación biométrica")
                .setSubtitle("Usa tu huella digital para iniciar sesión")
                .setNegativeButtonText("Cancelar")
                .build();

        biometricPrompt.authenticate(promptInfo);
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
}

