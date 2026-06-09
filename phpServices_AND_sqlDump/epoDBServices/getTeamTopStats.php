<?php
header('Content-Type: application/json; charset=utf-8');

$conn = new mysqli("127.0.0.1", "root", "", "epodb");
$conn->set_charset("utf8mb4");
if ($conn->connect_error) {
    die(json_encode(["error" => "Connection failed"]));
}

$allowed = ["goals", "assists", "yellow_cards", "red_cards",
            "shots_on_target", "tackles_succ", "fouls_committed", "fouls_won"];

$stat = isset($_GET['stat']) ? $_GET['stat'] : 'goals';
if (!in_array($stat, $allowed, true)) {
    $stat = 'goals';
}

$sql = "SELECT t.id AS team_id, t.name AS team_name, t.badge AS badge,
               SUM(e.`$stat`) AS total
        FROM match_events e
        JOIN players p ON e.player_id = p.id
        JOIN teams t   ON p.team_id   = t.id
        GROUP BY t.id, t.name, t.badge
        ORDER BY total DESC";

$result = $conn->query($sql);
$data = array();

if ($result) {
    while ($row = $result->fetch_assoc()) {
        $data[] = [
            "team_id"   => (int)$row['team_id'],
            "team_name" => $row['team_name'],
            "badge"     => $row['badge'],
            "total"     => (int)$row['total']
        ];
    }
}

echo json_encode($data, JSON_UNESCAPED_UNICODE);
$conn->close();
?>