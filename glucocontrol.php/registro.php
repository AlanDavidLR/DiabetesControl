<?php
include 'conexion.php';

// Variables para almacenar los datos del formulario
$nombre = $_POST['nombre'] ?? '';
$apellidos = $_POST['apellido'] ?? '';
$numeroSeguroSocial = $_POST['numeroSeguroSocial'] ?? '';
$email = $_POST['email'] ?? '';
$contrasena = $_POST['contrasena'] ?? '';

// Query para insertar los datos en la tabla "Registro"
$sql = "INSERT INTO registro (Nombre, Apellidos, NumeroSeguroSocial, Email, Contrasena) 
        VALUES (?, ?, ?, ?, ?)";

// Preparar la consulta
$stmt = $conn->prepare($sql);
$stmt->bind_param("sssss", $nombre, $apellidos, $numeroSeguroSocial, $email, $contrasena);

$response = array();
if ($stmt->execute()) {
    $response["status"] = "success";
    $response["message"] = "Registro exitoso";
} else {
    $response["status"] = "fail";
    $response["message"] = "Error al insertar el registro: " . $stmt->error;
}

// Devolver la respuesta en formato JSON
echo json_encode($response);

// Cerrar conexión
$stmt->close();
$conn->close();
?>
