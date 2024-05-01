package com.example.diabetescontrol;

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
import android.content.Intent;


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
                // Obtener los valores de los campos de texto
                String nombre = textFieldNombre.getEditText().getText().toString().trim();
                String apellido = textFieldApellido.getEditText().getText().toString().trim();
                String nssString = textFieldNSS.getEditText().getText().toString().trim();
                String email = textFieldEmail.getEditText().getText().toString().trim();
                String contrasena = textFieldPassword.getEditText().getText().toString().trim();
                String confirmarContrasena = textFieldConfirmPassword.getEditText().getText().toString().trim();

                // Verificar que no haya campos vacíos
                if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(apellido) || TextUtils.isEmpty(nssString)
                        || TextUtils.isEmpty(email) || TextUtils.isEmpty(contrasena) || TextUtils.isEmpty(confirmarContrasena)) {
                    Toast.makeText(Sign_inActivity.this, "No puede dejar ningún campo vacío", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Verificar campos vacíos individuales
                if (TextUtils.isEmpty(nombre)) {
                    Toast.makeText(Sign_inActivity.this, "El campo Nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(apellido)) {
                    Toast.makeText(Sign_inActivity.this, "El campo Apellido no puede estar vacío", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(nssString)) {
                    Toast.makeText(Sign_inActivity.this, "El campo NSS no puede estar vacío", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Sign_inActivity.this, "El campo Email no puede estar vacío", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(contrasena)) {
                    Toast.makeText(Sign_inActivity.this, "El campo Contraseña no puede estar vacío", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(confirmarContrasena)) {
                    Toast.makeText(Sign_inActivity.this, "El campo Confirmar Contraseña no puede estar vacío", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validar que las contraseñas coincidan
                if (!contrasena.equals(confirmarContrasena)) {
                    Toast.makeText(Sign_inActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Convertir el número de seguro social a entero
                long nss = Long.parseLong(nssString);

                // Insertar los datos en la base de datos a través de PHP
                new InsertarDatosTask().execute(nombre, apellido, String.valueOf(nss), email, contrasena);
            }
        });
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
                    Toast.makeText(Sign_inActivity.this, "Error al crear la cuenta", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(Sign_inActivity.this, "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show();
                    // Redirige al usuario a LoginActivity
                    Intent intent = new Intent(Sign_inActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // Termina la actividad actual para que el usuario no pueda volver atrás

                }
            } else {
                Toast.makeText(Sign_inActivity.this, "Error al crear la cuenta", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
