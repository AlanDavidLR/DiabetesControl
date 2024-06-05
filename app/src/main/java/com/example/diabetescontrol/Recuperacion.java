package com.example.diabetescontrol;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import android.widget.ImageButton;
import android.content.Intent;
import org.json.JSONObject;
import org.json.JSONException;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class Recuperacion extends AppCompatActivity {

    private TextInputLayout textFieldEmailR;
    private ImageButton btnRegresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperacion);

        textFieldEmailR = findViewById(R.id.textFieldEmailR);
        Button btnEnviar = findViewById(R.id.btnEnviar);
        ImageButton btnRegresar = findViewById(R.id.btnRegresar);

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
        if (!email.isEmpty()) {
            String subject = "Recuperación de contraseña";
            String message = "Para recuperar tu contraseña da click en el siguiente enlace.";

            new VerifyEmailTask().execute(email, subject, message);
        } else {
            Toast.makeText(this, "Por favor ingresa un correo electrónico", Toast.LENGTH_SHORT).show();
        }
    }

    private void enviarCorreo(String email, String subject, String message) {
        final String username = "imssglucocontrol@zohomail.com"; // Tu dirección de correo electrónico de Zoho Mail
        final String password = "Gluc0contrl75$4fY"; // Tu contraseña

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.zoho.com"); // Servidor SMTP de Zoho Mail
        props.put("mail.smtp.port", "587"); // Puerto SMTP de Zoho Mail

        Session session = Session.getInstance(props,
                new Authenticator() {
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

            // Esta operación de envío de correo se realiza en un hilo secundario
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
            String subject = params[1];
            String message = params[2];

            HttpURLConnection connection = null;
            try {
                URL url = new URL("http://glucocontrol.atwebpages.com/recuperacion.php");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                String postData = "email=" + URLEncoder.encode(email, "UTF-8");
                OutputStream os = connection.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                int responseCode = connection.getResponseCode();
                Log.d("VerifyEmailTask", "Response Code: " + responseCode);

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                Log.d("VerifyEmailTask", "Response: " + response.toString());
                return response.toString();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("VerifyEmailTask", "Error: " + e.getMessage());
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                Log.d("VerifyEmailTask", "Response not null");
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String status = jsonResponse.optString("status");
                    String message = jsonResponse.optString("message");
                    if ("success".equals(status) && "existe".equals(message)) {
                        String email = textFieldEmailR.getEditText().getText().toString().trim();
                        String subject = "Recuperación de contraseña";
                        String emailMessage = "Para recuperar tu contraseña da click en el siguiente enlace. http://glucocontrol.atwebpages.com/";
                        enviarCorreo(email, subject, emailMessage);
                    } else if ("fail".equals(status) && "no_existe".equals(message)) {
                        Toast.makeText(Recuperacion.this, "Ese correo no está registrado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Recuperacion.this, "Error en la conexión", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Recuperacion.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("VerifyEmailTask", "Response is null");
                Toast.makeText(Recuperacion.this, "Error en la conexión", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

