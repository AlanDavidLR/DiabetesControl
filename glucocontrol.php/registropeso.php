<?php
include 'conexion.php';

// Variables para almacenar los datos del formulario
$peso = $_POST['peso'] ?? '';
$fecha = $_POST['fecha'] ?? '';
$pacienteID = $_POST['pacienteID'] ?? '';

// Query para insertar los datos en la tabla "RegistroPeso"
$sql = "INSERT INTO registropeso (PacienteID, Peso, Fecha) 
        VALUES (?, ?, ?)";

// Preparar la consulta
$stmt = $conn->prepare($sql);
$stmt->bind_param("iss", $pacienteID, $peso, $fecha);

$response = array();
if ($stmt->execute()) {
    $response["status"] = "success";
    $response["message"] = "Registro de peso exitoso";
} else {
    $response["status"] = "fail";
    $response["message"] = "Error al insertar el registro de peso: " . $stmt->error;
}

// Devolver la respuesta en formato JSON
echo json_encode($response);

// Cerrar conexión
$stmt->close();
$conn->close();
?>

