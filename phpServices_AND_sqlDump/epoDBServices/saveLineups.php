<?php
// saveLineups.php  (POST: match_id, starter_ids)
// starter_ids = comma-separated τα player_id των 22 βασικών (11 home + 11 away).
// Σβήνει τυχόν υπάρχουσες γραμμές του αγώνα και ξαναγράφει ΟΛΟΥΣ τους παίκτες
// των 2 ομάδων: is_starting=1 αν είναι στη λίστα, αλλιώς 0 (όπως τα finished matches).

header('Content-Type: application/json; charset=utf-8');
$conn = new mysqli("localhost", "root", "", "epodb");
$conn->set_charset("utf8mb4");
if ($conn->connect_error) {
    die(json_encode(["success" => false, "error" => "Connection failed"]));
}

$matchId     = isset($_POST['match_id']) ? intval($_POST['match_id']) : 0;
$startersRaw = isset($_POST['starter_ids']) ? $_POST['starter_ids'] : "";

if ($matchId <= 0) {
    die(json_encode(["success" => false, "error" => "Invalid match_id"]));
}

// "45,46,47" -> set για γρήγορο lookup
$starterIds = [];
foreach (explode(",", $startersRaw) as $sid) {
    $sid = trim($sid);
    if ($sid !== "" && ctype_digit($sid)) {
        $starterIds[(int)$sid] = true;
    }
}

// 1. Οι 2 ομάδες του αγώνα
$stmt = $conn->prepare("SELECT home_team_id, away_team_id FROM matches WHERE id = ?");
$stmt->bind_param("i", $matchId);
$stmt->execute();
$res = $stmt->get_result();
if ($res->num_rows === 0) {
    die(json_encode(["success" => false, "error" => "Match not found"]));
}
$m = $res->fetch_assoc();
$homeId = (int)$m['home_team_id'];
$awayId = (int)$m['away_team_id'];
$stmt->close();

// 2. ΟΛΟΙ οι παίκτες των 2 ομάδων
$stmt = $conn->prepare("SELECT id FROM players WHERE team_id = ? OR team_id = ?");
$stmt->bind_param("ii", $homeId, $awayId);
$stmt->execute();
$res = $stmt->get_result();
$allPlayers = [];
while ($row = $res->fetch_assoc()) {
    $allPlayers[] = (int)$row['id'];
}
$stmt->close();

// 3. Σβήνουμε τις παλιές + ξαναγράφουμε (transaction)
$conn->begin_transaction();
try {
    $del = $conn->prepare("DELETE FROM match_lineups WHERE match_id = ?");
    $del->bind_param("i", $matchId);
    $del->execute();
    $del->close();

    $ins = $conn->prepare("INSERT INTO match_lineups (match_id, player_id, is_starting) VALUES (?, ?, ?)");
    foreach ($allPlayers as $pid) {
        $isStarting = isset($starterIds[$pid]) ? 1 : 0;
        $ins->bind_param("iii", $matchId, $pid, $isStarting);
        $ins->execute();
    }
    $ins->close();

    $conn->commit();
    echo json_encode(["success" => true, "saved" => count($allPlayers)]);
} catch (Exception $e) {
    $conn->rollback();
    echo json_encode(["success" => false, "error" => $e->getMessage()]);
}

$conn->close();
?>