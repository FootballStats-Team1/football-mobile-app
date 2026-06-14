<?php
// updateMatchStatus.php
// Αλλάζει το status ενός αγώνα (pending / live / finished).
// Κλήση (POST): match_id, status
// π.χ. ο Analyst πατάει "Λήξη Αγώνα" -> status = finished

header('Content-Type: application/json; charset=utf-8');

$conn = new mysqli("localhost", "root", "", "epodb");
$conn->set_charset("utf8mb4");
if ($conn->connect_error) {
    die(json_encode(["error" => "Connection failed"]));
}

$matchId = isset($_POST['match_id']) ? intval($_POST['match_id']) : 0;
$status  = isset($_POST['status']) ? $_POST['status'] : '';

// Δεχόμαστε ΜΟΝΟ τις έγκυρες τιμές του enum (αλλιώς η MySQL θα έβαζε κενό)
$allowed = ['pending', 'live', 'finished'];
if ($matchId <= 0 || !in_array($status, $allowed, true)) {
    die(json_encode(["error" => "Invalid parameters"]));
}

$stmt = $conn->prepare("UPDATE matches SET status = ? WHERE id = ?");
$stmt->bind_param("si", $status, $matchId);
$ok = $stmt->execute();

echo json_encode([
    "success"  => (bool)$ok,
    "match_id" => $matchId,
    "status"   => $status
]);

$stmt->close();
$conn->close();
?>
