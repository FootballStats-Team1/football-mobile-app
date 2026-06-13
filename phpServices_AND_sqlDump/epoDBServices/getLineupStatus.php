<?php
// getLineupStatus.php
// Καλείται ως: getLineupStatus.php?matchId=121
// Επιστρέφει για κάθε παίκτη της ενδεκάδας/πάγκου ένα χρώμα:
//   red    -> έχει κόκκινη κάρτα
//   green  -> έμπλεξε σε αλλαγή (is_on_pitch != is_starting)
//   yellow -> έχει κίτρινη κάρτα
//   ""     -> κανονικός (default χρώμα)
// Προτεραιότητα: red > green > yellow

header('Content-Type: application/json; charset=utf-8');
$conn = new mysqli("localhost", "root", "", "epodb");
$conn->set_charset("utf8mb4");
if ($conn->connect_error) { die(json_encode([])); }

$matchId = isset($_GET['matchId']) ? intval($_GET['matchId']) : 0;
if ($matchId <= 0) { die(json_encode([])); }

$sql = "
    SELECT p.name                       AS name,
           ml.is_starting               AS is_starting,
           ml.is_on_pitch               AS is_on_pitch,
           COALESCE(e.yellow_cards, 0)  AS yellow_cards,
           COALESCE(e.red_cards, 0)     AS red_cards
    FROM match_lineups ml
    JOIN players p        ON ml.player_id = p.id
    LEFT JOIN match_events e ON e.match_id = ml.match_id AND e.player_id = ml.player_id
    WHERE ml.match_id = ?
";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $matchId);
$stmt->execute();
$res = $stmt->get_result();

$out = [];
while ($row = $res->fetch_assoc()) {
    $red    = intval($row['red_cards']) > 0;
    $yellow = intval($row['yellow_cards']) > 0;
    $subbed = intval($row['is_on_pitch']) != intval($row['is_starting']);

    $color = "";
    if ($red)         $color = "red";
    elseif ($subbed)  $color = "green";
    elseif ($yellow)  $color = "yellow";

    $out[] = ["name" => $row['name'], "color" => $color];
}

echo json_encode($out, JSON_UNESCAPED_UNICODE);
$stmt->close();
$conn->close();
?>