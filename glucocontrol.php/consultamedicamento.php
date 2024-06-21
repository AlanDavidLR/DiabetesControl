<?php
include 'conexion.php';

header('Content-Type: application/json; charset=utf-8');

// Obtener los parámetros enviados desde la aplicación Android
$idUsuario = $_POST['idUsuario'];

// Log de los parámetros recibidos
error_log("Parámetros recibidos - idUsuario: $idUsuario");

// Consulta SQL para obtener los registros de medicamentos para el usuario específico
$sql = "SELECT NombreMedicamento, DiasToma, Hora, Dosis, TipoMedicamento 
        FROM RegistroMedicamentos 
        WHERE PacienteID = $idUsuario";

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
        error_log("Datos obtenidos de la fila - NombreMedicamento: {$row['NombreMedicamento']}, DiasToma: {$row['DiasToma']}, Hora: {$row['Hora']}, Dosis: {$row['Dosis']}, TipoMedicamento: {$row['TipoMedicamento']}");

        // Verificar si las claves "NombreMedicamento", "DiasToma", "Hora", "Dosis" y "TipoMedicamento" están definidas en el array $row
        if (isset($row['NombreMedicamento']) && isset($row['DiasToma']) && isset($row['Hora']) && isset($row['Dosis']) && isset($row['TipoMedicamento'])) {
            $registroMedicamento = array(
                'nombreMedicamento' => $row['NombreMedicamento'],
                'diasToma' => $row['DiasToma'],
                'hora' => $row['Hora'],
                'dosis' => $row['Dosis'],
                'tipoMedicamento' => $row['TipoMedicamento']
            );
            array_push($response, $registroMedicamento);
        } else {
            // Si una o más claves no están definidas, manejar el error aquí o simplemente omitir el registro
            // Aquí, simplemente omitiremos el registro
            continue;
        }
    }
    // Log de la respuesta generada
    error_log("Respuesta generada: ". json_encode($response));

    // Codificar la respuesta como JSON y enviarla de vuelta a la aplicación Android
    echo json_encode($response);
} else {
    // Si no hay resultados, enviar un array vacío codificado en JSON
    echo json_encode(array());
}

// Cerrar la conexión a la base de datos
$conn->close();
?>
