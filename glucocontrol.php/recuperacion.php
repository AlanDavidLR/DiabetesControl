<?php
include 'conexion.php';

$email_recuperacion = $_POST['email'] ?? '';

// Respuesta por defecto
$response = array("status" => "fail", "message" => "error");

try {
    // Consulta para verificar si el correo electrónico existe en la base de datos
    $stmt = $conn->prepare("SELECT * FROM registro WHERE Email = ?");
    if (!$stmt) {
        throw new Exception($conn->error);
    }
    $stmt->bind_param("s", $email_recuperacion);
    $stmt->execute();
    $result = $stmt->get_result();

    // Verificar si se encontró algún resultado
    if ($result->num_rows > 0) {
        $response["status"] = "success";
        $response["message"] = "existe";
    } else {
        $response["message"] = "no_existe";
    }
    $stmt->close();
} catch (Exception $e) {
    error_log("Error en la consulta: " . $e->getMessage());
    $response["message"] = "error_db";
}

$conn->close();
echo json_encode($response);
?>
