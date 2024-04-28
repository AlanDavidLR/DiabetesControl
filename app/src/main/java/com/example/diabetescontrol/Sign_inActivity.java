package com.example.diabetescontrol;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Sign_inActivity extends AppCompatActivity {

    private static final String TAG = "Sign_inActivity";

    private TextInputLayout textFieldNombre, textFieldApellido, textFieldNSS, textFieldEmail, textFieldPassword, textFieldConfirmPassword;
    private Button btnCrear;
    private DataBaseManager dbManager;

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

        // Inicializar DatabaseManager
        dbManager = new DataBaseManager();

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

                // Validar que los campos no estén vacíos
                if (nombre.isEmpty() || apellido.isEmpty() || nssString.isEmpty() || email.isEmpty() || contrasena.isEmpty() || confirmarContrasena.isEmpty()) {
                    Toast.makeText(Sign_inActivity.this, "Todos los campos son requeridos", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validar que las contraseñas coincidan
                if (!contrasena.equals(confirmarContrasena)) {
                    Toast.makeText(Sign_inActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Convertir el número de seguro social a entero
                int nss = Integer.parseInt(nssString);

                // Insertar los datos en la tabla Registro
                if (insertarRegistro(nombre, apellido, nss, email, contrasena)) {
                    Toast.makeText(Sign_inActivity.this, "Cuenta creada exitosamente", Toast.LENGTH_SHORT).show();
                    // Aquí puedes redirigir al usuario a otra actividad si lo deseas
                } else {
                    Toast.makeText(Sign_inActivity.this, "Error al crear la cuenta", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Método para insertar un registro en la tabla Registro
    private boolean insertarRegistro(String nombre, String apellido, int numeroSeguroSocial, String email, String contrasena) {
        // Prueba de conexión
        if (!testConnection()) {
            Toast.makeText(Sign_inActivity.this, "Error al conectar con la base de datos", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Obtener una conexión a la base de datos
        try (Connection connection = dbManager.conectar()) {
            // Crear la sentencia SQL para la inserción
            String sql = "INSERT INTO Registro (Nombre, Apellidos, NumeroSeguroSocial, Email, Contrasena) VALUES (?, ?, ?, ?, ?)";

            // Crear un PreparedStatement para ejecutar la sentencia SQL
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // Establecer los parámetros de la sentencia SQL
                statement.setString(1, nombre);
                statement.setString(2, apellido);
                statement.setInt(3, numeroSeguroSocial);
                statement.setString(4, email);
                statement.setString(5, contrasena);

                // Ejecutar la sentencia SQL y obtener el número de filas afectadas
                int rowsAffected = statement.executeUpdate();

                // Retornar true si se insertó correctamente, de lo contrario false
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "Error al insertar registro en la base de datos: " + e.getMessage());
            return false;
        }
    }

    // Método para probar la conexión
    private boolean testConnection() {
        try (Connection connection = dbManager.conectar()) {
            if (connection != null && !connection.isClosed()) {
                return true; // La conexión es exitosa
            } else {
                return false; // La conexión no se pudo establecer correctamente
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "Error al intentar establecer la conexión: " + e.getMessage());
            return false; // Ocurrió un error al intentar establecer la conexión
        }
    }
}
