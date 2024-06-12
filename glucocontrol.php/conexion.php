<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

$servername = ""; // Nombre del servidor
$username = ""; // Nombre de usuario de MySQL
$password = ""; // Contraseña de MySQL
$database = ""; // Nombre de la base de datos

// Ruta del archivo de logs
$log_file = 'db_connection.log';

// Función para registrar logs
function log_message($message, $file) {
    error_log(date('[Y-m-d H:i:s] ') . $message . "\n", 3, $file);
}

// Crear la conexión
$conn = new mysqli($servername, $username, $password, $database);

// Verificar la conexión
if ($conn->connect_error) {
    $error_message = "Conexión fallida: " . $conn->connect_error;
    log_message($error_message, $log_file);
    die(json_encode(array("status" => "error", "message" => $error_message)));
} else {
    log_message("Conexión exitosa a la base de datos.", $log_file);
    // Solo imprime el JSON si este archivo se ejecuta directamente
    if (basename(__FILE__) == basename($_SERVER["SCRIPT_FILENAME"])) {
        echo json_encode(array("status" => "success", "message" => "Conexión exitosa"));
    }
}
?>
