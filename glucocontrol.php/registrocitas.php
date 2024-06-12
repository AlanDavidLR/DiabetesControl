<?php
include 'conexion.php';

// Variables para almacenar los datos del formulario
$pacienteID = $_POST['pacienteID'] ?? '';
$tipoConsulta = $_POST['tipoConsulta'] ?? '';
$fecha = $_POST['fecha'] ?? '';
$hora = $_POST['hora'] ?? '';
$nota = $_POST['nota'] ?? '';

// Log de los datos recibidos
error_log("Datos recibidos:");
error_log("PacienteID: " . $pacienteID);
error_log("TipoConsulta: " . $tipoConsulta);
error_log("Fecha: " . $fecha);
error_log("Hora: " . $hora);
error_log("Nota: " . $nota);

// Query para insertar los datos en la tabla "RegistroConsulta"
$sql = "INSERT INTO registroconsulta (PacienteID, TipoConsulta, Fecha, Hora, Nota) 
        VALUES (?, ?, ?, ?, ?)";

// Preparar la consulta
$stmt = $conn->prepare($sql);
$stmt->bind_param("issss", $pacienteID, $tipoConsulta, $fecha, $hora, $nota);

$response = array();
if ($stmt->execute()) {
    $response["success"] = true;
    $response["message"] = "Registro de consulta exitoso";
} else {
    error_log("Error al insertar el registro de consulta: " . $stmt->error);
    $response["success"] = false;
    $response["message"] = "Error al insertar el registro de consulta: " . $stmt->error;
}

// Devolver la respuesta en formato JSON
echo json_encode($response);

// Cerrar conexión
$stmt->close();
$conn->close();
?>
