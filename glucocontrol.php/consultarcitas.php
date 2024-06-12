<?php
include 'conexion.php';

header('Content-Type: application/json; charset=utf-8');

// Obtener los parámetros enviados desde la aplicación Android
$idUsuario = $_POST['idUsuario'];
$fechaConsulta = $_POST['fechaConsulta'];

// Log de los parámetros recibidos
error_log("Parámetros recibidos - idUsuario: $idUsuario, fechaConsulta: $fechaConsulta");

// Consulta SQL para obtener los registros de consulta posteriores a la fecha especificada y para el usuario específico
$sql = "SELECT TipoConsulta, Fecha, Hora, Nota 
        FROM registroconsulta 
        WHERE PacienteID = $idUsuario 
        AND Fecha >= '$fechaConsulta'";

// Log de la consulta SQL generada
error_log("Consulta SQL generada: $sql");

// Ejecutar la consulta
$result = $conn->query($sql);

// Array para almacenar los resultados
$response = array();

if ($result->num_rows > 0) {
    // Iterar sobre los resultados y añadirlos al array de respuesta
    while ($row = $result->fetch_assoc()) {
        // Log de los datos obtenidos de la fila
        error_log("Datos obtenidos de la fila - TipoConsulta: {$row['TipoConsulta']}, Fecha: {$row['Fecha']}, Hora: {$row['Hora']}, Nota: {$row['Nota']}");

        // Verificar si las claves "TipoConsulta", "Fecha", "Hora" y "Nota" están definidas en el array $row
        if (isset($row['TipoConsulta']) && isset($row['Fecha']) && isset($row['Hora']) && isset($row['Nota'])) {
            $registroConsulta = array(
                'tipoConsulta' => $row['TipoConsulta'],
                'fecha' => $row['Fecha'],
                'hora' => $row['Hora'],
                'nota' => $row['Nota']
            );
            array_push($response, $registroConsulta);
        } else {
            // Si una o más claves no están definidas, manejar el error aquí o simplemente omitir el registro
            // Aquí, simplemente omitiremos el registro
            continue;
        }
    }
    // Log de la respuesta generada
    error_log("Respuesta generada: " . json_encode($response));

    // Codificar la respuesta como JSON y enviarla de vuelta a la aplicación Android
    echo json_encode($response);
} else {
    // Si no hay resultados, env// Si no hay resultados, enviar un array vacío codificado en JSON
    echo json_encode(array());
}

// Cerrar la conexión a la base de datos
$conn->close();
?>

