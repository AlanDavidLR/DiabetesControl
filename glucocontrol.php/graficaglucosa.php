<?php
include 'conexion.php';

// Obtener los parámetros enviados desde la aplicación Android
$idUsuario = $_POST['idUsuario'];
$fechaInicio = $_POST['fechaInicio'];
$fechaFin = $_POST['fechaFin'];

// Log de los parámetros recibidos
error_log("Parámetros recibidos - idUsuario: $idUsuario, fechaInicio: $fechaInicio, fechaFin: $fechaFin");

// Consulta SQL para obtener los registros de glucosa en el rango de fechas y para el usuario específico
$sql = "SELECT Fecha, NivelGlucosa FROM registroglucosa WHERE Fecha BETWEEN ? AND ? AND PacienteID = ?";

// Preparar la consulta
$stmt = $conn->prepare($sql);
$stmt->bind_param("ssi", $fechaInicio, $fechaFin, $idUsuario);

// Ejecutar la consulta
$stmt->execute();
$result = $stmt->get_result();

// Array para almacenar los resultados
$response = array();

if ($result->num_rows > 0) {
    // Iterar sobre los resultados y añadirlos al array de respuesta
    while ($row = $result->fetch_assoc()) {
        $registroGlucosa = array(
            'fecha' => $row['Fecha'],
            'glucosa' => $row['NivelGlucosa']
        );
        array_push($response, $registroGlucosa);
    }
    // Codificar la respuesta como JSON y enviarla de vuelta a la aplicación Android
    echo json_encode($response);
} else {
    // Si no hay resultados, enviar una respuesta vacía
    echo json_encode($response);
}

// Cerrar conexión
$stmt->close();
$conn->close();
?>

