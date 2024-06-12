<?php
include 'conexion.php';

// Ruta del archivo de logs
$log_file = 'login.log';

// Verificar que los parámetros POST no estén vacíos
if (empty($_POST['email']) || empty($_POST['password'])) {
    $response = array('success' => false, 'message' => 'Faltan parámetros');
    log_message("Faltan parámetros en la solicitud: " . json_encode($_POST), $log_file);
    die(json_encode($response));
}

// Variables para almacenar las credenciales del formulario de inicio de sesión
$email_login = $_POST['email'];
$contrasena_login = $_POST['password'];

// Consulta para verificar las credenciales de inicio de sesión
$stmt = $conn->prepare("SELECT PacienteID, Nombre, Apellidos, NumeroSeguroSocial, Email, Avatar FROM registro WHERE Email = ? AND Contrasena = ?");
if ($stmt === false) {
    $error_message = "Error al preparar la consulta: " . $conn->error;
    log_message($error_message, $log_file);
    die(json_encode(array('success' => false, 'message' => 'Error interno del servidor')));
}
$stmt->bind_param("ss", $email_login, $contrasena_login);
$stmt->execute();
$result = $stmt->get_result();

if ($result === false) {
    $error_message = "Error al ejecutar la consulta: " . $stmt->error;
    log_message($error_message, $log_file);
    die(json_encode(array('success' => false, 'message' => 'Error interno del servidor')));
}

if ($result->num_rows === 1) {
    // Las credenciales son válidas, almacenar los datos del usuario en variables
    $row = $result->fetch_assoc();
    $id_usuario = $row['PacienteID'];
    $nombre_usuario = $row['Nombre'];
    $apellidos_usuario = $row['Apellidos'];
    $numeroSeguroSocial_usuario = $row['NumeroSeguroSocial'];
    $email_usuario = $row['Email'];

    // Verificar si el campo avatar está vacío o no
    $avatar_usuario = !empty($row['Avatar']) ? base64_encode($row['Avatar']) : null;

    // Enviar los datos del usuario como respuesta
    $response = array(
        'success' => true,
        'id_usuario' => $id_usuario,
        'nombre_usuario' => $nombre_usuario,
        'apellidos_usuario' => $apellidos_usuario,
        'numeroSeguroSocial_usuario' => $numeroSeguroSocial_usuario,
        'email_usuario' => $email_usuario,
        'avatar_usuario' => $avatar_usuario // Nuevo campo
    );
    log_message("Inicio de sesión exitoso para el usuario: $email_login", $log_file);
    log_message("Avatar enviado desde el PHP: " . $avatar_usuario, $log_file);
    echo json_encode($response);
} else {
    // Las credenciales son inválidas
    $response = array(
        'success' => false,
        'message' => 'Credenciales de inicio de sesión inválidas'
    );
    log_message("Inicio de sesión fallido para el usuario: $email_login. Credenciales inválidas.", $log_file);
    echo json_encode($response);
}

// Cerrar conexión
$stmt->close();
$conn->close();
log_message("Conexión cerrada después del intento de inicio de sesión.", $log_file);
?>
