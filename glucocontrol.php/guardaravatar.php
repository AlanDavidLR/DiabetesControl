<?php
include 'conexion.php';

// Variables para almacenar los datos del formulario
$idUsuario = $_POST['idUsuario'] ?? '';
$imagen = $_POST['imagen'] ?? '';

$response = array();

// Log de los datos recibidos
error_log("Datos recibidos: idUsuario=$idUsuario, imagen_length=" . strlen($imagen));

if (!empty($idUsuario) && !empty($imagen)) {
    // Decodificar la imagen de Base64
    $imagenBinaria = base64_decode($imagen);

    if ($imagenBinaria === false) {
        $response["status"] = "fail";
        $response["message"] = "Error al decodificar la imagen";
        error_log("Error al decodificar la imagen");
    } else {
        // Query para actualizar el campo avatar en la tabla "Registro"
        $sql = "UPDATE registro SET avatar = ? WHERE PacienteID = ?";

        // Preparar la consulta
        $stmt = $conn->prepare($sql);
        if ($stmt === false) {
            $response["status"] = "fail";
            $response["message"] = "Error al preparar la consulta: " . $conn->error;
            error_log("Error al preparar la consulta: " . $conn->error);
        } else {
            // Enviar los parámetros correctos
            $null = NULL; // Esto es necesario para enviar los datos binarios correctamente
            $stmt->bind_param("bi", $imagenBinaria, $idUsuario);

            if ($stmt->send_long_data(0, $imagenBinaria)) {
                if ($stmt->execute()) {
                    $response["status"] = "success";
                    $response["message"] = "Avatar guardado exitosamente";
                    error_log("Avatar guardado exitosamente");
                } else {
                    $response["status"] = "fail";
                    $response["message"] = "Error al guardar el avatar: " . $stmt->error;
                    error_log("Error al guardar el avatar: " . $stmt->error);
                }
            } else {
                $response["status"] = "fail";
                $response["message"] = "Error al enviar datos binarios";
                error_log("Error al enviar datos binarios");
            }

            // Cerrar la consulta
            $stmt->close();
        }
    }
} else {
    $response["status"] = "fail";
    $response["message"] = "Datos incompletos";
    error_log("Datos incompletos: idUsuario=$idUsuario, imagen_length=" . strlen($imagen));
}

// Cerrar la conexión
$conn->close();

// Enviar la respuesta en formato JSON
header('Content-Type: application/json');
echo json_encode($response);
?>
