
package com.example.diabetescontrol;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Pattern;

public class Sign_inActivity extends AppCompatActivity {

    private static final String TAG = "Sign_inActivity";

    private TextInputLayout textFieldNombre, textFieldApellido, textFieldNSS, textFieldEmail, textFieldPassword, textFieldConfirmPassword;
    private Button btnCrear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Inicializar vistas
        textFieldNombre = findViewById(R.id.textFieldNombre);
        textFieldApellido = findViewById(R.id.textFieldApellido);
        textFieldNSS = findViewById(R.id.textFieldNSS);
        textFieldEmail = findViewById(R.id.textFieldEmail);
        textFieldPassword = findViewById(R.id.textFieldPassword);
        textFieldConfirmPassword = findViewById(R.id.textFieldConfirmPassword);
        btnCrear = findViewById(R.id.btnCrear);

        // Agregar listener al botón "Crear cuenta"
        btnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validar los campos antes de crear la cuenta
                if (validateFields()) {
                    // Obtener los valores de los campos de texto
                    String nombre = textFieldNombre.getEditText().getText().toString().trim();
                    String apellido = textFieldApellido.getEditText().getText().toString().trim();
                    long nss = Long.parseLong(textFieldNSS.getEditText().getText().toString().trim());
                    String email = textFieldEmail.getEditText().getText().toString().trim();
                    String contrasena = textFieldPassword.getEditText().getText().toString().trim();

                    // Insertar los datos en la base de datos a través de PHP
                    new InsertarDatosTask().execute(nombre, apellido, String.valueOf(nss), email, contrasena);
                }
            }
        });
    }

    // Función para validar todos los campos antes de crear la cuenta
    private boolean validateFields() {
        return validateNombre() && validateApellido() && validateNSS() && validateEmail() && validatePassword() && validateConfirmPassword();
    }

    // Función para validar el nombre
    private boolean validateNombre() {
        String nombre = textFieldNombre.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(nombre)) {
            textFieldNombre.setError("El campo Nombre no puede estar vacío");
            return false;
        } else {
            textFieldNombre.setError(null);
            return true;
        }
    }

    // Función para validar el apellido
    private boolean validateApellido() {
        String apellido = textFieldApellido.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(apellido)) {
            textFieldApellido.setError("El campo Apellido no puede estar vacío");
            return false;
        } else {
            textFieldApellido.setError(null);
            return true;
        }
    }

    // Función para validar el número de seguro social
    private boolean validateNSS() {
        String nssString = textFieldNSS.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(nssString)) {
            textFieldNSS.setError("El campo NSS no puede estar vacío");
            return false;
        } else if (!Pattern.matches("\\d{11}", nssString)) {
            textFieldNSS.setError("El NSS debe contener 11 dígitos");
            return false;
        } else {
            textFieldNSS.setError(null);
            return true;
        }
    }

    // Función para validar el correo electrónico
    private boolean validateEmail() {
        String email = textFieldEmail.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            textFieldEmail.setError("El campo Email no puede estar vacío");
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            textFieldEmail.setError("Correo no válido, debe tener estructura de correo electrónico");
            return false;
        } else {
            textFieldEmail.setError(null);
            return true;
        }
    }

    // Función para validar la contraseña
    private boolean validatePassword() {
        String password = textFieldPassword.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            textFieldPassword.setError("El campo Contraseña no puede estar vacío");
            return false;
        } else if (password.length() < 8) {
            textFieldPassword.setError("La contraseña debe tener al menos 8 caracteres");
            return false;
        } else if (!Pattern.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", password)) {
            textFieldPassword.setError("La contraseña debe contener al menos un número, una letra mayúscula, una letra minúscula y un carácter especial");
            return false;
        } else {
            textFieldPassword.setError(null);
            return true;
        }
    }

    // Función para validar la confirmación de la contraseña
    private boolean validateConfirmPassword() {
        String password = textFieldPassword.getEditText().getText().toString().trim();
        String confirmPassword = textFieldConfirmPassword.getEditText().getText().toString().trim();
        if (!password.equals(confirmPassword)) {
            textFieldConfirmPassword.setError("Las contraseñas no coinciden");
            return false;
        } else {
            textFieldConfirmPassword.setError(null);
            return true;
        }
    }

    // AsyncTask para enviar los datos al servidor PHP en un hilo secundario
    private class InsertarDatosTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String nombre = params[0];
            String apellido = params[1];
            String numeroSeguroSocial = params[2];
            String email = params[3];
            String contrasena = params[4];

            // URL del archivo PHP en tu servidor
            String urlServidor = "http://10.0.2.2:8080/conexiondevelop/registro.php";

            try {
                // Crea la conexión HTTP
                URL url = new URL(urlServidor);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Configura la conexión
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                // Parámetros que enviarás al servidor PHP
                String parametros = "nombre=" + URLEncoder.encode(nombre, "UTF-8") +
                        "&apellido=" + URLEncoder.encode(apellido, "UTF-8") +
                        "&numeroSeguroSocial=" + URLEncoder.encode(numeroSeguroSocial, "UTF-8") +
                        "&email=" + URLEncoder.encode(email, "UTF-8") +
                        "&contrasena=" + URLEncoder.encode(contrasena, "UTF-8");

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
                Log.e(TAG, "Error al enviar datos al servidor: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                // Muestra un mensaje dependiendo de la respuesta del servidor
                if (response.equals("Success")) {
                    Toast.makeText(Sign_inActivity.this, "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show();
                    // Redirige al usuario a LoginActivity
                    Intent intent = new Intent(Sign_inActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // Termina la actividad actual para que el usuario no pueda volver atrás
                } else {
                    Toast.makeText(Sign_inActivity.this, "Error al crear la cuenta", Toast.LENGTH_SHORT).show();


                }
            } else {
                Toast.makeText(Sign_inActivity.this, "Error al crear la cuenta", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void goToLoginActivity(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}

