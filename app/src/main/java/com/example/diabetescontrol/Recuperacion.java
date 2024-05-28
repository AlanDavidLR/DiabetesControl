package com.example.diabetescontrol;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import android.widget.TextView;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.OutputStream;
import java.io.IOException;
import com.google.android.material.textfield.TextInputLayout;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import android.widget.ImageButton;
import android.content.Intent;


public class Recuperacion extends AppCompatActivity {

    private TextInputLayout textFieldEmailR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperacion);

        textFieldEmailR = findViewById(R.id.textFieldEmailR);
        Button btnEnviar = findViewById(R.id.btnEnviar);
        ImageButton btnRegresar = findViewById(R.id.btnRegresar); // Variable btnRegresar definida correctamente

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarCorreo();
            }
        });

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Recuperacion.this, LoginActivity.class); // Intent definido correctamente
                startActivity(intent);
                finish();
            }
        });
    }

    private void verificarCorreo() {
        String email = textFieldEmailR.getEditText().getText().toString().trim();
        if (!email.isEmpty()) {
            String subject = "Recuperación de contraseña";
            String message = "Para recuperar tu contraseña da click en el siguiente enlace.";

            new VerifyEmailTask().execute(email, subject, message);
        } else {
            Toast.makeText(this, "Por favor ingresa un correo electrónico", Toast.LENGTH_SHORT).show();
        }
    }

    private void enviarCorreo(String email, String subject, String message) {
        Toast.makeText(this, "Correo electrónico enviado", Toast.LENGTH_SHORT).show();
    }

    private class VerifyEmailTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String email = params[0];
            String subject = params[1];
            String message = params[2];
            String urlServidor = "http://glucocontrol.atwebpages.com/recuperacion.php";

            try {
                URL url = new URL(urlServidor);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                String parametros = "email=" + URLEncoder.encode(email, "UTF-8") +
                        "&subject=" + URLEncoder.encode(subject, "UTF-8") +
                        "&message=" + URLEncoder.encode(message, "UTF-8");

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(parametros.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Leer la respuesta del servidor
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    return response.toString();
                } else {
                    return "Error en la conexión";
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Error en la conexión";
            }
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response.equals("existe")) {
                String email = textFieldEmailR.getEditText().getText().toString().trim();
                String subject = "Recuperación de contraseña";
                String message = "Para recuperar tu contraseña da click en el siguiente enlace.";
                enviarCorreo(email, subject, message);
            } else if (response.equals("no_existe")) {
                Toast.makeText(Recuperacion.this, "Ese correo no está registrado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Recuperacion.this, "Error en la conexión", Toast.LENGTH_SHORT).show();
            }
        }
    }
}