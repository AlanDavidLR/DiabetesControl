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
import com.google.android.material.textfield.TextInputLayout;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import android.widget.ImageButton;
import android.content.Intent;
import org.json.JSONObject;
import android.widget.ImageButton;

import org.json.JSONException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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
        final String username = ""; // Tu dirección de correo electrónico de Microsoft
        final String password = ""; // Tu contraseña

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp-mail.outlook.com"); // Servidor SMTP de Microsoft
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
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
            // Tu código para verificar el correo en la base de datos iría aquí
            // Esto es solo un ejemplo
            return "existe";
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            if (response != null) {
                if (response.equals("existe")) {
                    String email = textFieldEmailR.getEditText().getText().toString().trim();
                    String subject = "Recuperación de contraseña";
                    String emailMessage = "Para recuperar tu contraseña da click en el siguiente enlace.";
                    enviarCorreo(email, subject, emailMessage);
                } else if (response.equals("no_existe")) {
                    Toast.makeText(Recuperacion.this, "Ese correo no está registrado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Recuperacion.this, "Error en la conexión", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Recuperacion.this, "Error en la conexión", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
