package com.example.diabetescontrol;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.json.JSONObject;
public class Recuperacion extends AppCompatActivity {

    private TextInputLayout textFieldEmailR;
    private ImageButton btnRegresar;
    private static final String TAG = "Recuperacion";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperacion);

        textFieldEmailR = findViewById(R.id.textFieldEmailR);
        Button btnEnviar = findViewById(R.id.btnEnviar);
        btnRegresar = findViewById(R.id.btnRegresar);

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarCorreo();
            }
        });

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Recuperacion.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void verificarCorreo() {
        String email = textFieldEmailR.getEditText().getText().toString().trim();
        if (email.isEmpty()) {
            textFieldEmailR.setError("El campo Email no puede estar vacío.");
        } else if (!isValidEmail(email)) {
            textFieldEmailR.setError("Correo no válido, debe tener estructura de correo electrónico.");
        } else {
            textFieldEmailR.setError(null);
            String subject = "Recuperación de contraseña";
            String message = "Para recuperar tu contraseña da click en el siguiente enlace.";
            new VerifyEmailTask().execute(email, subject, message);
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

    private void enviarCorreo(String email, String subject, String message) {
        final String username = "imssglucocontrol@zohomail.com";
        final String password = "Gluc0contrl75$4fY";

        Properties props = new Properties();

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.zoho.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(username));
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(mimeMessage);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Recuperacion.this, "Correo electrónico enviado", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (MessagingException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Recuperacion.this, "Error al enviar el correo electrónico", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).start();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }


    private class VerifyEmailTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String email = params[0];
            String urlServidor = "http://glucocontrol.atwebpages.com/recuperacion.php";
            try {
                URL url = new URL(urlServidor);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                String parametros = "email=" + URLEncoder.encode(email, "UTF-8");

                // Log para ver los datos que se están enviando
                Log.d(TAG, "Enviando datos: " + parametros);

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(parametros.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Código de respuesta: " + responseCode);

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }
                bufferedReader.close();
                connection.disconnect();

                // Log para ver la respuesta recibida
                Log.d(TAG, "Respuesta recibida: " + response.toString());

                return response.toString();
            } catch (IOException e) {
                Log.e(TAG, "Error en la recuperación de la contraseña", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String status = jsonResponse.getString("status");
                    String message = jsonResponse.getString("message");

                    // Log para ver el estado y el mensaje de la respuesta
                    Log.d(TAG, "Estado: " + status + ", Mensaje: " + message);

                    if (status.equals("success") && message.equals("existe")) {
                        String email = textFieldEmailR.getEditText().getText().toString().trim();
                        String subject = "Recuperación de contraseña";
                        String emailMessage = "Para recuperar tu contraseña da click en el siguiente enlace: http://glucocontrol.atwebpages.com/.";
                        enviarCorreo(email, subject, emailMessage);
                    } else if (message.equals("no_existe")) {
                        Toast.makeText(Recuperacion.this, "Ese correo no está registrado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Recuperacion.this, "Error en la conexión", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error al procesar la respuesta JSON", e);
                    Toast.makeText(Recuperacion.this, "Error en la conexión", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Recuperacion.this, "Error en la conexión", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


