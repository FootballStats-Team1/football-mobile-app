<?php
// insertSubstitution.php
// Καταγράφει μια αλλαγή: ο player_out βγαίνει, ο player_in μπαίνει.
// Κλήση (POST): match_id, player_out_id, player_in_id
//
// Δεν αλλάζουμε το is_starting (μένει για πάντα = "ήταν βασικός").
// Αλλάζουμε ΜΟΝΟ το is_on_pitch (ποιος είναι αυτή τη στιγμή στο γήπεδο).
// Και οι δύο παίκτες έχουν ΗΔΗ γραμμή στο match_lineups (βασικοί + πάγκος).

header('Content-Type: application/json; charset=utf-8');

$conn = new mysqli("localhost", "root", "", "epodb");
$conn->set_charset("utf8mb4");
if ($conn->connect_error) {
    die(json_encode(["error" => "Connection failed"]));
}

$matchId = isset($_POST['match_id'])       ? intval($_POST['match_id'])       : 0;
$outId   = isset($_POST['player_out_id'])  ? intval($_POST['player_out_id'])  : 0;
$inId    = isset($_POST['player_in_id'])   ? intval($_POST['player_in_id'])   : 0;

if ($matchId <= 0 || $outId <= 0 || $inId <= 0) {
    die(json_encode(["error" => "Invalid parameters"]));
}

// 1. Ο παίκτης που ΒΓΑΙΝΕΙ -> φεύγει από το γήπεδο
$stmt = $conn->prepare("UPDATE match_lineups SET is_on_pitch = 0 WHERE match_id = ? AND player_id = ?");
$stmt->bind_param("ii", $matchId, $outId);
$stmt->execute();
$outAffected = $stmt->affected_rows;
$stmt->close();

// 2. Ο παίκτης που ΜΠΑΙΝΕΙ -> έρχεται στο γήπεδο
$stmt = $conn->prepare("UPDATE match_lineups SET is_on_pitch = 1 WHERE match_id = ? AND player_id = ?");
$stmt->bind_param("ii", $matchId, $inId);
$stmt->execute();
$inAffected = $stmt->affected_rows;
$stmt->close();

echo json_encode([
    "success"    => true,
    "match_id"   => $matchId,
    "player_out" => $outId,
    "player_in"  => $inId,
    // -1 σημαίνει "η τιμή ήταν ήδη ίδια", 0 σημαίνει "δεν βρέθηκε γραμμή"
    "out_rows"   => $outAffected,
    "in_rows"    => $inAffected
]);

$conn->close();
?>
