package com.example.diabetescontrol;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Ajustes extends AppCompatActivity {

    private ImageButton perfilImageButton;
    private static final int REQUEST_IMAGE_SELECT = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private SharedPreferences sharedPreferences;
    private Button guardarAvatarButton;
    private Button regresarButton;

    private static final String TAG = "AjustesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        perfilImageButton = findViewById(R.id.Perfil);
        guardarAvatarButton = findViewById(R.id.Guardaravatar);
        regresarButton = findViewById(R.id.Regresarimg);

        // Obtener referencia a SharedPreferences
        sharedPreferences = getSharedPreferences("loginPrefs", Context.MODE_PRIVATE);

        perfilImageButton.setBackgroundResource(R.drawable.user_default);

        perfilImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        guardarAvatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarAvatar();
            }
        });

        // Agregar OnClickListener para el botón de regresar
        regresarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Ajustes.this, Navegacion.class);
                startActivity(intent);
            }
        });
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_IMAGE_SELECT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_SELECT && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                perfilImageButton.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            perfilImageButton.setImageBitmap(imageBitmap);
        }
    }

    public void takePicture(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "No hay aplicaciones de cámara instaladas", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarAvatar() {
        long idUsuario = sharedPreferences.getLong("idUsuario", -1);
        if (idUsuario != -1) {
            BitmapDrawable drawable = (BitmapDrawable) perfilImageButton.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            Bitmap resizedBitmap = resizeBitmap(bitmap, 400, 400); // Redimensionar la imagen
            new GuardarAvatarTask(this, idUsuario, resizedBitmap).execute();
        } else {
            Toast.makeText(this, "Error al obtener el ID del usuario", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap resizeBitmap(Bitmap original, int width, int height) {
        return Bitmap.createScaledBitmap(original, width, height, true);
    }

    private static class GuardarAvatarTask extends AsyncTask<Void, Void, String> {
        private WeakReference<Context> contextRef;
        private long idUsuario;
        private Bitmap bitmap;

        GuardarAvatarTask(Context context, long idUsuario, Bitmap bitmap) {
            this.contextRef = new WeakReference<>(context);
            this.idUsuario = idUsuario;
            this.bitmap = bitmap;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String urlServidor = "http://glucocontrol.atwebpages.com/guardaravatar.php";

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream); // Cambia el 100 a 80 para reducir la calidad y el tamaño del archivo
            String imagenBase64 = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            try {
                // Crea la conexión HTTP
                URL url = new URL(urlServidor);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                // Configura la conexión
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                // Parámetros que se enviarán al servidor PHP
                String parametros = "idUsuario=" + idUsuario +
                        "&imagen=" + URLEncoder.encode(imagenBase64, "UTF-8");

                // Log de los parámetros enviados
                Log.d(TAG, "Parámetros enviados: " + parametros);

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
                connection.disconnect();

                // Log de la respuesta recibida
                Log.d(TAG, "Respuesta recibida: " + response.toString());

                return response.toString();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "IOException: " + e.getMessage());
                return "{\"status\":\"error\",\"message\":\"IOException: " + e.getMessage() + "\"}";
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Exception: " + e.getMessage());
                return "{\"status\":\"error\",\"message\":\"Exception: " + e.getMessage() + "\"}";
            }
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            Context context = contextRef.get();
            if (context != null) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.optString("status", "error");
                    String message = jsonObject.optString("message", "Error desconocido");
                    if (status.equals("success")) {
                        Toast.makeText(context, "Avatar guardado exitosamente, cambiara en su proximo inico de sesión", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Error al guardar el avatar: " + message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "JSONException: " + e.getMessage());
                    Toast.makeText(context, "Error al parsear la respuesta del servidor", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}


