<?php
include 'conexion.php';

// Variables para almacenar los datos del formulario
$hora = $_POST['hora'] ?? '';
$fecha = $_POST['fecha'] ?? '';
$tipoToma = $_POST['tipoToma'] ?? '';
$pacienteID = $_POST['pacienteID'] ?? '';
$nivelGlucosa = $_POST['nivelGlucosa'] ?? ''; // Recibe el valor de NivelGlucosa desde la aplicación Android

// Query para insertar los datos en la tabla "RegistroGlucosa"
$sql = "INSERT INTO registroglucosa (PacienteID, Hora, Fecha, TipoToma, NivelGlucosa) 
        VALUES ('$pacienteID', '$hora', '$fecha', '$tipoToma', '$nivelGlucosa')";

header('Content-Type: application/json');

if ($conn->query($sql) === TRUE) {
    echo json_encode(["status" => "Success"]);
} else {
    echo json_encode(["status" => "error", "message" => "Error al insertar el registro de glucosa: " . $conn->error]);
}

$conn->close();
?>

