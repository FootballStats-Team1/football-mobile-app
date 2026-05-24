<?php
header('Content-Type: application/json; charset=utf-8');
$conn = new mysqli("localhost", "root", "", "epodb");
$conn->set_charset("utf8mb4");

$matchId = isset($_GET['matchId']) ? intval($_GET['matchId']) : 0;

$sql = "
    SELECT p.name, p.team_id, ml.is_starting, m.home_team_id, m.away_team_id
    FROM match_lineups ml
    JOIN players p ON ml.player_id = p.id
    JOIN matches m ON ml.match_id = m.id
    WHERE ml.match_id = ?
    ORDER BY p.name ASC
";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $matchId);
$stmt->execute();
$result = $stmt->get_result();

$home_starters = [];
$away_starters = [];
$home_subs = [];
$away_subs = [];

while ($row = $result->fetch_assoc()) {
    if ($row['team_id'] == $row['home_team_id']) {
        if ($row['is_starting'] == 1) $home_starters[] = $row['name'];
        else $home_subs[] = $row['name'];
    } else {
        if ($row['is_starting'] == 1) $away_starters[] = $row['name'];
        else $away_subs[] = $row['name'];
    }
}

echo json_encode([
    "home_starters" => $home_starters,
    "away_starters" => $away_starters,
    "home_subs" => $home_subs,
    "away_subs" => $away_subs
], JSON_UNESCAPED_UNICODE);
$conn->close();
?>