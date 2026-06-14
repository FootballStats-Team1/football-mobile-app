<?php
header('Content-Type: application/json; charset=utf-8');
$conn = new mysqli("localhost", "root", "", "epodb");
$conn->set_charset("utf8mb4");
if ($conn->connect_error) {
    die(json_encode(["error" => "Connection failed"]));
}

// 1. Δεδομένα από το Android App
$match_id   = isset($_POST['match_id'])   ? intval($_POST['match_id'])  : 0;
$player_id  = isset($_POST['player_id'])  ? intval($_POST['player_id']) : 0;
$team_type  = isset($_POST['team_type'])  ? $_POST['team_type']  : ''; // "home" ή "away"
$event_type = isset($_POST['event_type']) ? $_POST['event_type'] : '';
$value_str  = isset($_POST['value'])      ? $_POST['value']      : '';

// 2. "+1" / "-1" -> νούμερο
$val = 0;
if ($value_str === '+1') $val = 1;
elseif ($value_str === '-1') $val = -1;

// Whitelist στηλών του match_events (ασφάλεια, αφού μπαίνει στο SQL)
$allowed_events = [
    'goals', 'shots_on_target', 'shots_off_target',
    'passes_succ', 'passes_fail', 'tackles_succ', 'tackles_fail',
    'crosses_succ', 'crosses_fail', 'assists', 'errors',
    'fouls_won', 'fouls_committed', 'yellow_cards', 'red_cards'
];

if ($match_id > 0 && $val != 0) {

    // --- ΠΕΡΙΠΤΩΣΗ Α: Κόρνερ (πάνε στον πίνακα matches) ---
    if ($event_type === 'corners_won') {
        $column = ($team_type === 'home') ? 'home_corners' : 'away_corners';
        $sql = "UPDATE matches SET $column = GREATEST(0, $column + ?) WHERE id = ?";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("ii", $val, $match_id);
        $stmt->execute();
        $stmt->close();
        echo json_encode(["status" => "success", "msg" => "Corner updated"]);
        exit;
    }

    // --- ΠΕΡΙΠΤΩΣΗ Β: Ατομικά στατιστικά (πάνε στον πίνακα match_events) ---
    if ($player_id > 0 && in_array($event_type, $allowed_events)) {

        // INSERT ... ON DUPLICATE KEY UPDATE:
        // Αν ΔΕΝ υπάρχει γραμμή για (match_id, player_id) -> τη φτιάχνει με την αρχική τιμή.
        // Αν υπάρχει -> αυξομειώνει την υπάρχουσα τιμή.
        // Χρειάζεται UNIQUE KEY (match_id, player_id) στον πίνακα match_events.
        $sql = "INSERT INTO match_events (match_id, player_id, $event_type)
                VALUES (?, ?, GREATEST(0, ?))
                ON DUPLICATE KEY UPDATE $event_type = GREATEST(0, $event_type + ?)";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("iiii", $match_id, $player_id, $val, $val);
        $stmt->execute();
        $stmt->close();

        // Αν είναι Γκολ, ενημερώνουμε ΚΑΙ το συνολικό σκορ του αγώνα
        if ($event_type === 'goals') {
            $score_col = ($team_type === 'home') ? 'home_score' : 'away_score';
            $sql2 = "UPDATE matches SET $score_col = GREATEST(0, $score_col + ?) WHERE id = ?";
            $stmt2 = $conn->prepare($sql2);
            $stmt2->bind_param("ii", $val, $match_id);
            $stmt2->execute();
            $stmt2->close();
        }

        echo json_encode(["status" => "success"]);
    } else {
        echo json_encode(["error" => "Invalid player_id or event_type"]);
    }

} else {
    echo json_encode(["error" => "Invalid parameters"]);
}

$conn->close();
?>